package Project.quantHelper.controller;

import Project.quantHelper.dto.StockDTO;
import Project.quantHelper.dto.StockPriceDTO;
import Project.quantHelper.dto.request.GetStockPriceRequest;
import Project.quantHelper.dto.request.GetStockRequest;
import Project.quantHelper.dto.response.ErrorResponse;
import Project.quantHelper.dto.response.SuccessResponse;
import Project.quantHelper.service.KisService;
import Project.quantHelper.service.StockPriceService;
import Project.quantHelper.service.StockService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
@Slf4j
public class StockController {

    @Autowired
    private final StockService stockService;

    @Autowired
    private final KisService kisService;

    @Autowired
    private final StockPriceService stockPriceService;

    @PostMapping("/")
    @Operation(
            summary = "get stock information from KIS",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuccessResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Bad credentials",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<String> stock(@RequestBody GetStockRequest request) throws JsonProcessingException {
        StockDTO stockDTO = stockService.getStockDTOByStockName(request.getStockName());
        String stockCode = stockDTO.getStockCode();
        if (stockCode.length() < 6){
            System.out.println("stock code should be longer than 6");
            return ResponseEntity.badRequest().body(null);
        }
        Mono<String> stockNameByCode = kisService.getStockNameByCode(stockCode); // 주식 코드로 주식 이름 조회
        Mono<String> stockInfoByCode = kisService.getStockInfoByCode(stockCode); // 주식 코드로 주식 정보 조회
        String stockNameResponse = stockNameByCode.block();
        String stockInfoResponse = stockInfoByCode.block();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,String> stockMap =
                (Map<String, String>) objectMapper.readValue(stockNameResponse, Map.class).get("output");
        Map<String,String> stockInfoMap =
                (Map<String, String>) objectMapper.readValue(stockInfoResponse, Map.class).get("output");
        // 응답 Json이 두번 감싸져 있는 형태 {"output" : {"stockName" : "samsung"}} 이런식으로

        stockCode = stockMap.get("pdno");
        stockCode = stockCode.substring(Math.max(stockCode.length() - 6, 0));

        String stockName = stockMap.get("prdt_abrv_name");
        String stockPriceIndex = stockInfoMap.get("rprs_mrkt_kor_name");
        Long price = Long.valueOf(stockInfoMap.get("stck_oprc"));
        String theme = stockInfoMap.get("bstp_kor_isnm");
        String status = stockInfoMap.get("iscd_stat_cls_code");
        String corpCode = stockDTO.getCorpCode();

        int i = stockService.updateStock(stockName, stockCode, stockPriceIndex, price, theme, status, corpCode);

        return ResponseEntity.ok().body(i+"개의 row 업데이트");
    }

