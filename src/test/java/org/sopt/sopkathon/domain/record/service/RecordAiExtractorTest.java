package org.sopt.sopkathon.domain.record.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.sopkathon.domain.record.code.RecordErrorCode;
import org.sopt.sopkathon.domain.record.domain.RecordCategory;
import org.sopt.sopkathon.domain.record.dto.ai.AiRecordExtraction;
import org.sopt.sopkathon.domain.record.dto.ai.AiRecordItem;
import org.sopt.sopkathon.global.exception.GeneralException;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecordAiExtractor 응답 파싱 테스트")
class RecordAiExtractorTest {
    private static final List<String> KIDS = List.of("노정완");

    @Mock private GoogleGenAiChatModel chatModel;

    private RecordAiExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new RecordAiExtractor(chatModel, JsonMapper.builder().build(), "{assignedKids}\n{text}");
    }

    private AiRecordExtraction extractCategoryOnly(String aiResponse) {
        given(chatModel.call(anyString())).willReturn(aiResponse);
        return extractor.extract("우유 먹었어요", KIDS, false, true);
    }

    @ParameterizedTest(name = "{0}")
    @ValueSource(strings = {
            // 정상 JSON, 담당 아이가 아니라 kidName null
            "{\"records\":[{\"kidName\":null,\"category\":\"FEEDING\"}]}",
            // 코드 블록
            "```json\n{\"records\":[{\"kidName\":null,\"category\":\"FEEDING\"}]}\n```",
            // JSON 앞뒤 설명 문장
            "추출 결과입니다.\n{\"records\":[{\"kidName\":null,\"category\":\"FEEDING\"}]}\n이상입니다.",
            // 지시하지 않은 필드 추가
            "{\"records\":[{\"kidName\":null,\"category\":\"FEEDING\",\"reason\":\"우유\"}]}",
            // 소문자 카테고리
            "{\"records\":[{\"kidName\":null,\"category\":\"feeding\"}]}"
    })
    @DisplayName("Gemini가 형식을 조금 벗어나도 카테고리를 읽는다")
    void 형식이_조금_달라도_파싱(String aiResponse) {
        AiRecordExtraction result = extractCategoryOnly(aiResponse);

        assertThat(result.records()).extracting(AiRecordItem::category)
                .containsExactly(RecordCategory.FEEDING);
    }

    @Test
    @DisplayName("목록에 없는 카테고리면 AI 서버 오류가 아니라 RECORD_EXTRACTION_FAILED를 던진다")
    void 모르는_카테고리_분류실패() {
        assertThatThrownBy(() -> extractCategoryOnly(
                "{\"records\":[{\"kidName\":null,\"category\":\"BATH\"}]}"))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                        .isEqualTo(RecordErrorCode.RECORD_EXTRACTION_FAILED));
    }

    @Test
    @DisplayName("JSON이 아닌 응답이면 AI_SERVER_RESPONSE_FAILED를 던진다")
    void JSON아님_AI응답실패() {
        assertThatThrownBy(() -> extractCategoryOnly("죄송하지만 분석할 수 없습니다."))
                .isInstanceOf(GeneralException.class)
                .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                        .isEqualTo(RecordErrorCode.AI_SERVER_RESPONSE_FAILED));
    }
}
