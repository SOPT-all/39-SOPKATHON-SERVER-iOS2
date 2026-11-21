package org.sopt.sopkathon.domain.briefing.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.domain.briefing.code.ApiBriefingErrors;
import org.sopt.sopkathon.domain.briefing.code.ApiBriefingSuccess;
import org.sopt.sopkathon.domain.briefing.code.BriefingErrorCode;
import org.sopt.sopkathon.domain.briefing.code.BriefingSuccessCode;
import org.sopt.sopkathon.domain.briefing.dto.request.BriefingCreateRequest;
import org.sopt.sopkathon.domain.briefing.dto.request.BriefingRefineRequest;
import org.sopt.sopkathon.domain.briefing.dto.response.BriefingContentResponse;
import org.sopt.sopkathon.domain.briefing.dto.response.BriefingListResponse;
import org.sopt.sopkathon.domain.briefing.service.BriefingService;
import org.sopt.sopkathon.domain.member.code.ApiMemberErrors;
import org.sopt.sopkathon.domain.member.code.MemberErrorCode;
import org.sopt.sopkathon.global.code.CommonErrorCode;
import org.sopt.sopkathon.global.swagger.ApiCommonErrors;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BriefingController implements BriefingApi {
    private final BriefingService briefingService;

    @Override
    @ApiBriefingSuccess(BriefingSuccessCode.BRIEFING_LIST_OK)
    @ApiCommonErrors({CommonErrorCode.BAD_REQUEST})
    @ApiMemberErrors({MemberErrorCode.MEMBER_NOT_FOUND})
    public BriefingListResponse getBriefings(Long memberId) {
        return briefingService.getBriefings(memberId);
    }

    @Override
    @ApiBriefingSuccess(BriefingSuccessCode.BRIEFING_CREATED)
    @ApiCommonErrors({CommonErrorCode.BAD_REQUEST})
    @ApiBriefingErrors({
            BriefingErrorCode.KID_NOT_FOUND_OR_NOT_ASSIGNED,
            BriefingErrorCode.NO_CARE_RECORDS,
            BriefingErrorCode.AI_RESPONSE_FAILED
    })
    public BriefingContentResponse createBriefing(Long memberId, BriefingCreateRequest request) {
        return briefingService.createBriefing(memberId, request);
    }

    @Override
    @ApiBriefingSuccess(BriefingSuccessCode.BRIEFING_REFINED)
    @ApiCommonErrors({CommonErrorCode.BAD_REQUEST})
    @ApiBriefingErrors({
            BriefingErrorCode.BRIEFING_NOT_FOUND,
            BriefingErrorCode.AI_RESPONSE_FAILED
    })
    public BriefingContentResponse refineBriefing(Long memberId, Long briefingId, BriefingRefineRequest request) {
        return briefingService.refineBriefing(memberId, briefingId, request);
    }
}
