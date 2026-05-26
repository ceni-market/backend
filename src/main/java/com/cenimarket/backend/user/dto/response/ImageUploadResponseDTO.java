package com.cenimarket.backend.user.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class ImageUploadResponseDTO {
    private List<String> imageUrls; // 브라우저가 접근할 수 있는 URL 리스트
}