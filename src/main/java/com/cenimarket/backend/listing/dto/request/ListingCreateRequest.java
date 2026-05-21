package com.cenimarket.backend.listing.dto.request;

import com.cenimarket.backend.listing.domain.ListingType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ListingCreateRequest {
    @NotNull
    private Long categoryId;

    @NotBlank
    @Size(max = 150)
    private String title;

    @NotBlank
    private String description;

    @NotNull
    @Min(0)
    private Integer price;

    @NotNull
    private ListingType type;

    private List<String> imageUrls;

}
