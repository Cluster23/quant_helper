package Project.quantHelper.controller;

import Project.quantHelper.dto.StockDTO;
import Project.quantHelper.dto.request.GetFinancialStatementRequest;
import Project.quantHelper.dto.request.GetStockRequest;
import Project.quantHelper.dto.response.ErrorResponse;
import Project.quantHelper.dto.response.SuccessResponse;
import Project.quantHelper.service.KisService;
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
    public ResponseEntity<StockDTO> stock(@ModelAttribute("PDNO") GetStockRequest request) throws JsonProcessingException {
        System.out.println(request.getStockCode());
        if (request.getStockCode().length() > 6){
            System.out.println("stock id should be smaller than 6 digits");
            return ResponseEntity.badRequest().body(null);
        }
        Mono<String> stockNameByCode = kisService.getStockNameByCode(request.getStockCode());
        Mono<String> stockInfoByCode = kisService.getStockInfoByCode(request.getStockCode());
        String stockNameResponse = stockNameByCode.block();
        String stockInfoResponse = stockInfoByCode.block();

        log.info(stockNameResponse);
        log.info(stockInfoResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,String> stockMap = (Map<String, String>) objectMapper.readValue(stockNameResponse, Map.class).get("output");
        Map<String,String> stockInfoMap = (Map<String, String>) objectMapper.readValue(stockInfoResponse, Map.class).get("output");

        log.info(stockInfoMap.toString());

        StockDTO stockDTO = StockDTO.builder()
                .stockCode(stockMap.get("pdno"))
                .stockName(stockMap.get("prdt_abrv_name"))
                .stockPriceIndex(stockInfoMap.get("rprs_mrkt_kor_name"))
                .price(Long.valueOf(stockInfoMap.get("stck_oprc")))
                .theme(stockInfoMap.get("bstp_kor_isnm"))
                .status(stockInfoMap.get("iscd_stat_cls_code"))
                .build();

        stockService.save(stockDTO);
        return ResponseEntity.ok().body(stockDTO);
    }

}
