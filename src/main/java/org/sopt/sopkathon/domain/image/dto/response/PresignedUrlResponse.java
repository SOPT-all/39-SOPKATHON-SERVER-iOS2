package org.sopt.sopkathon.domain.image.dto.response;

public record PresignedUrlResponse(
        String presignedUrl,
        String fileKey
) {
}
