package org.sopt.sopkathon.domain.record.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record HomeResponse(
        @Schema(description = "선생님 이름", example = "한미녀")
        String name,

        @Schema(description = "조회 날짜에 돌봄 기록이 없는 아이 목록", example = "[]")
        List<UnrecordedKid> unrecordedKids,

        @Schema(description = "조회 날짜의 돌봄 기록 목록", example = "[]")
        List<TodayRecord> todayRecords
) {
    public record UnrecordedKid(
            @Schema(description = "아이 ID", example = "1")
            Long kidId,

            @Schema(description = "아이 이름", example = "노정완")
            String kidName
    ) {
    }

    public record TodayRecord(
            @Schema(description = "돌봄 기록 ID", example = "103")
            Long recordId,

            @Schema(description = "아이 이름", example = "김지우")
            String kidName,

            @Schema(description = "아이 색상 ID (0~3)", example = "0")
            int colorId,

            @Schema(description = "돌봄 기록 카테고리 코드", example = "FEEDING")
            String category,

            @Schema(description = "돌봄 기록 생성 시각", example = "2026-11-21T13:20:00")
            LocalDateTime createdAt
    ) {
    }
}
