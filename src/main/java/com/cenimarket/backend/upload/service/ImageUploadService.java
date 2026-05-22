package com.cenimarket.backend.upload.service;

import com.cenimarket.backend.upload.dto.ImageUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageUploadService {
    private static final int MAX_IMAGE_COUNT = 10;
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    public ImageUploadResponse uploadImages(List<MultipartFile> files) {

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("업로드할 이미지가 없습니다.");
        }

        if (files.size() > MAX_IMAGE_COUNT) {
            throw new IllegalArgumentException("이미지는 최대 10장까지 업로드할 수 있습니다.");
        }

        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            validateImage(file);

            try {
                String originalFilename = file.getOriginalFilename();

                int dotIndex = originalFilename.lastIndexOf(".");
                String extension = originalFilename.substring(dotIndex);

                String saveFilename = UUID.randomUUID() + extension;
                String key = "uploads/images/" + saveFilename;

                PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType(file.getContentType())
                        .contentLength(file.getSize())
                        .build();

                s3Client.putObject(
                        request,
                        RequestBody.fromInputStream(file.getInputStream(), file.getSize())
                );

                String imageUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
                imageUrls.add(imageUrl);

            } catch (IOException e) {
                throw new IllegalStateException("이미지 업로드에 실패했습니다.", e);
            }
        }

        return new ImageUploadResponse(imageUrls);
    }

    private void validateImage(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다.");
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("이미지는 개당 5MB 이하만 업로드할 수 있습니다.");
        }

        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 할 수 있습니다.");
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null) {
            throw new IllegalArgumentException("파일명이 올바르지 않습니다.");
        }

        int dotIndex = originalFilename.lastIndexOf(".");

        if (dotIndex == -1 || dotIndex == originalFilename.length() - 1) {
            throw new IllegalArgumentException("확장자가 없는 파일은 업로드할 수 없습니다.");
        }
    }
}
