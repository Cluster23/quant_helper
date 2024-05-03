package Project.quantHelper.controller;

import Project.quantHelper.dto.request.GetFinancialStatementRequest;
import Project.quantHelper.dto.response.ErrorResponse;
import Project.quantHelper.dto.response.SuccessResponse;
import Project.quantHelper.service.DartService;
import Project.quantHelper.service.FinancialStatementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/financial-statement")
@RequiredArgsConstructor
public class FinancialStatementController {

    private final DartService dartService;

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
        System.out.println(request.getQuarter());
        if (request.getQuarter() <= 0){
            return ResponseEntity.badRequest().body("quater should be upper zero");
        }
        Mono<String> financialStatement = dartService.getFinancialStatementFromDart(request.getCorpName(), request.getYear(), request.getQuarter());
        System.out.println(financialStatement.block());
        return ResponseEntity.ok().body(financialStatement.block());

    }
}
