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
    public ListingCreateResponse createListing(Long sellerId,
                                               ListingCreateRequest request) {
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
    public ListingUpdateResponse updateListing(Long userId,
                                               Long listingId,
                                               ListingUpdateRequest request) {
        Listing listing = listingRepository.findById(listingId).orElseThrow();
        if (!listing.getSeller().getId().equals(userId)) {
            throw new IllegalArgumentException("본인이 작성한 게시글만 수정할 수 있습니다.");
        }

        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow();

        listing.update(
                category,
                request.getTitle(),
                request.getDescription(),
                request.getPrice(),
                request.getType()
        );

        if (request.getImageUrls() != null) {
            listingImageRepository.deleteByListing_Id(listingId);

            for (int i = 0; i < request.getImageUrls().size(); i++) {
                ListingImage listingImage = ListingImage.create(
                        listing,
                        request.getImageUrls().get(i),
                        i
                );
                listingImageRepository.save(listingImage);
            }
        }

        return new ListingUpdateResponse(listing.getId());
    }
    //게시글 삭제
    public ListingDeleteResponse deleteListing(Long userId,
                                               Long listingId) {
        Listing listing = listingRepository.findById(listingId).orElseThrow();
        if (!listing.getSeller().getId().equals(userId)) {
            throw new IllegalArgumentException("본인이 작성한 게시글만 삭제할 수 있습니다.");
        }

        listing.delete();
        return new ListingDeleteResponse(listing.getId());
    }
    //게시글 상태변경
    public ListingStatusUpdateResponse updateListingStatus(Long userId,
                                                           Long listingId,
                                                           ListingStatusUpdateRequest request) {
        Listing listing = listingRepository.findById(listingId).orElseThrow();
        if (!listing.getSeller().getId().equals(userId)) {
            throw new IllegalArgumentException("본인이 작성한 게시글만 상태 변경할 수 있습니다.");
        }

        listing.changeStatus(request.getStatus());
        return new ListingStatusUpdateResponse(listing.getId(), listing.getStatus());
    }

}
