package org.sopt.sopkathon.domain.briefing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.sopkathon.domain.briefing.dto.response.BriefingListResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "브리핑 API", description = "브리핑(AI 메시지) 관련 API 입니다")
@RequestMapping(path = "/api/v1/briefings")
public interface BriefingApi {
    @Operation(summary = "일일 브리핑 목록 조회",
            description = "일일 브리핑 화면의 아이별 카드를 조회합니다.")
    @GetMapping
    BriefingListResponse getBriefings(
            @Parameter(hidden = true)
            @RequestHeader("Member-Id") Long memberId
    );
}
