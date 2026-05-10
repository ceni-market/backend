package com.cenimarket.backend.listing.dto.request;

import com.cenimarket.backend.listing.domain.ListingType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ListingUpdateRequest {
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

}
