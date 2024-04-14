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
            name = "stockCode",
            description = "stock code",
            type = "String",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "000660"
    )
    private String stockCode;

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
