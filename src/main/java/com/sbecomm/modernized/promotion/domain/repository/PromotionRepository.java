package com.sbecomm.modernized.promotion.domain.repository;

import com.sbecomm.modernized.promotion.domain.model.Promotion;
import java.util.Optional;

public interface PromotionRepository {
    Optional<Promotion> findByCode(String code);
    Promotion save(Promotion promotion);
}
