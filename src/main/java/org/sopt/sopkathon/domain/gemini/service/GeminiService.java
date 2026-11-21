package org.sopt.sopkathon.domain.gemini.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GeminiService {

    private final GoogleGenAiChatModel googleGenAiChatModel;

    public String ask(String prompt) {
        return googleGenAiChatModel.call(prompt);
    }
}
