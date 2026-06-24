package com.sbecomm.modernized.promotion.presentation.rest;

import com.sbecomm.modernized.promotion.application.dto.request.CreatePromotionRequest;
import com.sbecomm.modernized.promotion.application.dto.response.PromotionResponse;
import com.sbecomm.modernized.promotion.application.port.PromotionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
@Slf4j
public class PromotionController {

    private final PromotionUseCase promotionUseCase;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromotionResponse> createPromotion(@Valid @RequestBody CreatePromotionRequest request) {
        log.info("Admin requested to create promotion: {}", request.code());
        return ResponseEntity.status(HttpStatus.CREATED).body(promotionUseCase.createPromotion(request));
    }
}
