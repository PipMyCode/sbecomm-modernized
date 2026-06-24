package com.sbecomm.modernized.promotion.infrastructure.adapter;

import com.sbecomm.modernized.promotion.domain.model.Promotion;
import com.sbecomm.modernized.promotion.domain.repository.PromotionRepository;
import com.sbecomm.modernized.promotion.infrastructure.entity.PromotionEntity;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PromotionRepositoryAdapter implements PromotionRepository {

    private final PromotionJpaRepository jpaRepository;

    @Override
    public Optional<Promotion> findByCode(String code) {
        return jpaRepository.findByCode(code).map(this::toDomain);
    }

    @Override
    public Promotion save(Promotion promotion) {
        PromotionEntity entity = toEntity(promotion);
        return toDomain(jpaRepository.save(entity));
    }

    private Promotion toDomain(PromotionEntity entity) {
        return new Promotion(
            entity.getId(),
            entity.getCode(),
            entity.getDiscountPercentage(),
            entity.isActive(),
            entity.getExpiresAt(),
            entity.getMaxUses(),
            entity.getCurrentUses()
        );
    }

    private PromotionEntity toEntity(Promotion promotion) {
        PromotionEntity entity = new PromotionEntity();
        entity.setId(promotion.getId());
        entity.setCode(promotion.getCode());
        entity.setDiscountPercentage(promotion.getDiscountPercentage());
        entity.setActive(promotion.isActive());
        entity.setExpiresAt(promotion.getExpiresAt());
        entity.setMaxUses(promotion.getMaxUses());
        entity.setCurrentUses(promotion.getCurrentUses() != null ? promotion.getCurrentUses() : 0);
        return entity;
    }
}
