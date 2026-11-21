package org.sopt.sopkathon.domain.briefing.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.domain.briefing.code.ApiBriefingSuccess;
import org.sopt.sopkathon.domain.briefing.code.BriefingSuccessCode;
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
}
