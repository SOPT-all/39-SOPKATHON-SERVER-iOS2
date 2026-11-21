package org.sopt.sopkathon.domain.kid.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.domain.kid.code.ApiKidErrors;
import org.sopt.sopkathon.domain.kid.code.ApiKidSuccess;
import org.sopt.sopkathon.domain.kid.code.KidErrorCode;
import org.sopt.sopkathon.domain.kid.code.KidSuccessCode;
import org.sopt.sopkathon.domain.kid.dto.request.CreateKidRequest;
import org.sopt.sopkathon.domain.kid.dto.request.UpdateKidRequest;
import org.sopt.sopkathon.domain.kid.dto.response.UpdateKidResponse;
import org.sopt.sopkathon.domain.kid.dto.response.CreateKidResponse;
import org.sopt.sopkathon.domain.kid.dto.response.DeleteKidResponse;
import org.sopt.sopkathon.domain.kid.dto.response.KidDetailResponse;
import org.sopt.sopkathon.domain.kid.dto.response.KidListResponse;
import org.sopt.sopkathon.domain.kid.service.KidService;
import org.sopt.sopkathon.domain.member.code.ApiMemberErrors;
import org.sopt.sopkathon.domain.member.code.MemberErrorCode;
import org.sopt.sopkathon.global.code.CommonErrorCode;
import org.sopt.sopkathon.global.swagger.ApiCommonErrors;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KidController implements KidApi {
    private final KidService kidService;

    @Override
    @ApiKidSuccess(KidSuccessCode.KID_LIST_OK)
    @ApiCommonErrors({CommonErrorCode.BAD_REQUEST})
    @ApiMemberErrors({MemberErrorCode.MEMBER_NOT_FOUND})
    public KidListResponse getKids(Long memberId, String keyword) {
        return kidService.getKids(memberId, keyword);
    }

    @Override
    @ApiKidSuccess(KidSuccessCode.KID_DETAIL_OK)
    @ApiCommonErrors({CommonErrorCode.BAD_REQUEST})
    @ApiMemberErrors({MemberErrorCode.MEMBER_NOT_FOUND})
    @ApiKidErrors({KidErrorCode.KID_NOT_FOUND})
    public KidDetailResponse getKid(Long memberId, Long kidId) {
        return kidService.getKid(memberId, kidId);
    }

    @Override
    @ApiKidSuccess(KidSuccessCode.KID_CREATED)
    @ApiCommonErrors({CommonErrorCode.BAD_REQUEST})
    @ApiMemberErrors({MemberErrorCode.MEMBER_NOT_FOUND})
    public CreateKidResponse createKid(Long memberId, CreateKidRequest request) {
        return kidService.createKid(memberId, request);
    }

    @Override
    @ApiKidSuccess(KidSuccessCode.KID_UPDATED_OK)
    @ApiCommonErrors({CommonErrorCode.BAD_REQUEST})
    @ApiMemberErrors({MemberErrorCode.MEMBER_NOT_FOUND})
    @ApiKidErrors({KidErrorCode.KID_NOT_FOUND, KidErrorCode.PARENT_NOT_FOUND})
    public UpdateKidResponse updateKid(Long memberId, Long kidId, UpdateKidRequest request) {
        return kidService.updateKid(memberId, kidId, request);
    }

    @Override
    @ApiKidSuccess(KidSuccessCode.KID_DELETED_OK)
    @ApiCommonErrors({CommonErrorCode.BAD_REQUEST})
    @ApiMemberErrors({MemberErrorCode.MEMBER_NOT_FOUND})
    @ApiKidErrors({KidErrorCode.KID_NOT_FOUND})
    public DeleteKidResponse deleteKid(Long memberId, Long kidId) {
        kidService.deleteKid(memberId, kidId);
        return null;
    }
}
