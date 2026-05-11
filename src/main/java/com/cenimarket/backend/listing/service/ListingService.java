package com.cenimarket.backend.listing.service;

import com.cenimarket.backend.category.domain.Category;
import com.cenimarket.backend.category.repository.CategoryRepository;
import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.listing.domain.ListingImage;
import com.cenimarket.backend.listing.dto.request.ListingCreateRequest;
import com.cenimarket.backend.listing.dto.request.ListingStatusUpdateRequest;
import com.cenimarket.backend.listing.dto.request.ListingUpdateRequest;
import com.cenimarket.backend.listing.dto.response.ListingCreateResponse;
import com.cenimarket.backend.listing.dto.response.ListingDeleteResponse;
import com.cenimarket.backend.listing.dto.response.ListingStatusUpdateResponse;
import com.cenimarket.backend.listing.dto.response.ListingUpdateResponse;
import com.cenimarket.backend.listing.repository.ListingImageRepository;
import com.cenimarket.backend.listing.repository.ListingRepository;
import com.cenimarket.backend.user.domain.User;
import com.cenimarket.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ListingService {
    private final ListingRepository listingRepository;
    private final ListingImageRepository listingImageRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    //게시글 등록
    public ListingCreateResponse createListing(Long sellerId, ListingCreateRequest request) {
        User seller = userRepository.findById(sellerId).orElseThrow();
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow();
        // listing 엔티티생성
        Listing listing = Listing.create(
                seller,
                category,
                request.getTitle(),
                request.getDescription(),
                request.getPrice(),
                request.getType()
                );
        Listing savedListing = listingRepository.save(listing);

        if(request.getImageUrls() != null) {
            for(int i=0; i<request.getImageUrls().size(); i++){
                String imageUrl = request.getImageUrls().get(i);
                // ListingImage 엔티티생성
                ListingImage listingImage = ListingImage.create(
                        savedListing,
                        imageUrl,
                        i
                );
                listingImageRepository.save(listingImage);
            }
        }

        return new ListingCreateResponse(savedListing.getId());
    }
    //게시글 수정
    public ListingUpdateResponse updateListing(Long listingId, ListingUpdateRequest request) {
        Listing listing = listingRepository.findById(listingId).orElseThrow();
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow();

        listing.update(
                category,
                request.getTitle(),
                request.getDescription(),
                request.getPrice(),
                request.getType()
        );

        return new ListingUpdateResponse(listing.getId());
    }
    //게시글 삭제
    public ListingDeleteResponse deleteListing(Long listingId) {
        Listing listing = listingRepository.findById(listingId).orElseThrow();
        listing.delete();
        return new ListingDeleteResponse(listing.getId());
    }
    //게시글 상태변경
    public ListingStatusUpdateResponse updateListingStatus(
            Long listingId,
            ListingStatusUpdateRequest request) {
        Listing listing = listingRepository.findById(listingId).orElseThrow();
        listing.changeStatus(request.getStatus());
        return new ListingStatusUpdateResponse(listing.getId(), listing.getStatus());
    }

}
