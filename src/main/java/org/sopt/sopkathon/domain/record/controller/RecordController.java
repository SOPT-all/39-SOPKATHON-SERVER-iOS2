package org.sopt.sopkathon.domain.record.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.domain.record.code.ApiRecordErrors;
import org.sopt.sopkathon.domain.record.code.ApiRecordSuccess;
import org.sopt.sopkathon.domain.record.code.RecordErrorCode;
import org.sopt.sopkathon.domain.record.code.RecordSuccessCode;
import org.sopt.sopkathon.domain.record.dto.request.RecordCreateRequest;
import org.sopt.sopkathon.domain.record.dto.response.RecordCreateResponse;
import org.sopt.sopkathon.domain.record.service.RecordService;
import org.sopt.sopkathon.global.code.CommonErrorCode;
import org.sopt.sopkathon.global.swagger.ApiCommonErrors;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecordController implements RecordApi {

    private final RecordService recordService;

    @Override
    @ApiRecordSuccess(RecordSuccessCode.RECORD_CREATED)
    @ApiCommonErrors({CommonErrorCode.BAD_REQUEST})
    @ApiRecordErrors({
            RecordErrorCode.KID_NOT_FOUND_OR_NOT_ASSIGNED,
            RecordErrorCode.RECORD_EXTRACTION_FAILED,
            RecordErrorCode.AI_SERVER_RESPONSE_FAILED
    })
    public RecordCreateResponse createRecords(
            Long memberId,
            RecordCreateRequest request
    ) {
        return recordService.createRecords(memberId, request);
    }
}
