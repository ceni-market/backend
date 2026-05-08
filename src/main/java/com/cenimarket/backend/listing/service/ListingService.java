package com.cenimarket.backend.listing.service;

import com.cenimarket.backend.category.domain.Category;
import com.cenimarket.backend.category.repository.CategoryRepository;
import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.listing.domain.ListingImage;
import com.cenimarket.backend.listing.dto.request.ListingCreateRequest;
import com.cenimarket.backend.listing.dto.response.ListingCreateResponse;
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

    public ListingCreateResponse createListing(ListingCreateRequest request) {
        User seller = userRepository.findById(request.getSellerId()).orElseThrow();
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
}
