package org.sopt.sopkathon.domain.kid.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sopt.sopkathon.domain.kid.code.KidErrorCode;
import org.sopt.sopkathon.domain.kid.code.KidSuccessCode;
import org.sopt.sopkathon.domain.kid.domain.Gender;
import org.sopt.sopkathon.domain.kid.domain.Kid;
import org.sopt.sopkathon.domain.kid.domain.Relationship;
import org.sopt.sopkathon.domain.kid.dto.request.CreateKidRequest;
import org.mockito.ArgumentCaptor;
import org.sopt.sopkathon.domain.kid.dto.request.ParentRequest;
import org.sopt.sopkathon.domain.kid.dto.request.UpdateKidRequest;
import org.sopt.sopkathon.domain.kid.dto.request.UpdateParentRequest;
import org.sopt.sopkathon.domain.kid.dto.response.UpdateKidResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.sopt.sopkathon.domain.kid.dto.response.CreateKidResponse;
import org.sopt.sopkathon.domain.kid.dto.response.KidListResponse;
import org.sopt.sopkathon.domain.kid.service.KidService;
import org.sopt.sopkathon.domain.member.code.MemberErrorCode;
import org.sopt.sopkathon.global.code.CommonErrorCode;
import org.sopt.sopkathon.global.exception.GeneralException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.StringNode;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.sopt.sopkathon.support.fixture.KidFixture.*;
import static org.sopt.sopkathon.support.fixture.MemberFixture.MEMBER_ID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KidController.class)
@DisplayName("KidController 테스트")
class KidControllerTest {
    private static final String MEMBER_ID_HEADER = "Member-Id";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private KidService kidService;

    @Nested
    @DisplayName("GET /api/v1/kids - 아이 목록 조회")
    class GetKids {

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("회원의 아이 목록을 kidId와 name만 담아 200으로 반환한다")
            void 목록조회_200을_반환한다() throws Exception {
                Kid kid = kid();
                given(kidService.getKids(MEMBER_ID, null)).willReturn(KidListResponse.of(List.of(kid)));

                mockMvc.perform(get("/api/v1/kids").header(MEMBER_ID_HEADER, MEMBER_ID))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value(true))
                        .andExpect(jsonPath("$.code").value(KidSuccessCode.KID_LIST_OK.getCode()))
                        .andExpect(jsonPath("$.result.kids.length()").value(1))
                        .andExpect(jsonPath("$.result.kids[0].kidId").value(KID_ID))
                        .andExpect(jsonPath("$.result.kids[0].name").value("김솝트"))
                        .andExpect(jsonPath("$.result.kids[0].birthDate").doesNotExist());
            }

