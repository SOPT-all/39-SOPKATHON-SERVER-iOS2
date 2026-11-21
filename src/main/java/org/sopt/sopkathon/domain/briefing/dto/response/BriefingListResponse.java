package org.sopt.sopkathon.domain.briefing.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public record BriefingListResponse(
        @Schema(description = "브리핑 날짜", example = "2026-11-21")
        LocalDate date,

        @Schema(description = "해당 날짜에 기록이 있는 아이 수", example = "4")
        int recordedKidCount,

        @Schema(description = "아이별 브리핑 카드 목록")
        List<BriefingKidResponse> kids
) {
    public static BriefingListResponse of(LocalDate date, List<BriefingKidResponse> kids) {
        return new BriefingListResponse(date, kids.size(), kids);
    }
}
