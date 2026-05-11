package com.cenimarket.backend.upload.controller;

import com.cenimarket.backend.global.response.ApiResponse;
import com.cenimarket.backend.upload.dto.ImageUploadResponse;
import com.cenimarket.backend.upload.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/uploads")
public class ImageUploadController {
    private final ImageUploadService imageUploadService;

    @PostMapping("/images")
    public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadImage(
            @RequestParam("files") List<MultipartFile> files
    ) {
        ImageUploadResponse response = imageUploadService.uploadImages(files);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
