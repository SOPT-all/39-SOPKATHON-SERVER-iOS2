package org.sopt.sopkathon.domain.message.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.domain.message.dto.request.MessageRequest;
import org.sopt.sopkathon.domain.message.dto.response.MessageResponse;
import org.sopt.sopkathon.domain.message.service.MessageService;
import org.sopt.sopkathon.global.code.CommonSuccessCode;
import org.sopt.sopkathon.global.swagger.ApiCommonSuccess;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController implements MessageApi {

    private final MessageService messageService;

    @Override
    @ApiCommonSuccess(CommonSuccessCode.OK)
    public MessageResponse generateDailyBriefing(MessageRequest request) {
        return messageService.generateDailyBriefing(request);
    }
}
