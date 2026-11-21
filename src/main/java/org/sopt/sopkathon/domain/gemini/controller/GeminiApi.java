package org.sopt.sopkathon.domain.gemini.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.sopt.sopkathon.domain.gemini.dto.request.GeminiRequest;
import org.sopt.sopkathon.domain.gemini.dto.response.GeminiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "AI 요약", description = "제미나이 요약 API")
@RequestMapping("/api/v1/briefings")
public interface GeminiApi {

    @Operation(summary = "돌봄 기록으로 일일 브리핑 생성")
    @PostMapping("/summary")
    GeminiResponse summarize(@Valid @RequestBody GeminiRequest request);
}
