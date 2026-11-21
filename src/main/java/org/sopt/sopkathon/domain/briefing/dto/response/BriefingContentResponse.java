package org.sopt.sopkathon.domain.briefing.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.sopt.sopkathon.domain.briefing.domain.Briefing;

public record BriefingContentResponse(
        @Schema(description = "브리핑 ID", example = "5")
        Long briefingId,

        @Schema(description = "브리핑 메시지 내용", example = "안녕하세요! 정완이는 오늘 오전 수유 3회 잘 먹었고...")
        String content
) {
    public static BriefingContentResponse of(Briefing briefing) {
        return new BriefingContentResponse(briefing.getId(), briefing.getContent());
    }
}
