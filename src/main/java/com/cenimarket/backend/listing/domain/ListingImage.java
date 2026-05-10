package com.cenimarket.backend.listing.domain;

import com.cenimarket.backend.global.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "listing_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ListingImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Builder
    private ListingImage (
            Listing listing,
            String imageUrl,
            Integer sortOrder)
    {
        this.listing = listing;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
    }

    public static ListingImage create(Listing listing, String imageUrl, Integer sortOrder) {
        return ListingImage.builder()
                .listing(listing)
                .imageUrl(imageUrl)
                .sortOrder(sortOrder)
                .build();
    }
}
