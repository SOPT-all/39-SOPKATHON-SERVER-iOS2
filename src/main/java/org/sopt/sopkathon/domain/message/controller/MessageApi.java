package org.sopt.sopkathon.domain.message.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.sopkathon.domain.message.dto.request.MessageCreateRequest;
import org.sopt.sopkathon.domain.message.dto.request.MessagePolishRequest;
import org.sopt.sopkathon.domain.message.dto.response.MessageResponse;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AI 메세지", description = "돌봄 기록으로 브리핑 메시지 관련 API")
@RequestMapping("/api/v1")
public interface MessageApi {

    @Operation(summary = "돌봄 기록으로 일일 브리핑 생성")
    @PostMapping("/briefings")
    MessageResponse generateDailyBriefing(
            @RequestHeader("Member-Id") Long memberId,
            @Valid @RequestBody MessageCreateRequest request
    );

    @Operation(summary = "AI로 브리핑 메시지 다듬기")
    @PatchMapping("/briefings/{briefingId}/refine")
    MessageResponse refineBriefing(
            @RequestHeader("Member-Id") Long memberId,
            @PathVariable Long briefingId,
            @Valid @RequestBody MessagePolishRequest request
    );
}
