package Project.quantHelper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetFinancialStatementRequest {
    @NotBlank(message = "{not_blank}")
    @Schema(
            name = "corpName",
            description = "corporation name",
            type = "String",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "삼성"
    )
    private String corpName;
    @NotBlank(message = "{not_blank}")
    @Schema(
            name = "year",
            description = "year",
            type = "String",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "2023"
    )
    private String year;
    @NotBlank(message = "{not_blank}")
    @Schema(
            name = "quater",
            description = "quater",
            type = "Integer",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "2"
    )
    private int quarter;
}