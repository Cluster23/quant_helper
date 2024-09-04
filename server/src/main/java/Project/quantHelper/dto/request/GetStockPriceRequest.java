package Project.quantHelper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class GetStockPriceRequest {
    @NotBlank(message = "{not_blank}")
    @Schema(
            name = "stockName",
            description = "stock name",
            type = "String",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "SK하이닉스"
    )
    private String stockName;

    @NotBlank(message = "{not_blank}")
    @Schema(
            name = "startDate",
            description = "가격 조회 시작 일자",
            type = "String",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "20240415"
    )
    private String startDate;

    @NotBlank(message = "{not_blank}")
    @Schema(
            name = "endDate",
            description = "가격 조회 종료 일자",
            type = "String",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "20240415"
    )
    private String endDate;

}
