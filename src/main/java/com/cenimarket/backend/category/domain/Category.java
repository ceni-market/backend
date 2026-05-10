package com.cenimarket.backend.category.domain;

import com.cenimarket.backend.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Builder
    private Category(String name, Integer sortOrder){
        this.name = name;
        this.sortOrder = sortOrder;
    }

    public static Category create(String name, Integer sortOrder) {
        return Category.builder()
                .name(name)
                .sortOrder(sortOrder)
                .build();
    }
}
