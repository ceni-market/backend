package com.cenimarket.backend.chat.service;

import com.cenimarket.backend.category.domain.Category;
import com.cenimarket.backend.category.repository.CategoryRepository;
import com.cenimarket.backend.chat.dto.request.TestListingCreateRequest;
import com.cenimarket.backend.chat.repository.TestListingRepository;
import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.listing.domain.ListingImage;
import com.cenimarket.backend.listing.dto.request.ListingCreateRequest;
import com.cenimarket.backend.listing.repository.ListingImageRepository;
import com.cenimarket.backend.listing.repository.ListingRepository;
import com.cenimarket.backend.user.domain.User;
import com.cenimarket.backend.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestService {
    private final TestListingRepository listingRepository;
    private final ListingImageRepository listingImageRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public TestService(TestListingRepository listingRepository, UserRepository userRepository, ListingImageRepository listingImageRepository, ListingImageRepository listingImageRepository1, CategoryRepository categoryRepository) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
        this.listingImageRepository = listingImageRepository1;
        this.categoryRepository = categoryRepository;
    }

    public List<Listing> getListingList() {
        return listingRepository.findTop20ByOrderByIdDesc();
    }

    //게시글 등록
    public void createListing(TestListingCreateRequest request) {
        System.out.println("셀러의 이메일은" + request.getSellerEmail());
        User seller = userRepository.findByEmail(request.getSellerEmail()).orElseThrow();
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
    }


    public Optional<Listing> getListing(Long listingId) {
        return listingRepository.findByIdWithUser(listingId);
    }
}