            @Test
            @DisplayName("keyword를 보내면 서비스에 keyword를 넘긴다")
            void 키워드검색_200을_반환한다() throws Exception {
                given(kidService.getKids(MEMBER_ID, "솝트")).willReturn(KidListResponse.of(List.of(kid())));

                mockMvc.perform(get("/api/v1/kids")
                                .header(MEMBER_ID_HEADER, MEMBER_ID)
                                .param("keyword", "솝트"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.result.kids[0].kidId").value(KID_ID));
            }
        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("회원 ID 헤더가 없으면 400을 반환한다")
            void 헤더없음_400을_반환한다() throws Exception {
                mockMvc.perform(get("/api/v1/kids"))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.isSuccess").value(false))
                        .andExpect(jsonPath("$.code").value(CommonErrorCode.BAD_REQUEST.getCode()));
            }

            @Test
            @DisplayName("회원이 없으면 MEMBER_NOT_FOUND로 404를 반환한다")
            void 목록조회_회원없음_404를_반환한다() throws Exception {
                given(kidService.getKids(MEMBER_ID, null))
                        .willThrow(new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));

                mockMvc.perform(get("/api/v1/kids").header(MEMBER_ID_HEADER, MEMBER_ID))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.isSuccess").value(false))
                        .andExpect(jsonPath("$.code").value(MemberErrorCode.MEMBER_NOT_FOUND.getCode()));
            }
        }
    }

    @Nested
    @DisplayName("POST /api/v1/kids - 아이 등록")
    class CreateKid {

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("아이와 학부모를 등록하고 200을 반환한다")
            void 등록_200을_반환한다() throws Exception {
                given(kidService.createKid(eq(MEMBER_ID), any(CreateKidRequest.class)))
                        .willReturn(new CreateKidResponse(KID_ID));

                mockMvc.perform(post("/api/v1/kids")
                                .header(MEMBER_ID_HEADER, MEMBER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createKidRequest(parentRequest()))))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value(true))
                        .andExpect(jsonPath("$.code").value(KidSuccessCode.KID_CREATED.getCode()))
                        .andExpect(jsonPath("$.result.kidId").value(KID_ID));
            }
        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("회원이 없으면 MEMBER_NOT_FOUND로 404를 반환한다")
            void 등록_회원없음_404를_반환한다() throws Exception {
                given(kidService.createKid(eq(MEMBER_ID), any(CreateKidRequest.class)))
                        .willThrow(new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));

                mockMvc.perform(post("/api/v1/kids")
                                .header(MEMBER_ID_HEADER, MEMBER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createKidRequest(parentRequest()))))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.code").value(MemberErrorCode.MEMBER_NOT_FOUND.getCode()));
            }

            @Test
            @DisplayName("아이 이름이 비어 있으면 400을 반환한다")
            void 이름공백_400을_반환한다() throws Exception {
                expectBadRequest(createKidRequest(" ", BIRTH_DATE, Gender.FEMALE, parentRequest()));
            }

            @Test
            @DisplayName("아이 이름이 100자를 넘으면 400을 반환한다")
            void 이름초과_400을_반환한다() throws Exception {
                expectBadRequest(createKidRequest("가".repeat(101), BIRTH_DATE, Gender.FEMALE, parentRequest()));
            }

            @Test
            @DisplayName("생년월일이 없으면 400을 반환한다")
            void 생년월일없음_400을_반환한다() throws Exception {
                expectBadRequest(createKidRequest("김솝트", null, Gender.FEMALE, parentRequest()));
            }

            @Test
            @DisplayName("생년월일이 미래면 400을 반환한다")
            void 생년월일미래_400을_반환한다() throws Exception {
                expectBadRequest(createKidRequest("김솝트", LocalDate.now().plusDays(1), Gender.FEMALE, parentRequest()));
            }

            @Test
            @DisplayName("성별이 없으면 400을 반환한다")
            void 성별없음_400을_반환한다() throws Exception {
                expectBadRequest(createKidRequest("김솝트", BIRTH_DATE, null, parentRequest()));
            }

            @Test
            @DisplayName("학부모 정보가 없으면 400을 반환한다")
            void 학부모없음_400을_반환한다() throws Exception {
                expectBadRequest(createKidRequest(null));
            }

            @Test
            @DisplayName("학부모 이름이 비어 있으면 400을 반환한다")
            void 학부모이름공백_400을_반환한다() throws Exception {
                expectBadRequest(createKidRequest(new ParentRequest(" ", "01012345678", Relationship.MOTHER)));
            }

            @Test
            @DisplayName("학부모 이름이 100자를 넘으면 400을 반환한다")
            void 학부모이름초과_400을_반환한다() throws Exception {
                expectBadRequest(createKidRequest(
                        new ParentRequest("가".repeat(101), "01012345678", Relationship.MOTHER)));
            }

            @Test
            @DisplayName("휴대폰 번호가 비어 있으면 400을 반환한다")
            void 휴대폰번호공백_400을_반환한다() throws Exception {
                expectBadRequest(createKidRequest(new ParentRequest("김엄마", "", Relationship.MOTHER)));
            }

            @Test
            @DisplayName("휴대폰 번호 형식이 틀리면 400을 반환한다")
            void 휴대폰번호형식오류_400을_반환한다() throws Exception {
                expectBadRequest(createKidRequest(new ParentRequest("김엄마", "010-12-ab", Relationship.MOTHER)));
            }

            @Test
            @DisplayName("관계가 없으면 400을 반환한다")
            void 관계없음_400을_반환한다() throws Exception {
                expectBadRequest(createKidRequest(new ParentRequest("김엄마", "01012345678", null)));
            }

            private void expectBadRequest(CreateKidRequest request) throws Exception {
                mockMvc.perform(post("/api/v1/kids")
                                .header(MEMBER_ID_HEADER, MEMBER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.isSuccess").value(false))
                        .andExpect(jsonPath("$.code").value(CommonErrorCode.BAD_REQUEST.getCode()));

                verifyNoInteractions(kidService);
            }
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/kids/{kidId} - 아이 정보 수정")
    class UpdateKid {

        private ResultActions patchKid(Object body) throws Exception {
            return mockMvc.perform(patch("/api/v1/kids/{kidId}", KID_ID)
                    .header(MEMBER_ID_HEADER, MEMBER_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body)));
        }

        private UpdateKidRequest captureRequest() {
            ArgumentCaptor<UpdateKidRequest> captor = ArgumentCaptor.forClass(UpdateKidRequest.class);
            verify(kidService).updateKid(eq(MEMBER_ID), eq(KID_ID), captor.capture());
            return captor.getValue();
        }

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("아이 정보를 수정하고 200을 반환한다")
            void 수정_200을_반환한다() throws Exception {
                given(kidService.updateKid(eq(MEMBER_ID), eq(KID_ID), any(UpdateKidRequest.class)))
                        .willReturn(new UpdateKidResponse(KID_ID));

                patchKid(new UpdateKidRequest("김민교", BIRTH_DATE, Gender.FEMALE,
                        new UpdateParentRequest("김민지", "01012345678", Relationship.MOTHER),
                        StringNode.valueOf("우유 알레르기가 있습니다.")))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value(true))
                        .andExpect(jsonPath("$.code").value(KidSuccessCode.KID_UPDATED_OK.getCode()))
                        .andExpect(jsonPath("$.result.kidId").value(KID_ID));
            }

            @Test
            @DisplayName("note 필드를 보내지 않으면 note가 null(기존 값 유지)로 전달된다")
            void note미전달_null로_전달된다() throws Exception {
                patchKid(Map.of("name", "김민교"))
                        .andExpect(status().isOk());

                assertThat(captureRequest().note()).isNull();
            }

            @Test
            @DisplayName("note를 null로 보내면 삭제 요청(hasNote=true, noteValue=null)으로 전달된다")
            void note_null_삭제요청으로_전달된다() throws Exception {
                Map<String, Object> body = new HashMap<>();
                body.put("note", null);

                patchKid(body)
                        .andExpect(status().isOk());

                UpdateKidRequest request = captureRequest();
                assertThat(request.hasNote()).isTrue();
                assertThat(request.noteValue()).isNull();
            }

            @Test
            @DisplayName("note를 문자열로 보내면 그 값으로 전달된다")
            void note문자열_값으로_전달된다() throws Exception {
                patchKid(Map.of("note", "우유 알레르기가 있습니다."))
                        .andExpect(status().isOk());

                assertThat(captureRequest().noteValue()).isEqualTo("우유 알레르기가 있습니다.");
            }
        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("아이가 없거나 담당하지 않으면 KID_NOT_FOUND로 404를 반환한다")
            void 수정_아이없음_404를_반환한다() throws Exception {
                given(kidService.updateKid(eq(MEMBER_ID), eq(KID_ID), any(UpdateKidRequest.class)))
                        .willThrow(new GeneralException(KidErrorCode.KID_NOT_FOUND));

                patchKid(Map.of("name", "김민교"))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.code").value(KidErrorCode.KID_NOT_FOUND.getCode()));
            }

            @Test
            @DisplayName("연결된 학부모가 없으면 PARENT_NOT_FOUND로 404를 반환한다")
            void 수정_학부모없음_404를_반환한다() throws Exception {
                given(kidService.updateKid(eq(MEMBER_ID), eq(KID_ID), any(UpdateKidRequest.class)))
                        .willThrow(new GeneralException(KidErrorCode.PARENT_NOT_FOUND));

                patchKid(Map.of("parent", Map.of("name", "김민지")))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.code").value(KidErrorCode.PARENT_NOT_FOUND.getCode()));
            }

            @Test
            @DisplayName("아이 이름이 공백이면 400을 반환한다")
            void 수정_이름공백_400을_반환한다() throws Exception {
                expectUpdateBadRequest(Map.of("name", " "));
            }

            @Test
            @DisplayName("아이 이름이 100자를 넘으면 400을 반환한다")
            void 수정_이름초과_400을_반환한다() throws Exception {
                expectUpdateBadRequest(Map.of("name", "가".repeat(101)));
            }

            @Test
            @DisplayName("생년월일이 미래면 400을 반환한다")
            void 수정_생년월일미래_400을_반환한다() throws Exception {
                expectUpdateBadRequest(Map.of("birthDate", LocalDate.now().plusDays(1).toString()));
            }

            @Test
            @DisplayName("학부모 이름이 공백이면 400을 반환한다")
            void 수정_학부모이름공백_400을_반환한다() throws Exception {
                expectUpdateBadRequest(Map.of("parent", Map.of("name", " ")));
            }

            @Test
            @DisplayName("학부모 이름이 100자를 넘으면 400을 반환한다")
            void 수정_학부모이름초과_400을_반환한다() throws Exception {
                expectUpdateBadRequest(Map.of("parent", Map.of("name", "가".repeat(101))));
            }

            @Test
            @DisplayName("휴대폰 번호 형식이 틀리면 400을 반환한다")
            void 수정_휴대폰번호형식오류_400을_반환한다() throws Exception {
                expectUpdateBadRequest(Map.of("parent", Map.of("phone", "010-12-ab")));
            }

            @Test
            @DisplayName("note가 문자열이 아니면 400을 반환한다")
            void 수정_note타입오류_400을_반환한다() throws Exception {
                expectUpdateBadRequest(Map.of("note", 123));
            }

            private void expectUpdateBadRequest(Object body) throws Exception {
                patchKid(body)
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.isSuccess").value(false))
                        .andExpect(jsonPath("$.code").value(CommonErrorCode.BAD_REQUEST.getCode()));

                verifyNoInteractions(kidService);
            }
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/kids/{kidId} - 아이 삭제")
    class DeleteKid {

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("아이를 삭제하고 result 없이 200을 반환한다")
            void 삭제_200을_반환한다() throws Exception {
                mockMvc.perform(delete("/api/v1/kids/{kidId}", KID_ID).header(MEMBER_ID_HEADER, MEMBER_ID))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value(true))
                        .andExpect(jsonPath("$.code").value(KidSuccessCode.KID_DELETED_OK.getCode()))
                        .andExpect(jsonPath("$.result").value(nullValue()));

                verify(kidService).deleteKid(MEMBER_ID, KID_ID);
            }
        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("아이가 없거나 담당하지 않으면 KID_NOT_FOUND로 404를 반환한다")
            void 삭제_아이없음_404를_반환한다() throws Exception {
                willThrow(new GeneralException(KidErrorCode.KID_NOT_FOUND))
                        .given(kidService).deleteKid(MEMBER_ID, KID_ID);

                mockMvc.perform(delete("/api/v1/kids/{kidId}", KID_ID).header(MEMBER_ID_HEADER, MEMBER_ID))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.isSuccess").value(false))
                        .andExpect(jsonPath("$.code").value(KidErrorCode.KID_NOT_FOUND.getCode()));
            }
        }
    }
}
