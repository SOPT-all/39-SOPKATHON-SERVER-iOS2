package org.sopt.sopkathon.domain.kid.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.sopkathon.domain.kid.code.KidErrorCode;
import org.sopt.sopkathon.domain.kid.domain.Kid;
import org.sopt.sopkathon.domain.kid.domain.MemberKid;
import org.sopt.sopkathon.domain.kid.domain.Parent;
import org.sopt.sopkathon.domain.kid.domain.Relationship;
import org.sopt.sopkathon.domain.kid.domain.Gender;
import org.sopt.sopkathon.domain.kid.dto.request.UpdateKidRequest;
import org.sopt.sopkathon.domain.kid.dto.request.UpdateParentRequest;
import org.sopt.sopkathon.domain.kid.dto.response.UpdateKidResponse;
import org.sopt.sopkathon.domain.kid.dto.response.CreateKidResponse;
import org.sopt.sopkathon.domain.kid.dto.response.KidListResponse;
import org.sopt.sopkathon.domain.kid.dto.response.KidResponse;
import org.sopt.sopkathon.domain.kid.repository.KidQueryRepository;
import org.sopt.sopkathon.domain.kid.repository.KidRepository;
import org.sopt.sopkathon.domain.kid.repository.MemberKidRepository;
import org.sopt.sopkathon.domain.kid.repository.ParentRepository;
import org.sopt.sopkathon.domain.member.code.MemberErrorCode;
import org.sopt.sopkathon.domain.member.domain.Member;
import org.sopt.sopkathon.domain.member.repository.MemberRepository;
import org.sopt.sopkathon.global.exception.GeneralException;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.node.NullNode;
import tools.jackson.databind.node.StringNode;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.sopt.sopkathon.global.config.TimeConfig.KST;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.sopt.sopkathon.support.fixture.KidFixture.*;
import static org.sopt.sopkathon.support.fixture.MemberFixture.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("KidService 단위 테스트")
class KidServiceTest {
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 22, 9, 0);
    private static final Clock FIXED_CLOCK = Clock.fixed(NOW.atZone(KST).toInstant(), KST);

    private KidService kidService;

    @Mock private KidRepository kidRepository;
    @Mock private KidQueryRepository kidQueryRepository;
    @Mock private ParentRepository parentRepository;
    @Mock private MemberKidRepository memberKidRepository;
    @Mock private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        kidService = new KidService(kidRepository, kidQueryRepository, parentRepository, memberKidRepository,
                memberRepository, FIXED_CLOCK);
    }

    @Nested
    @DisplayName("아이 목록 조회(getKids)")
    class GetKids {

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("회원이 담당하는 아이 목록을 keyword 조건으로 조회해 반환한다")
            void 담당아이_목록조회성공() {
                given(memberRepository.existsById(MEMBER_ID)).willReturn(true);
                given(kidQueryRepository.findMyKids(MEMBER_ID, "솝트"))
                        .willReturn(List.of(kid(1L, "김솝트"), kid(2L, "이솝트")));

                KidListResponse result = kidService.getKids(MEMBER_ID, "솝트");

                assertThat(result.kids()).extracting(KidResponse::kidId).containsExactly(1L, 2L);
                assertThat(result.kids()).extracting(KidResponse::name).containsExactly("김솝트", "이솝트");
            }
        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("회원이 없으면 MEMBER_NOT_FOUND를 던진다")
            void 목록조회시_회원없음_예외() {
                given(memberRepository.existsById(MEMBER_ID)).willReturn(false);

                assertThatThrownBy(() -> kidService.getKids(MEMBER_ID, null))
                        .isInstanceOf(GeneralException.class)
                        .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                                .isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND));
            }
        }
    }

    @Nested
    @DisplayName("아이 등록(createKid)")
    class CreateKid {

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("아이를 저장하고 회원과 연결한 뒤 학부모를 아이에 연결해 저장한다")
            void 학부모포함_등록성공() {
                Member member = member();
                given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(member));
                given(kidRepository.save(any(Kid.class))).willAnswer(inv -> {
                    Kid saved = inv.getArgument(0);
                    ReflectionTestUtils.setField(saved, "id", KID_ID);
                    return saved;
                });

                CreateKidResponse result = kidService.createKid(MEMBER_ID, createKidRequest(parentRequest()));

                assertThat(result.kidId()).isEqualTo(KID_ID);

                ArgumentCaptor<MemberKid> memberKidCaptor = ArgumentCaptor.forClass(MemberKid.class);
                verify(memberKidRepository).save(memberKidCaptor.capture());
                assertThat(memberKidCaptor.getValue().getMember()).isEqualTo(member);
                assertThat(memberKidCaptor.getValue().getKid().getId()).isEqualTo(KID_ID);

                ArgumentCaptor<Parent> parentCaptor = ArgumentCaptor.forClass(Parent.class);
                verify(parentRepository).save(parentCaptor.capture());
                assertThat(parentCaptor.getValue().getKid().getId()).isEqualTo(KID_ID);
                assertThat(parentCaptor.getValue().getPhone()).isEqualTo("01012345678");
                assertThat(parentCaptor.getValue().getRelationship()).isEqualTo(Relationship.MOTHER);
            }
        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("회원이 없으면 MEMBER_NOT_FOUND를 던지고 아이를 저장하지 않는다")
            void 등록시_회원없음_예외() {
                given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.empty());

                assertThatThrownBy(() -> kidService.createKid(MEMBER_ID, createKidRequest(parentRequest())))
                        .isInstanceOf(GeneralException.class)
                        .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                                .isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND));
                verify(kidRepository, never()).save(any());
                verify(parentRepository, never()).save(any());
            }
        }
    }

    @Nested
    @DisplayName("아이 정보 수정(updateKid)")
    class UpdateKid {

        private void givenMyKid(Kid kid) {
            given(memberRepository.existsById(MEMBER_ID)).willReturn(true);
            given(memberKidRepository.existsByMemberIdAndKidId(MEMBER_ID, KID_ID)).willReturn(true);
            given(kidRepository.findByIdAndDeletedAtIsNull(KID_ID)).willReturn(Optional.of(kid));
        }

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("전달한 아이 정보와 학부모 정보를 수정한다")
            void 아이와학부모_수정성공() {
                Kid kid = kid();
                Parent parent = parent(kid);
                givenMyKid(kid);
                given(parentRepository.findFirstByKidIdOrderByIdAsc(KID_ID)).willReturn(Optional.of(parent));
                UpdateKidRequest request = new UpdateKidRequest(
                        "김지우", null, Gender.MALE,
                        new UpdateParentRequest(null, "01098765432", Relationship.FATHER),
                        StringNode.valueOf("우유 알레르기가 있습니다."));

                UpdateKidResponse result = kidService.updateKid(MEMBER_ID, KID_ID, request);

                assertThat(result.kidId()).isEqualTo(KID_ID);
                assertThat(kid.getName()).isEqualTo("김지우");
                assertThat(kid.getBirthDate()).isEqualTo(BIRTH_DATE);
                assertThat(kid.getGender()).isEqualTo(Gender.MALE);
                assertThat(kid.getNote()).isEqualTo("우유 알레르기가 있습니다.");
                assertThat(parent.getName()).isEqualTo("김엄마");
                assertThat(parent.getPhone()).isEqualTo("01098765432");
                assertThat(parent.getRelationship()).isEqualTo(Relationship.FATHER);
            }

            @Test
            @DisplayName("note를 전달하지 않으면 기존 특이사항을 유지한다")
            void note미전달_기존값유지() {
                Kid kid = kid();
                givenMyKid(kid);

                kidService.updateKid(MEMBER_ID, KID_ID, new UpdateKidRequest(null, null, null, null, null));

                assertThat(kid.getNote()).isEqualTo("견과류 알레르기가 있어요.");
            }

            @Test
            @DisplayName("note를 null로 전달하면 특이사항을 삭제한다")
            void note_null_삭제() {
                Kid kid = kid();
                givenMyKid(kid);

                kidService.updateKid(MEMBER_ID, KID_ID, new UpdateKidRequest(null, null, null, null, NullNode.getInstance()));

                assertThat(kid.getNote()).isNull();
            }

            @Test
            @DisplayName("학부모 정보를 전달하지 않으면 학부모를 조회하지 않는다")
            void 학부모미전달_조회안함() {
                givenMyKid(kid());

                kidService.updateKid(MEMBER_ID, KID_ID, new UpdateKidRequest("김지우", null, null, null, null));

                verify(parentRepository, never()).findFirstByKidIdOrderByIdAsc(any());
            }
        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("회원이 담당하지 않는 아이면 KID_NOT_FOUND를 던진다")
            void 수정시_담당아님_예외() {
                given(memberRepository.existsById(MEMBER_ID)).willReturn(true);
                given(memberKidRepository.existsByMemberIdAndKidId(MEMBER_ID, KID_ID)).willReturn(false);

                assertThatThrownBy(() -> kidService.updateKid(MEMBER_ID, KID_ID,
                        new UpdateKidRequest("김지우", null, null, null, null)))
                        .isInstanceOf(GeneralException.class)
                        .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                                .isEqualTo(KidErrorCode.KID_NOT_FOUND));
            }

            @Test
            @DisplayName("학부모 정보를 전달했는데 연결된 학부모가 없으면 PARENT_NOT_FOUND를 던진다")
            void 수정시_학부모없음_예외() {
                givenMyKid(kid());
                given(parentRepository.findFirstByKidIdOrderByIdAsc(KID_ID)).willReturn(Optional.empty());

                assertThatThrownBy(() -> kidService.updateKid(MEMBER_ID, KID_ID, new UpdateKidRequest(
                        null, null, null, new UpdateParentRequest("김민지", null, null), null)))
                        .isInstanceOf(GeneralException.class)
                        .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                                .isEqualTo(KidErrorCode.PARENT_NOT_FOUND));
            }
        }
    }

    @Nested
    @DisplayName("아이 삭제(deleteKid)")
    class DeleteKid {

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("담당하는 아이면 실제로 지우지 않고 deletedAt에 현재 시각을 기록한다")
            void 담당아이_소프트삭제성공() {
                Kid kid = kid();
                given(memberRepository.existsById(MEMBER_ID)).willReturn(true);
                given(memberKidRepository.existsByMemberIdAndKidId(MEMBER_ID, KID_ID)).willReturn(true);
                given(kidRepository.findByIdAndDeletedAtIsNull(KID_ID)).willReturn(Optional.of(kid));

                kidService.deleteKid(MEMBER_ID, KID_ID);

                assertThat(kid.getDeletedAt()).isEqualTo(NOW);
                verify(kidRepository, never()).delete(any());
            }
        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("회원이 없으면 MEMBER_NOT_FOUND를 던진다")
            void 삭제시_회원없음_예외() {
                given(memberRepository.existsById(MEMBER_ID)).willReturn(false);

                assertThatThrownBy(() -> kidService.deleteKid(MEMBER_ID, KID_ID))
                        .isInstanceOf(GeneralException.class)
                        .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                                .isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND));
                verify(kidRepository, never()).delete(any());
            }

            @Test
            @DisplayName("회원이 담당하지 않는 아이면 KID_NOT_FOUND를 던지고 삭제하지 않는다")
            void 삭제시_담당아님_예외() {
                given(memberRepository.existsById(MEMBER_ID)).willReturn(true);
                given(memberKidRepository.existsByMemberIdAndKidId(MEMBER_ID, KID_ID)).willReturn(false);

                assertThatThrownBy(() -> kidService.deleteKid(MEMBER_ID, KID_ID))
                        .isInstanceOf(GeneralException.class)
                        .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                                .isEqualTo(KidErrorCode.KID_NOT_FOUND));
                verify(kidRepository, never()).delete(any());
            }

            @Test
            @DisplayName("이미 삭제된 아이면 KID_NOT_FOUND를 던진다")
            void 삭제시_이미삭제됨_예외() {
                given(memberRepository.existsById(MEMBER_ID)).willReturn(true);
                given(memberKidRepository.existsByMemberIdAndKidId(MEMBER_ID, KID_ID)).willReturn(true);
                given(kidRepository.findByIdAndDeletedAtIsNull(KID_ID)).willReturn(Optional.empty());

                assertThatThrownBy(() -> kidService.deleteKid(MEMBER_ID, KID_ID))
                        .isInstanceOf(GeneralException.class)
                        .satisfies(ex -> assertThat(((GeneralException) ex).getErrorCode())
                                .isEqualTo(KidErrorCode.KID_NOT_FOUND));
            }
        }
    }
}
