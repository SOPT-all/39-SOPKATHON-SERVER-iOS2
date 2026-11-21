package org.sopt.sopkathon.domain.gemini.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.domain.gemini.dto.request.GeminiRequest;
import org.sopt.sopkathon.domain.gemini.dto.response.GeminiResponse;
import org.sopt.sopkathon.domain.gemini.service.GeminiService;
import org.sopt.sopkathon.global.code.CommonSuccessCode;
import org.sopt.sopkathon.global.swagger.ApiCommonSuccess;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GeminiController implements GeminiApi {

    private final GeminiService geminiService;

    @Override
    @ApiCommonSuccess(CommonSuccessCode.OK)
    public GeminiResponse summarize(GeminiRequest request) {
        return new GeminiResponse(geminiService.ask(request.records()));
    }
}
