package com.sbecomm.modernized.catalog.infrastructure.adapter;

import com.sbecomm.modernized.catalog.infrastructure.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, String> {
}
