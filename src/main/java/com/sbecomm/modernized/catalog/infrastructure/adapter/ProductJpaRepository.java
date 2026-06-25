package com.sbecomm.modernized.catalog.infrastructure.adapter;

import com.sbecomm.modernized.catalog.infrastructure.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, String> {
    Page<ProductEntity> findByCategoryId(String categoryId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductEntity p WHERE p.id = :id")
    Optional<ProductEntity> findByIdForUpdate(@Param("id") String id);
}
