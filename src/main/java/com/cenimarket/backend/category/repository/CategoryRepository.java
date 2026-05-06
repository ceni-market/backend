package com.cenimarket.backend.category.repository;

import com.cenimarket.backend.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // 최상위 카테고리 중 활성화된 카테고리를 정렬 순서대로 조회
    List<Category> findByParentIsNullAndActiveTrueOrderBySortOrderAsc();
    // 특정 부모 카테고리의 하위 카테고리 중 활성화된 카테고리를 정렬 순서대로 조회
    List<Category> findByParent_IdAndActiveTrueOrderBySortOrderAsc(Long parentId);
}
