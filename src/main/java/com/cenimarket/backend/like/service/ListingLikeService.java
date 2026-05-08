package com.cenimarket.backend.like.service;

import com.cenimarket.backend.like.domain.ListingLike;
import com.cenimarket.backend.like.dto.ListingLikeResponse;
import com.cenimarket.backend.like.repository.ListingLikeRepository;
import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.listing.repository.ListingRepository;
import com.cenimarket.backend.user.domain.User;
import com.cenimarket.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ListingLikeService {
    private final ListingLikeRepository listingLikeRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    // 좋아요 생성 및 증가
    public ListingLikeResponse addLike(Long listingId, Long userId){
        User user = userRepository.findById(userId).orElseThrow();
        Listing listing = listingRepository.findById(listingId).orElseThrow();

        boolean alreadyLiked = listingLikeRepository.existsByUser_IdAndListing_Id(userId, listingId);

        if(!alreadyLiked){
            ListingLike listingLike = ListingLike.create(user, listing);
            listingLikeRepository.save(listingLike);
            listing.increaseLikeCount();
        }

        return new ListingLikeResponse(
                listing.getId(),
                true,
                listing.getLikeCount()
        );
    }
    // 좋아요 취소
    public ListingLikeResponse removeLike(Long listingId, Long userId){
        Listing listing = listingRepository.findById(listingId).orElseThrow();

        listingLikeRepository.findByUser_IdAndListing_Id(userId, listingId)
                .ifPresent(listingLike -> {
                    listingLikeRepository.delete(listingLike);
                    listing.decreaseLikeCount();
                });
        return new ListingLikeResponse(
                listing.getId(),
                false,
                listing.getLikeCount());
    }

}