    /**
     * 기간별 주식 가격 조회
     */
    @PostMapping("/price")
    @Operation(
            summary = "get stock price from KIS",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful operation",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SuccessResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Bad credentials",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<ArrayList<StockPriceDTO>> stockPrice(@RequestBody GetStockPriceRequest request) throws JsonProcessingException {
        ArrayList<StockPriceDTO> stockPriceDTOs = new ArrayList<>(); // 응답 메시지에 넣을 리스트
        ArrayList<Long> prevStockPriceList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        StockDTO byStockName = stockService.getStockDTOByStockName(request.getStockName()); // 주식 이름으로 stockDTO 조회
        String stockCode = byStockName.getStockCode();
        Long stockId = stockService.findStockIdByStockCode(stockCode);

        if (stockCode.length() < 6){
            System.out.println("stock code should be longer than 6");
            return ResponseEntity.badRequest().body(null);
        }

        // String 형식으로 받은 startDate, endDate를 LocalDate 타입으로 파싱
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        LocalDate startDate = LocalDate.parse(request.getStartDate(), formatter);
        LocalDate endDate = LocalDate.parse(request.getEndDate(), formatter);

        //5/4 업데이트 - 일자별 데이터 호출 시 이미 DB에 있는 경우, KIS에서 새로 불러오는 것이 아니라 DB에 있는 값을 리턴

        // 조회하려는 모든 날짜를 dates 리스트에 저장
        ArrayList<LocalDate> dates = new ArrayList<>();

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

        if(dates.size() > 100){
            // 최대 받아올 수 있는 데이터 수는 100
            // 사실 휴장일까지 계산해서 넣어야 함 -> 휴장일 테이블 만들면 넣기
            log.error("period must be shorter than 100 days");
        }

        // 조회하려는 데이터 중에, DB에 없는 날짜가 하나라도 포함되어 있다면? -> Kis 서버에 새로 요첨
        // 조회하려는 데이터가 모두 DB에 있다면? -> DB에서 가져와서 stockPriceDTOs에 넣어줌
        boolean findFromDB = true;

        for(LocalDate date : dates){
            if(!stockPriceService.isStockPriceExistByStockIdAndDate(stockId, date)){
                // DB에 존재하지 않는다면
                findFromDB = false;
                break;
            }
        }

        if(findFromDB){
            // 조회하려는 데이터가 모두 DB에 존재
            for(LocalDate date : dates) {
                StockPriceDTO stockPriceDtoDB = stockPriceService.getStockPriceDTOByStockIdAndDate(stockId, date);
                log.info("stockId: {},Date: {} data already exists in DB. Got Data From DB", stockId, date);
                stockPriceDTOs.add(stockPriceDtoDB);
            }
        } else {
            // 조회하려는 데이터 중, DB에 존재하지 않는 날짜가 하나라도 포함되어 있다면

            // startDate ~ endDate의 주식 정보 조회
            String stockPriceResponse = kisService.getStockPriceByCodeAndDate(stockCode, request.getStartDate(), request.getEndDate()).block();

            // prevDate ~ startDate-1의 주식 정보 조회 -> 이동평균선 계산을 위해
            String prevDate = startDate.minusDays(100).format(formatter);
            String prevStockPriceResponse = kisService.getStockPriceByCodeAndDate(stockCode, prevDate, startDate.minusDays(1).format(formatter)).block();

            ArrayList<Map<String, String>> stockPriceJson = (ArrayList<Map<String,String>>) objectMapper.readValue(stockPriceResponse, Map.class).get("output2");
            ArrayList<Map<String, String>> prevStockPriceJson = (ArrayList<Map<String,String>>) objectMapper.readValue(prevStockPriceResponse, Map.class).get("output2");


            for (int i = prevStockPriceJson.size() - 1; i >= 0; i--) {
                // prevDate ~ startDate-1의 주식 가격을 prevStockPriceList에 저장
                // 날짜가 오름차순이 되도록 정렬
                Map<String, String> prevStockPriceData = prevStockPriceJson.get(i);
                prevStockPriceList.add(Long.valueOf(prevStockPriceData.get("stck_clpr")));
                log.info("date: {}, price: {}, temporaliy saved", LocalDate.parse(prevStockPriceData.get("stck_bsop_date"), formatter),Long.valueOf(prevStockPriceData.get("stck_clpr")));
            }

            for (int i = stockPriceJson.size() - 1; i >= 0; i--) {
                Map<String,String> stockPriceData = stockPriceJson.get(i);

                prevStockPriceList.add(Long.valueOf(stockPriceData.get("stck_clpr"))); // 리스트의 마지막에 추가
                log.info("date: {}, price: {}, temporaliy saved", LocalDate.parse(stockPriceData.get("stck_bsop_date"), formatter),Long.valueOf(stockPriceData.get("stck_clpr")));

                Long movingAverageLine5 = calculateMovingAverage(prevStockPriceList, 5);
                Long movingAverageLine10 = calculateMovingAverage(prevStockPriceList, 10);
                Long movingAverageLine20 = calculateMovingAverage(prevStockPriceList, 20);

                StockPriceDTO stockPriceDTO = StockPriceDTO.builder()
                        .date(LocalDate.parse(stockPriceData.get("stck_bsop_date"), formatter))
                        .stockId(stockService.findStockIdByStockCode(stockCode))
                        .maxPriceDay(Long.valueOf(stockPriceData.get("stck_hgpr")))
                        .minPriceDay(Long.valueOf(stockPriceData.get("stck_lwpr")))
                        .openPrice(Long.valueOf(stockPriceData.get("stck_oprc")))
                        .closePrice(Long.valueOf(stockPriceData.get("stck_clpr")))
                        .tradingVolume(Long.valueOf(stockPriceData.get("acml_vol")))
                        .movingAverageLine5(movingAverageLine5)
                        .movingAverageLine10(movingAverageLine10)
                        .movingAverageLine20(movingAverageLine20)
                        .build();

                stockPriceService.saveStockPrice(stockPriceDTO);
                stockPriceDTOs.add(stockPriceDTO);
            }
        }
        return ResponseEntity.ok().body(stockPriceDTOs);

    }

    public static Long calculateMovingAverage(List<Long> prices, int period) {

        if (prices.size() < period) {
            return null; // 데이터가 충분하지 않은 경우
        }

        long sum = 0;
        int count = 0;

        while(count < period){
            sum += prices.get(prices.size() - count - 1); // 뒤에서부터 가져옴
            count++;
        }
        return sum / period;
    }
}
