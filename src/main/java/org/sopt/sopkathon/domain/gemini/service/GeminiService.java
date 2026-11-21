package org.sopt.sopkathon.domain.gemini.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional(readOnly = true)
public class GeminiService {

    private final GoogleGenAiChatModel googleGenAiChatModel;
    private final String summaryPrompt;
    private final String refinePrompt;

    public GeminiService(
            GoogleGenAiChatModel googleGenAiChatModel,
            @Value("${app.gemini.summary-prompt}") String summaryPrompt,
            @Value("${app.gemini.refine-prompt}") String refinePrompt
    ) {
        this.googleGenAiChatModel = googleGenAiChatModel;
        this.summaryPrompt = summaryPrompt;
        this.refinePrompt = refinePrompt;
    }

    public String ask(String records) {
        long start = System.nanoTime();
        log.info("Starting Gemini briefing generation");

        try {
            String briefing = googleGenAiChatModel.call(
                    summaryPrompt + "\n\n[돌봄 기록]\n" + records
            );
            log.info("Gemini briefing generation completed ({} ms)",
                    TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            return briefing;
        } catch (RuntimeException exception) {
            log.warn("Gemini briefing generation failed ({} ms, {})",
                    TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start),
                    exception.getClass().getSimpleName());
            throw exception;
        }
    }

    public String refine(String content, String instruction) {
        return googleGenAiChatModel.call(refinePrompt.formatted(content, instruction));
    }
}
