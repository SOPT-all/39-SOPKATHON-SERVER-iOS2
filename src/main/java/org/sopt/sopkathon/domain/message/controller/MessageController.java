package org.sopt.sopkathon.domain.message.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.domain.message.code.ApiMessageErrors;
import org.sopt.sopkathon.domain.message.code.ApiMessageSuccess;
import org.sopt.sopkathon.domain.message.code.MessageErrorCode;
import org.sopt.sopkathon.domain.message.code.MessageSuccessCode;
import org.sopt.sopkathon.domain.message.dto.request.MessageCreateRequest;
import org.sopt.sopkathon.domain.message.dto.request.MessagePolishRequest;
import org.sopt.sopkathon.domain.message.dto.response.MessageResponse;
import org.sopt.sopkathon.domain.message.service.MessageService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController implements MessageApi {

    private final MessageService messageService;

    @Override
    @ApiMessageSuccess(MessageSuccessCode.BRIEFING_CREATED)
    @ApiMessageErrors({
            MessageErrorCode.KID_NOT_FOUND_OR_NOT_ASSIGNED,
            MessageErrorCode.NO_CARE_RECORDS,
            MessageErrorCode.AI_RESPONSE_FAILED
    })
    public MessageResponse generateDailyBriefing(Long memberId, MessageCreateRequest request) {
        return messageService.generateDailyBriefing(memberId, request);
    }

    @Override
    @ApiMessageSuccess(MessageSuccessCode.BRIEFING_REFINED)
    @ApiMessageErrors({
            MessageErrorCode.BRIEFING_NOT_FOUND,
            MessageErrorCode.AI_RESPONSE_FAILED
    })
    public MessageResponse refineBriefing(Long memberId, Long briefingId, MessagePolishRequest request) {
        return messageService.refineBriefing(memberId, briefingId, request);
    }
}
