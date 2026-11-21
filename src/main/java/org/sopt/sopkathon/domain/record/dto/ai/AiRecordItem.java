package org.sopt.sopkathon.domain.record.dto.ai;

import org.sopt.sopkathon.domain.record.domain.RecordCategory;

public record AiRecordItem(
        String kidName,
        RecordCategory category,
        String content
) {
}
