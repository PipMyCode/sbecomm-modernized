package com.sbecomm.modernized.common.config;

import com.sbecomm.modernized.order.application.dto.event.OrderPlacedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        // explicitly instantiate ObjectMapper within an immutable configuration bean layer
        // to guarantee zero thread-pinning anomalies during asynchronous Jackson serialization.
        // Jackson 3 JsonMapper is immutable once built.
        return JsonMapper.builder()
                .findAndAddModules()
                .build();
    }
}
