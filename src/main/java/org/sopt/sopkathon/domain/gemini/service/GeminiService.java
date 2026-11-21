package org.sopt.sopkathon.domain.gemini.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GeminiService {

    private final GoogleGenAiChatModel googleGenAiChatModel;

    @Value("${app.gemini.summary-prompt}")
    private String summaryPrompt;

    public String ask(String records) {
        return googleGenAiChatModel.call(
                summaryPrompt + "\n\n[돌봄 기록]\n" + records
        );
    }
}
