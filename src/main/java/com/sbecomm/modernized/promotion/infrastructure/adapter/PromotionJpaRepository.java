package com.sbecomm.modernized.promotion.infrastructure.adapter;

import com.sbecomm.modernized.promotion.infrastructure.entity.PromotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PromotionJpaRepository extends JpaRepository<PromotionEntity, String> {
    Optional<PromotionEntity> findByCode(String code);
}
