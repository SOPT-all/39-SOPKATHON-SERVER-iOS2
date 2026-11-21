package org.sopt.sopkathon.domain.record.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.sopkathon.domain.kid.domain.Kid;
import org.sopt.sopkathon.domain.kid.domain.MemberKid;
import org.sopt.sopkathon.domain.kid.repository.MemberKidRepository;
import org.sopt.sopkathon.domain.record.code.RecordErrorCode;
import org.sopt.sopkathon.domain.record.domain.RecordCategory;
import org.sopt.sopkathon.domain.record.dto.ai.AiRecordExtraction;
import org.sopt.sopkathon.domain.record.dto.ai.AiRecordItem;
import org.sopt.sopkathon.domain.record.dto.request.RecordCreateRequest;
import org.sopt.sopkathon.domain.record.dto.response.RecordCreateItemResponse;
import org.sopt.sopkathon.domain.record.dto.response.RecordCreateResponse;
import org.sopt.sopkathon.domain.record.repository.RecordRepository;
import org.sopt.sopkathon.global.exception.GeneralException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.sopt.sopkathon.global.config.TimeConfig.KST;
import static org.sopt.sopkathon.support.fixture.KidFixture.KID_ID;
import static org.sopt.sopkathon.support.fixture.KidFixture.kid;
import static org.sopt.sopkathon.support.fixture.MemberFixture.MEMBER_ID;
import static org.sopt.sopkathon.support.fixture.MemberFixture.member;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecordService 단위 테스트")
class RecordServiceTest {
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 22, 9, 0);
    private static final Clock FIXED_CLOCK = Clock.fixed(NOW.atZone(KST).toInstant(), KST);
    private static final String TEXT = "우유 먹고 낮잠 잤어요";

    private RecordService recordService;

    @Mock private MemberKidRepository memberKidRepository;
    @Mock private RecordRepository recordRepository;
    @Mock private RecordAiExtractor recordAiExtractor;

    private Kid kid;

    @BeforeEach
    void setUp() {
        recordService = new RecordService(memberKidRepository, recordRepository, recordAiExtractor, FIXED_CLOCK);
        kid = kid();
    }

    private void givenAssignedKid() {
        MemberKid memberKid = MemberKid.builder().member(member()).kid(kid).build();
        given(memberKidRepository.findByMemberIdAndKidIdAndKidDeletedAtIsNull(MEMBER_ID, KID_ID))
                .willReturn(Optional.of(memberKid));
    }

    private void givenSaveAllReturnsInput() {
        given(recordRepository.saveAll(anyList())).willAnswer(invocation -> invocation.getArgument(0));
    }

    @Nested
    @DisplayName("아이만 선택하고 category를 null로 보냈을 때")
    class KidSelectedCategoryNull {

        @Test
        @DisplayName("AI가 분류한 카테고리로 선택한 아이의 기록을 저장한다")
        void AI_카테고리로_저장() {
            givenAssignedKid();
            givenSaveAllReturnsInput();
            given(recordAiExtractor.extract(TEXT, List.of(kid.getName()), false, true))
                    .willReturn(new AiRecordExtraction(List.of(
                            new AiRecordItem(kid.getName(), RecordCategory.FEEDING),
                            new AiRecordItem(kid.getName(), RecordCategory.SLEEP)
                    )));

            RecordCreateResponse response = recordService.createRecords(MEMBER_ID,
                    new RecordCreateRequest(KID_ID, null, TEXT, null));

            assertThat(response.records())
                    .extracting(RecordCreateItemResponse::kidId, RecordCreateItemResponse::category,
                            RecordCreateItemResponse::recordedAt)
                    .containsExactly(
                            org.assertj.core.groups.Tuple.tuple(KID_ID, "FEEDING", NOW),
                            org.assertj.core.groups.Tuple.tuple(KID_ID, "SLEEP", NOW)
                    );
        }

        @Test
        @DisplayName("AI가 이름을 비우거나 다르게 줘도 선택한 아이로 저장한다")
        void AI_이름무시_선택한아이로저장() {
            givenAssignedKid();
            givenSaveAllReturnsInput();
            given(recordAiExtractor.extract(TEXT, List.of(kid.getName()), false, true))
                    .willReturn(new AiRecordExtraction(List.of(
                            new AiRecordItem(null, RecordCategory.FEEDING)
                    )));

            RecordCreateResponse response = recordService.createRecords(MEMBER_ID,
                    new RecordCreateRequest(KID_ID, null, TEXT, null));

            assertThat(response.records()).singleElement()
                    .satisfies(item -> {
                        assertThat(item.kidId()).isEqualTo(KID_ID);
                        assertThat(item.category()).isEqualTo("FEEDING");
                    });
        }

        @Test
        @DisplayName("AI가 카테고리를 분류하지 못하면 예외를 그대로 던지고 저장하지 않는다")
        void AI_분류실패_저장안함() {
            givenAssignedKid();
            given(recordAiExtractor.extract(TEXT, List.of(kid.getName()), false, true))
                    .willThrow(new GeneralException(RecordErrorCode.RECORD_EXTRACTION_FAILED));

            assertThatThrownBy(() -> recordService.createRecords(MEMBER_ID,
                    new RecordCreateRequest(KID_ID, null, TEXT, null)))
                    .isInstanceOf(GeneralException.class)
                    .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                            .isEqualTo(RecordErrorCode.RECORD_EXTRACTION_FAILED));
            verify(recordRepository, never()).saveAll(anyList());
        }

        @Test
        @DisplayName("담당하지 않는 아이면 AI를 호출하지 않고 KID_NOT_FOUND_OR_NOT_ASSIGNED를 던진다")
        void 담당아님_AI호출안함() {
            given(memberKidRepository.findByMemberIdAndKidIdAndKidDeletedAtIsNull(MEMBER_ID, KID_ID))
                    .willReturn(Optional.empty());

            assertThatThrownBy(() -> recordService.createRecords(MEMBER_ID,
                    new RecordCreateRequest(KID_ID, null, TEXT, null)))
                    .isInstanceOf(GeneralException.class)
                    .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                            .isEqualTo(RecordErrorCode.KID_NOT_FOUND_OR_NOT_ASSIGNED));
            verifyNoInteractions(recordAiExtractor);
        }
    }

    @Test
    @DisplayName("아이와 카테고리를 모두 보내면 AI 없이 바로 저장한다")
    void 직접입력_AI호출안함() {
        givenAssignedKid();
        givenSaveAllReturnsInput();

        RecordCreateResponse response = recordService.createRecords(MEMBER_ID,
                new RecordCreateRequest(KID_ID, RecordCategory.MEAL, TEXT, null));

        assertThat(response.records()).singleElement()
                .satisfies(item -> assertThat(item.category()).isEqualTo("MEAL"));
        verifyNoInteractions(recordAiExtractor);
    }
}
