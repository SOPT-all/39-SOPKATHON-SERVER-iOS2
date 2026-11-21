package org.sopt.sopkathon.domain.message.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.domain.message.code.ApiMessageErrors;
import org.sopt.sopkathon.domain.message.code.ApiMessageSuccess;
import org.sopt.sopkathon.domain.message.code.MessageErrorCode;
import org.sopt.sopkathon.domain.message.code.MessageSuccessCode;
import org.sopt.sopkathon.domain.message.dto.request.MessageRequest;
import org.sopt.sopkathon.domain.message.dto.response.MessageResponse;
import org.sopt.sopkathon.domain.message.service.MessageService;
import org.sopt.sopkathon.global.code.CommonErrorCode;
import org.sopt.sopkathon.global.swagger.ApiCommonErrors;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController implements MessageApi {

    private final MessageService messageService;

    @Override
    @ApiMessageSuccess(MessageSuccessCode.BRIEFING_CREATED)
    @ApiCommonErrors({
            CommonErrorCode.BAD_REQUEST,
            CommonErrorCode.INTERNAL_SERVER_ERROR
    })
    @ApiMessageErrors({
            MessageErrorCode.KID_NOT_FOUND_OR_NOT_ASSIGNED,
            MessageErrorCode.NO_CARE_RECORDS,
            MessageErrorCode.AI_RESPONSE_FAILED
    })
    public MessageResponse generateDailyBriefing(MessageRequest request) {
        return messageService.generateDailyBriefing(request);
    }
}
