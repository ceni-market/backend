package com.cenimarket.backend.upload.service;

import com.cenimarket.backend.upload.dto.ImageUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageUploadService {

    // 실제 파일 저장 경로와 클라이언트에 반환할 이미지 URL 경로
    private static final String UPLOAD_DIR = "uploads/images";
    private static final String IMAGE_URL_PREFIX = "/uploads/images/";

    public ImageUploadResponse uploadImages(List<MultipartFile> files) {

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("업로드할 이미지가 없습니다.");
        }

        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다.");
            }
            String contentType = file.getContentType();

            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("이미지 파일만 업로드 할 수 있습니다.");
            }

            try {
                Files.createDirectories(Paths.get(UPLOAD_DIR));
                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null) {
                    throw new IllegalArgumentException("파일명이 올바르지 않습니다.");
                }

                int dotIndex = originalFilename.lastIndexOf(".");

                if (dotIndex == -1 || dotIndex == originalFilename.length() - 1) {
                    throw new IllegalArgumentException("확장자가 없는 파일은 업로드할 수 없습니다.");
                }
                // 원본 확장자는 유지하고 파일명만 UUID로 변경한다.
                String extension = originalFilename.substring(dotIndex);
                String saveFilename = UUID.randomUUID() + extension;

                // 파일은 로컬 폴더에 저장하고, 응답에는 접근 가능한 URL만 담는다.
                Path path = Paths.get(UPLOAD_DIR).resolve(saveFilename);
                file.transferTo(path);
                imageUrls.add(IMAGE_URL_PREFIX + saveFilename);

            } catch (IOException e){
                throw new IllegalStateException("이미지 저장에 실패했습니다.", e);
            }

        }

        return new ImageUploadResponse(imageUrls);
    }
}
