package org.sopt.sopkathon.domain.message.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record MessageResponse(
        @Schema(description = "브리핑 id", example = "5")
        Long briefingId,
        @Schema(description = "브리핑 내용", example = "안녕하세요! 지우는 오늘 오전 수유 3회 잘 먹었고...")
        String content
) {
}
