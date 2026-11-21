package org.sopt.sopkathon.domain.gemini.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record GeminiResponse(
        @Schema(description = "학부모에게 전달할 일일 브리핑", example = "오늘 오전에는 분유 30mL를 먹었고, 낮잠은 1시간 잤습니다.")
        String briefing
) {
}
