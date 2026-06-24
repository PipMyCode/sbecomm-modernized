package com.sbecomm.modernized.promotion.application.port;

import com.sbecomm.modernized.promotion.application.dto.request.CreatePromotionRequest;
import com.sbecomm.modernized.promotion.application.dto.response.PromotionResponse;
import java.util.Optional;

public interface PromotionUseCase {
    PromotionResponse createPromotion(CreatePromotionRequest request);
    Optional<PromotionResponse> validateCode(String code);
    void consumeCode(String code);
}
