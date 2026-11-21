package org.sopt.sopkathon.domain.image.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record PresignedUrlResponse(
        @Schema(
                description = "이미지를 PUT으로 업로드할 수 있는 5분짜리 프리사인드 URL",
                example = "https://example-bucket.s3.ap-southeast-2.amazonaws.com/images/example.jpg?X-Amz-Expires=300"
        )
        String presignedUrl,

        @Schema(
                description = "S3에 이미지가 저장되는 객체 키",
                example = "images/550e8400-e29b-41d4-a716-446655440000-sample.jpg"
        )
        String fileKey
) {
}
