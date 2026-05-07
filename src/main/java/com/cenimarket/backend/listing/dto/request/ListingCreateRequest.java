package com.cenimarket.backend.listing.dto.request;

import com.cenimarket.backend.listing.domain.ListingType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ListingCreateRequest {
    private Long sellerId;
    private Long categoryId;
    private String title;
    private String description;
    private Integer price;
    private ListingType type;
    private List<String> imageUrls;

}
