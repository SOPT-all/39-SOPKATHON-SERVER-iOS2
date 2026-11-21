package org.sopt.sopkathon.domain.gemini.service;

import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GeminiService {

    private final GoogleGenAiChatModel googleGenAiChatModel;
    private final String summaryPrompt;

    public GeminiService(
            GoogleGenAiChatModel googleGenAiChatModel,
            @Value("${app.gemini.summary-prompt}") String summaryPrompt
    ) {
        this.googleGenAiChatModel = googleGenAiChatModel;
        this.summaryPrompt = summaryPrompt;
    }

    public String ask(String records) {
        return googleGenAiChatModel.call(
                summaryPrompt + "\n\n[돌봄 기록]\n" + records
        );
    }
}
