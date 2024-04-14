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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    @GetMapping("/")
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
    public ResponseEntity<StockDTO> stock(@ModelAttribute GetStockRequest request) throws JsonProcessingException {
        String stockCodeByStockName = stockService.findStockCodeByStockName(request.getStockName());
        if (stockCodeByStockName.length() < 6){
            System.out.println("stock code should be longer than 6");
            return ResponseEntity.badRequest().body(null);
        }
        Mono<String> stockNameByCode = kisService.getStockNameByCode(stockCodeByStockName); // 주식 코드로 주식 이름 조회
        Mono<String> stockInfoByCode = kisService.getStockInfoByCode(stockCodeByStockName); // 주식 코드로 주식 정보 조회
        String stockNameResponse = stockNameByCode.block();
        String stockInfoResponse = stockInfoByCode.block();

        log.info(stockNameResponse);
        log.info(stockInfoResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,String> stockMap =
                (Map<String, String>) objectMapper.readValue(stockNameResponse, Map.class).get("output");
        Map<String,String> stockInfoMap =
                (Map<String, String>) objectMapper.readValue(stockInfoResponse, Map.class).get("output");
        // 응답 Json이 두번 감싸져 있는 형태 {"output" : {"stockName" : "samsung"}} 이런식으로

        log.info(stockInfoMap.toString());

        StockDTO stockDTO = StockDTO.builder()
                .stockCode(stockMap.get("pdno")) // 주식 코드
                .stockName(stockMap.get("prdt_abrv_name")) // 주식 이름
                .stockPriceIndex(stockInfoMap.get("rprs_mrkt_kor_name")) // KOSPI200, KOSPI, KOSDAQ 등
                .price(Long.valueOf(stockInfoMap.get("stck_oprc"))) // 가격 (당일 종가)
                .theme(stockInfoMap.get("bstp_kor_isnm")) // 분야
                .status(stockInfoMap.get("iscd_stat_cls_code")) // status Code
                .build();

        stockService.save(stockDTO);
        return ResponseEntity.ok().body(stockDTO);
    }

    /**
     * 일별 주식 가격 조회
     */
    @GetMapping("/price")
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
    public ResponseEntity<StockPriceDTO> stockPrice(@ModelAttribute GetStockPriceRequest request) throws JsonProcessingException {
        String stockCodeByStockName = stockService.findStockCodeByStockName(request.getStockName());
        if (stockCodeByStockName.length() < 6){
            System.out.println("stock code should be longer than 6");
            return ResponseEntity.badRequest().body(null);
        }
        Mono<String> stockPriceByCodeAndDate = kisService.getStockPriceByCodeAndDate(stockCodeByStockName, request.getStartDate(), request.getEndDate()); // 주식 코드로 주식 정보 조회
        String stockPriceResponse = stockPriceByCodeAndDate.block();

        log.info(stockPriceResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String,String>> stockPriceJson = (List<Map<String, String>>) objectMapper.readValue(stockPriceResponse, Map.class).get("output2");

        Map<String, String> stockPriceMap = stockPriceJson.get(0);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        StockPriceDTO stockPriceDTO = StockPriceDTO.builder()
                .date(LocalDate.parse(request.getStartDate(), formatter))
                .stockId(stockService.findStockIdByStockCode(stockCodeByStockName))
                .maxPriceDay(Long.valueOf(stockPriceMap.get("stck_hgpr")))
                .minPriceDay(Long.valueOf(stockPriceMap.get("stck_lwpr")))
                .openPrice(Long.valueOf(stockPriceMap.get("stck_oprc")))
                .closePrice(Long.valueOf(stockPriceMap.get("stck_clpr")))
                .tradingVolume(Long.valueOf(stockPriceMap.get("acml_vol"))).
                build();

        stockPriceService.saveStockPrice(stockPriceDTO);
        return ResponseEntity.ok().body(stockPriceDTO);
    }
}
