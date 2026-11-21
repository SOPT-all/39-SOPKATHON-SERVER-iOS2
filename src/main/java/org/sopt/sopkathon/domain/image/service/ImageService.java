package org.sopt.sopkathon.domain.image.service;

import lombok.RequiredArgsConstructor;
import org.sopt.sopkathon.domain.image.code.ImageErrorCode;
import org.sopt.sopkathon.domain.image.dto.request.PresignedUrlRequest;
import org.sopt.sopkathon.domain.image.dto.response.PresignedUrlResponse;
import org.sopt.sopkathon.global.config.s3.S3Properties;
import org.sopt.sopkathon.global.exception.GeneralException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ImageService {
    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;

    public PresignedUrlResponse getPresignedUrl(PresignedUrlRequest request) {
        String objectKey = createObjectKey(request.fileName());
        PresignedPutObjectRequest presignedRequest =
                createPresignedRequest(request, objectKey);

        return PresignedUrlResponse.builder()
                .presignedUrl(presignedRequest.url().toString())
                .fileKey(objectKey)
                .build();
    }

    private String createObjectKey(String fileName) {
        String sanitizedFileName = fileName.replaceAll("[^가-힣A-Za-z0-9._-]", "_");
        String prefix = s3Properties.uploadPrefix();

        if (prefix == null || prefix.isBlank()) {
            return UUID.randomUUID() + "-" + sanitizedFileName;
        }

        return prefix.replaceAll("/+$", "") + "/"
                + UUID.randomUUID() + "-" + sanitizedFileName;
    }

    private PresignedPutObjectRequest createPresignedRequest(
            PresignedUrlRequest request,
            String objectKey
    ) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.bucket())
                    .key(objectKey)
                    .contentType(request.contentType())
                    .build();

            PutObjectPresignRequest presignRequest =
                    PutObjectPresignRequest.builder()
                            .signatureDuration(
                                    Duration.ofMinutes(
                                            s3Properties.presignedUrlExpirationMinutes()
                                    )
                            )
                            .putObjectRequest(putObjectRequest)
                            .build();

            return s3Presigner.presignPutObject(presignRequest);
        } catch (SdkException | IllegalArgumentException exception) {
            throw new GeneralException(ImageErrorCode.S3_PRESIGNED_URL_FAILED);
        }
    }
}
