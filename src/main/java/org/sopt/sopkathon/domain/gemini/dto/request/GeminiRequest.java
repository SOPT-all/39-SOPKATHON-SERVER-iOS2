package org.sopt.sopkathon.domain.gemini.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record GeminiRequest(
        @NotBlank
        @Schema(description = "교사가 작성한 돌봄 기록", example = "오전에 분유 30mL를 먹었고, 낮잠을 1시간 잤어요.")
        String records
) {
}
