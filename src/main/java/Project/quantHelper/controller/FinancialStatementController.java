package Project.quantHelper.controller;

import Project.quantHelper.dto.FinancialStatementDTO;
import Project.quantHelper.dto.StockDTO;
import Project.quantHelper.dto.request.GetFinancialStatementRequest;
import Project.quantHelper.dto.response.ErrorResponse;
import Project.quantHelper.dto.response.SuccessResponse;
import Project.quantHelper.service.DartService;
import Project.quantHelper.service.FinancialStatementService;
import Project.quantHelper.service.StockService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/financial-statement")
@RequiredArgsConstructor
@Slf4j
public class FinancialStatementController {

    private final DartService dartService;

    private final StockService stockService;

    private final FinancialStatementService financialStatementService;

    @PostMapping("/")
    @Operation(
            summary = "get financial statement by stock name",
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
    public ResponseEntity<String> financialStatement(
            @RequestBody GetFinancialStatementRequest request
    ) {
        if (request.getQuarter() <= 0){
            return ResponseEntity.badRequest().body("quater should be upper zero");
        }
        String content = null;
        String majorContent = "";
        JsonParser jsonParser = new JsonParser();
        StockDTO stockDTO = stockService.getStockDTOByStockName(request.getCorpName());
        try {
            FinancialStatementDTO financialStatementDTO = financialStatementService.findFinancialStatementByStockId(stockDTO.getStockID(), request.getYear(), request.getQuarter());
            majorContent = financialStatementDTO.getContent();
        } catch (RuntimeException e) {
            // Getting major content from Dart
            majorContent = dartService.getFinancialStatementFromDart(stockDTO, request.getYear(), request.getQuarter()).block();

            // Parsing first JSON
            JsonObject jsonObject = jsonParser.parse(dartService.getStockAmountFromDart(stockDTO, request.getYear(), request.getQuarter()).block()).getAsJsonObject();
            JsonArray list = jsonObject.get("list").getAsJsonArray();

            // Creating extraContent object for second JSON
            JsonObject extraContent = new JsonObject();

            for (JsonElement element : list) {
                JsonObject obj = element.getAsJsonObject();

                // Extracting specific fields
                if (obj.get("se").getAsString().equals("합계")) {
                    extraContent.addProperty("발행 주식의 총 수", obj.get("istc_totqy").getAsString());
                    break;
                }
            }

            JsonObject alloJsonObject = jsonParser.parse(dartService.getAllocationAndEpsFromDart(stockDTO, request.getYear(), request.getQuarter()).block()).getAsJsonObject();
            JsonArray alloList = alloJsonObject.get("list").getAsJsonArray();
            for (JsonElement element : alloList) {
                JsonObject obj = element.getAsJsonObject();

                if (obj.get("se").getAsString().equals("(연결)주당순이익(원)")) {
                    extraContent.addProperty("EPS", obj.get("thstrm").getAsString());
                }

                if (obj.get("se").getAsString().equals("주당 현금배당금(원)")) {
                    if (obj.get("stock_knd").getAsString().equals("보통주")) {
                        extraContent.addProperty("보통주 주당 배당금", obj.get("thstrm").getAsString());
                    } else if (obj.get("stock_knd").getAsString().equals("우선주")) {
                        extraContent.addProperty("우선주 주당 배당금", obj.get("thstrm").getAsString());
                    }
                }
            }

            // Merging majorContent and extraContent into one JSON object
            JsonObject mergedContent = new JsonObject();
            JsonObject majorContentJson = jsonParser.parse(majorContent).getAsJsonObject();

            // Adding major content to merged JSON
            for (Map.Entry<String, JsonElement> entry : majorContentJson.entrySet()) {
                mergedContent.add(entry.getKey(), entry.getValue());
            }

            // Adding extra content to merged JSON
            for (Map.Entry<String, JsonElement> entry : extraContent.entrySet()) {
                mergedContent.add(entry.getKey(), entry.getValue());
            }

            content = mergedContent.toString();

            // Save merged content
            FinancialStatementDTO financialStatementDTO = FinancialStatementDTO.builder()
                    .stockId(stockDTO.getStockID())
                    .year(request.getYear())
                    .quarter(request.getQuarter())
                    .content(content)
                    .build();
            financialStatementService.save(financialStatementDTO);
        }
        return ResponseEntity.ok().body(content);
    }
}
