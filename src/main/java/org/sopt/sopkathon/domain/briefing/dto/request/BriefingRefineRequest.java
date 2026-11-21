package org.sopt.sopkathon.domain.briefing.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record BriefingRefineRequest(
        @Schema(description = "브리핑 메세지 수정 요청사항", example = "조금 더 따뜻한 말투로, 낮잠 이야기를 먼저 해줘")
        @NotBlank
        String instruction
) {
}
