package Project.quantHelper.controller;

import Project.quantHelper.dto.request.GetFinancialStatementRequest;
import Project.quantHelper.dto.response.ErrorResponse;
import Project.quantHelper.dto.response.SuccessResponse;
import Project.quantHelper.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    @Autowired
    private final StockService stockService;
/*
    @GetMapping("/")
    @Operation(
            summary = "get Kis Approval Key",
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
            @ModelAttribute GetFinancialStatementRequest request
    ) {
        System.out.println(request.getQuarter());
        if (request.getQuarter() <= 0){
            return ResponseEntity.badRequest().body("quater should be upper zero");
        }
        Mono<String> financialStatement = dartService.getFinancialStatementFromDart(request.getCorpName(), request.getYear(), request.getQuarter());
        System.out.println(financialStatement.block());
        return ResponseEntity.ok().body(financialStatement.block());

    }
    */
    @GetMapping("/")
    public Mono<ResponseEntity<String>> requestApi() {
        WebClient webClient = WebClient.create("https://openapi.koreainvestment.com:9443");

        return webClient.get()
                .uri("/your-endpoint") // 실제 엔드포인트로 변경해야 합니다.
                .retrieve()
                .bodyToMono(String.class)
                .map(responseBody -> ResponseEntity.ok().body(responseBody))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while fetching data from external API."));
    }


}
