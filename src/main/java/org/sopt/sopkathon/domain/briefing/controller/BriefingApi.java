package org.sopt.sopkathon.domain.briefing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.sopkathon.domain.briefing.dto.request.BriefingCreateRequest;
import org.sopt.sopkathon.domain.briefing.dto.request.BriefingRefineRequest;
import org.sopt.sopkathon.domain.briefing.dto.response.BriefingContentResponse;
import org.sopt.sopkathon.domain.briefing.dto.response.BriefingListResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Operation(summary = "돌봄 기록으로 일일 브리핑 생성",
            description = "해당 날짜의 돌봄 기록으로 AI 브리핑을 생성합니다. 이미 있으면 새 내용으로 덮어씁니다.")
    @PostMapping
    BriefingContentResponse createBriefing(
            @Parameter(hidden = true)
            @RequestHeader("Member-Id") Long memberId,
            @Valid @RequestBody BriefingCreateRequest request
    );

    @Operation(summary = "AI로 브리핑 메시지 다듬기")
    @PatchMapping("/{briefingId}/refine")
    BriefingContentResponse refineBriefing(
            @Parameter(hidden = true)
            @RequestHeader("Member-Id") Long memberId,
            @Parameter(description = "브리핑 ID", example = "5")
            @PathVariable Long briefingId,
            @Valid @RequestBody BriefingRefineRequest request
    );
}
