package com.cenimarket.backend.category.service;

import com.cenimarket.backend.category.domain.Category;
import com.cenimarket.backend.category.dto.CategoryItemResponse;
import com.cenimarket.backend.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryItemResponse> getCategories() {
        // DB에 저장된 카테고리를 화면 노출 순서대로 조회한다.
        List<Category> categories = categoryRepository.findAllByOrderBySortOrderAsc();

        // DTO의 정적 팩토리 메서드에 변환 책임을 위임한다.
        return categories
                .stream()
                .map(CategoryItemResponse::from)
                .toList();
    }
}
