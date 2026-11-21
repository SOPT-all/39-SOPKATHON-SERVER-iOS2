package org.sopt.sopkathon.domain.briefing.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public record BriefingListResponse(
        @Schema(description = "브리핑 날짜", example = "2026-11-21")
        LocalDate date,

        @Schema(description = "해당 날짜에 기록이 있는 아이 이름 목록 (kids와 같은 이름순)",
                example = "[\"김민교\", \"노정완\", \"박서아\", \"이하준\"]")
        List<String> recordedKidNames,

        @Schema(description = "아이별 브리핑 카드 목록")
        List<BriefingKidResponse> kids
) {
    public static BriefingListResponse of(LocalDate date, List<BriefingKidResponse> kids) {
        List<String> names = kids.stream()
                .map(BriefingKidResponse::name)
                .toList();
        return new BriefingListResponse(date, names, kids);
    }
}
