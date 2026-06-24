package com.sbecomm.modernized.promotion.application.service;

import com.sbecomm.modernized.promotion.application.dto.request.CreatePromotionRequest;
import com.sbecomm.modernized.promotion.application.dto.response.PromotionResponse;
import com.sbecomm.modernized.promotion.application.port.PromotionUseCase;
import com.sbecomm.modernized.promotion.domain.model.Promotion;
import com.sbecomm.modernized.promotion.domain.repository.PromotionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionService implements PromotionUseCase {

    private final PromotionRepository promotionRepository;

    @Override
    @Transactional
    public PromotionResponse createPromotion(CreatePromotionRequest request) {
        log.info("Creating promotion with code: {}", request.code());
        Promotion promotion = new Promotion(
                UUID.randomUUID().toString(),
                request.code().toUpperCase(),
            request.discountPercentage(),
            true,
            request.expiresAt(),
            request.maxUses(),
            0
        );
        return toResponse(promotionRepository.save(promotion));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PromotionResponse> validateCode(String code) {
        log.debug("Validating promotion code: {}", code);
        return promotionRepository.findByCode(code.toUpperCase())
                .filter(Promotion::isActive)
                .filter(p -> p.getExpiresAt() == null || java.time.LocalDateTime.now().isBefore(p.getExpiresAt()))
                .filter(p -> p.getMaxUses() == null || p.getCurrentUses() < p.getMaxUses())
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public void consumeCode(String code) {
        log.info("Consuming promotion code: {}", code);
        Promotion promotion = promotionRepository.findByCode(code.toUpperCase())
            .orElseThrow(() -> new IllegalArgumentException("Promotion not found: " + code));
        
        promotion.consume();
        promotionRepository.save(promotion);
    }

    private PromotionResponse toResponse(Promotion promotion) {
        return new PromotionResponse(
                promotion.getId(),
            promotion.getCode(),
            promotion.getDiscountPercentage(),
            promotion.isActive(),
            promotion.getExpiresAt(),
            promotion.getMaxUses(),
            promotion.getCurrentUses()
        );
    }
}
