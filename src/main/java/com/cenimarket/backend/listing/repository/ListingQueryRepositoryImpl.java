package com.cenimarket.backend.listing.repository;

import com.cenimarket.backend.listing.domain.Listing;
import com.cenimarket.backend.listing.domain.ListingStatus;
import com.cenimarket.backend.listing.domain.ListingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ListingQueryRepositoryImpl implements ListingQueryRepositoryCustom {

    private final EntityManager em;

    @Override
    public Page<Listing> findAllBySearch(
            List<String> keywords,
            ListingType type,
            Long categoryId,
            ListingStatus status,
            Pageable pageable
    ) {
        Map<String, Object> params = new HashMap<>();

        StringBuilder jpql = new StringBuilder("""
            select l
            from Listing l
            join fetch l.category c
            where 1 = 1
        """);

        StringBuilder countJpql = new StringBuilder("""
            select count(l)
            from Listing l
            join l.category c
            where 1 = 1
        """);

        if (type != null) {
            jpql.append(" and l.type = :type");
            countJpql.append(" and l.type = :type");
            params.put("type", type);
        }

        if (categoryId != null) {
            jpql.append(" and c.id = :categoryId");
            countJpql.append(" and c.id = :categoryId");
            params.put("categoryId", categoryId);
        }

        if (status != null) {
            jpql.append(" and l.status = :status");
            countJpql.append(" and l.status = :status");
            params.put("status", status);
        }

        if (keywords != null && !keywords.isEmpty()) {
            jpql.append(" and (");
            countJpql.append(" and (");

            for (int i = 0; i < keywords.size(); i++) {
                if (i > 0) {
                    jpql.append(" or ");
                    countJpql.append(" or ");
                }

                jpql.append("""
                    lower(l.title) like lower(:keyword%s)
                    or lower(l.description) like lower(:keyword%s)
                """.formatted(i, i));

                countJpql.append("""
                    lower(l.title) like lower(:keyword%s)
                    or lower(l.description) like lower(:keyword%s)
                """.formatted(i, i));

                params.put("keyword" + i, "%" + keywords.get(i) + "%");
            }

            jpql.append(")");
            countJpql.append(")");
        }

        jpql.append(" order by l.id desc");

        TypedQuery<Listing> query = em.createQuery(jpql.toString(), Listing.class);
        TypedQuery<Long> countQuery = em.createQuery(countJpql.toString(), Long.class);

        params.forEach((key, value) -> {
            query.setParameter(key, value);
            countQuery.setParameter(key, value);
        });

        List<Listing> content = query
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = countQuery.getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }
}