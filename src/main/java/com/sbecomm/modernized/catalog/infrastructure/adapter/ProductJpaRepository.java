package com.sbecomm.modernized.catalog.infrastructure.adapter;

import com.sbecomm.modernized.catalog.infrastructure.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, String> {
    Page<ProductEntity> findByCategoryId(String categoryId, Pageable pageable);
}
