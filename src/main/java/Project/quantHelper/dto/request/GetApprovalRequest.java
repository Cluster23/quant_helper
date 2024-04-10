package Project.quantHelper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetApprovalRequest {
    @NotBlank(message = "{not_blank}")
    @Schema(
            name = "approvalKey",
            description = "웹소켓 접속키",
            type = "String",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "a2585daf-8c09-4587-9fce-8ab893XXXXX"
    )
    private String approvalKey;
}
