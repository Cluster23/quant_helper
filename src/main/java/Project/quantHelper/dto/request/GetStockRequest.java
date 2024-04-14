package Project.quantHelper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetStockRequest {
    @NotBlank(message = "{not_blank}")
    @Schema(
            name = "PDNO",
            description = "stock code",
            type = "String",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "000660"
    )
    private String stockCode;
}
