package com.cenimarket.backend.like.domain;

import com.cenimarket.backend.global.domain.BaseEntity;
import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "listing_likes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_listing_likes_user_listing",
                        columnNames = {"user_id", "listing_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ListingLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @Builder
    private ListingLike(User user, Listing listing) {
        this.user = user;
        this.listing = listing;
    }

    public static ListingLike create(User user, Listing listing) {
        return ListingLike.builder()
                .user(user)
                .listing(listing)
                .build();
    }

}
