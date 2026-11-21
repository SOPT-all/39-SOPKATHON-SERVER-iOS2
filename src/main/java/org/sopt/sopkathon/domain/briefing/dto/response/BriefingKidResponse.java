package org.sopt.sopkathon.domain.briefing.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

public record BriefingKidResponse(
        @Schema(description = "아이 ID", example = "1")
        Long kidId,

        @Schema(description = "아이 이름", example = "노정완")
        String name,

        @Schema(description = "만 나이 (브리핑 날짜 기준)", example = "2")
        int age,

        @Schema(description = "해당 날짜의 전체 기록 건수", example = "6")
        long recordCount,

        @Schema(description = "카테고리별 기록 건수", example = "{\"FEEDING\": 3, \"SLEEP\": 1, \"DIAPER\": 2}")
        Map<String, Long> summary,

        @Schema(description = "학부모 정보, 등록된 학부모가 없으면 null", nullable = true)
        BriefingParentResponse parent,

        @Schema(description = "생성된 브리핑 메시지, 아직 생성 전이면 null", nullable = true)
        BriefingContentResponse briefing
) {
}
