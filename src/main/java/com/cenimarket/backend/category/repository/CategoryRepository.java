package com.cenimarket.backend.category.repository;

import com.cenimarket.backend.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 카테고리를 정렬 순서대로 조회
    List<Category> findAllByOrderBySortOrderAsc();

    // 기본 카테고리 자동 저장 시 중복 생성을 막기 위해 이름 존재 여부 확인
    boolean existsByName(String name);
}
