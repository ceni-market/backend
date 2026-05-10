package com.cenimarket.backend.category.config;

import com.cenimarket.backend.category.domain.Category;
import com.cenimarket.backend.category.domain.DefaultCategory;
import com.cenimarket.backend.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CategoryDataInitializer implements ApplicationRunner {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (DefaultCategory defaultCategory : DefaultCategory.values()) {
            // 서버 재시작 시 이미 저장된 기본 카테고리는 다시 만들지 않는다.
            if (categoryRepository.existsByName(defaultCategory.getDisplayName())) {
                continue;
            }

            categoryRepository.save(Category.create(defaultCategory.getDisplayName(), defaultCategory.getSortOrder()));
        }
    }
}
