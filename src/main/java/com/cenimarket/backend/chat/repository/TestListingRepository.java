package com.cenimarket.backend.chat.repository;

import com.cenimarket.backend.listing.domain.Listing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TestListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findTop20ByOrderByIdDesc();


    @Query("select l from Listing l join fetch l.seller where l.id = :id")
    Optional<Listing> findByIdWithUser(@Param("id") Long id);
}
