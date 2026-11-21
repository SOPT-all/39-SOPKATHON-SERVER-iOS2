package org.sopt.sopkathon.domain.message.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.sopkathon.domain.message.dto.request.MessageRequest;
import org.sopt.sopkathon.domain.message.dto.response.MessageResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "AI 메세지 생성", description = "돌봄 기록으로 브리핑 메시지 생성 API")
@RequestMapping("/api/v1")
public interface MessageApi {

    @Operation(summary = "돌봄 기록으로 일일 브리핑 생성")
    @PostMapping("/daily-briefings")
    MessageResponse generateDailyBriefing(
            @RequestHeader("Member-Id") Long memberId,
            @Valid @RequestBody MessageRequest request
    );
}
