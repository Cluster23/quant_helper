package Project.quantHelper.controller;

import Project.quantHelper.dto.StockDTO;
import Project.quantHelper.dto.StockPriceDTO;
import Project.quantHelper.dto.request.GetFinancialStatementRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


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

        StockDTO byStockName = stockService.getStockDTOByStockName(request.getStockName()); // 주식 이름으로 stockDTO 조회
        String stockCode = byStockName.getStockCode();
        if (stockCode.length() < 6){
            System.out.println("stock code should be longer than 6");
            return ResponseEntity.badRequest().body(null);
        }

        // String 형식으로 받은 startDate, endDate를 LocalDate 타입으로 파싱
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        LocalDate startDate = LocalDate.parse(request.getStartDate(), formatter);
        LocalDate endDate = LocalDate.parse(request.getEndDate(), formatter);

        /*
        5/4 업데이트 - 일자별 데이터 호출 시 이미 DB에 있는 경우, KIS에서 새로 불러오는 것이 아니라 DB에 있는 값을 리턴
         */

        // startDate와 endDate가 같은 경우 -> 특정 날짜의 주식 가격 조회
        if(startDate.isEqual(endDate)){
            StockPriceDTO stockPriceDtoDB = stockPriceService.getStockPriceDTOByStockIdAndDate(stockService.findStockIdByStockCode(stockCode), startDate);

            // 이미 DB에 존재하는 경우
            if(stockPriceDtoDB != null){
                log.info("stockId: {},Date: {} data already exists in DB. Got Data From DB", stockService.findStockIdByStockCode(stockCode), startDate);
                stockPriceDTOs.add(stockPriceDtoDB);
                return ResponseEntity.ok().body(stockPriceDTOs);
            } else {
                // DB에 존재하지 않는 경우 -> KIS에서 데이터를 받아와서 DB에 저장 후, 응답 메시지에 넣음
                Mono<String> stockPriceByCodeAndDate = kisService.getStockPriceByCodeAndDate(stockCode, request.getStartDate(), request.getEndDate()); // 주식 코드로 주식 정보 조회
                String stockPriceResponse = stockPriceByCodeAndDate.block();

                ObjectMapper objectMapper = new ObjectMapper();
                List<Map<String,String>> stockPriceJson = (List<Map<String, String>>) objectMapper.readValue(stockPriceResponse, Map.class).get("output2");

                Map<String, String> stockPriceMap = stockPriceJson.get(0);

                StockPriceDTO stockPriceDTO = StockPriceDTO.builder()
                        .date(startDate)
                        .stockId(stockService.findStockIdByStockCode(stockCode))
                        .maxPriceDay(Long.valueOf(stockPriceMap.get("stck_hgpr")))
                        .minPriceDay(Long.valueOf(stockPriceMap.get("stck_lwpr")))
                        .openPrice(Long.valueOf(stockPriceMap.get("stck_oprc")))
                        .closePrice(Long.valueOf(stockPriceMap.get("stck_clpr")))
                        .tradingVolume(Long.valueOf(stockPriceMap.get("acml_vol"))).
                        build();

                stockPriceService.saveStockPrice(stockPriceDTO);
                stockPriceDTOs.add(stockPriceDTO);
                return ResponseEntity.ok().body(stockPriceDTOs);
            }
        } else { // startDate와 endDate가 다른 경우 -> 기간별 조회

            // 조회하려는 모든 날짜를 dates 리스트에 저장
            ArrayList<LocalDate> dates = new ArrayList<>();
            ArrayList<LocalDate> datesDB = new ArrayList<>(); // DB에 이미 존재하는 날짜

            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                dates.add(currentDate);
                currentDate = currentDate.plusDays(1);
            }

            if(dates.size() > 100){
                // 최대 받아올 수 있는 데이터 수는 100
                log.error("period must be shorter than 100 days");
            }

            /*
                dates를 두가지로 구분
                1. DB에 이미 존재하는 경우
                2. KIS 서버에서 새로 받아와야 하는 경우
             */
            for(LocalDate date : dates){
                // date, stockId를 통해서 StockPrice 테이블에 데이터가 이미 존재하는지 확인
                StockPriceDTO stockPriceDtoDB = stockPriceService.getStockPriceDTOByStockIdAndDate(stockService.findStockIdByStockCode(stockCode), date);

                if(stockPriceDtoDB != null){
                    // 데이터가 존재하는 경우, 해당 데이터를 stockPriceDTOs에 넣고, 날짜를 datesDB에 저장한다.
                    log.info("stockId: {},Date: {} data already exists in DB. Got Data From DB", stockService.findStockIdByStockCode(stockCode), startDate);
                    stockPriceDTOs.add(stockPriceDtoDB);
                    datesDB.add(date);
                }
            }


            Mono<String> stockPriceByCodeAndDate = kisService.getStockPriceByCodeAndDate(stockCode, request.getStartDate(), request.getEndDate()); // 주식 코드로 주식 정보 조회
            String stockPriceResponse = stockPriceByCodeAndDate.block();

            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String,String>> stockPriceJson = (List<Map<String, String>>) objectMapper.readValue(stockPriceResponse, Map.class).get("output2");

            for (Map<String, String> stockPriceData : stockPriceJson) {
                LocalDate stckBsopDate = LocalDate.parse(stockPriceData.get("stck_bsop_date"),formatter);

                // DB에 존재하지 않는 날짜를 조회하려 하는 경우 -> KIS 서버에 요청
                if (!datesDB.contains(stckBsopDate)) {
                    StockPriceDTO stockPriceDTO = StockPriceDTO.builder()
                            .date(stckBsopDate)
                            .stockId(stockService.findStockIdByStockCode(stockCode))
                            .maxPriceDay(Long.valueOf(stockPriceData.get("stck_hgpr")))
                            .minPriceDay(Long.valueOf(stockPriceData.get("stck_lwpr")))
                            .openPrice(Long.valueOf(stockPriceData.get("stck_oprc")))
                            .closePrice(Long.valueOf(stockPriceData.get("stck_clpr")))
                            .tradingVolume(Long.valueOf(stockPriceData.get("acml_vol"))).
                            build();

                    stockPriceService.saveStockPrice(stockPriceDTO);
                    stockPriceDTOs.add(stockPriceDTO);
                }
            }

            return ResponseEntity.ok().body(stockPriceDTOs);
        }

    }
}
