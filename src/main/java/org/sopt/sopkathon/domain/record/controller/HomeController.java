package org.sopt.sopkathon.domain.record.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.domain.record.dto.response.HomeResponse;
import org.sopt.sopkathon.domain.record.service.HomeService;
import org.sopt.sopkathon.global.code.CommonSuccessCode;
import org.sopt.sopkathon.global.swagger.ApiCommonSuccess;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeController implements HomeApi {

    private final HomeService homeService;

    @Override
    @ApiCommonSuccess(CommonSuccessCode.OK)
    public HomeResponse getHome(Long memberId) {
        return homeService.getHome(memberId);
    }
}
