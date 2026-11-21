package org.sopt.sopkathon.domain.briefing.repository;

// 아이별, 카테고리별 기록 건수
public record RecordCount(
        Long kidId,
        String category,
        Long count
) {
}
