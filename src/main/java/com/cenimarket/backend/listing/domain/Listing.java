package com.cenimarket.backend.listing.domain;

import com.cenimarket.backend.category.domain.Category;
import com.cenimarket.backend.global.domain.SoftDeleteEntity;
import com.cenimarket.backend.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "listings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Listing extends SoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @OneToMany(mappedBy = "listing")
    private List<ListingImage> images;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ListingStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ListingType type;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    @Builder
    private Listing(User seller,
                    Category category,
                    String title,
                    String description,
                    Integer price,
                    ListingType type)
    {
        this.seller = seller;
        this.category = category;
        this.title = title;
        this.description = description;
        this.price = price;
        this.type = type;
        this.viewCount = 0;
        this.status = ListingStatus.ACTIVE;
        this.likeCount = 0;
    }

    public static Listing create(User seller,
                                 Category category,
                                 String title,
                                 String description,
                                 Integer price,
                                 ListingType type){
        return Listing.builder()
                .seller(seller)
                .category(category)
                .title(title)
                .description(description)
                .price(price)
                .type(type)
                .build();
    }

    public void update(
            Category category,
            String title,
            String description,
            Integer price,
            ListingType type
    ) {
        this.category = category;
        this.title = title;
        this.description = description;
        this.price = price;
        this.type = type;
    }
    public void delete(){
        this.status = ListingStatus.DELETED;
        softDelete();
    }

    public void changeStatus(ListingStatus status) {
        this.status = status;
    }

    public void increaseLikeCount(){
        this.likeCount++;
    }

    public void decreaseLikeCount(){
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

}
