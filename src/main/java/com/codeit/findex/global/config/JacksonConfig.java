package com.codeit.findex.global.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    // JSON 정수를 String 필드로 역직렬화 허용 (numeric ID → String)
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCoercionCustomizer() {
        return builder -> builder.postConfigurer(mapper ->
                mapper.coercionConfigFor(String.class)
                        .setCoercion(CoercionInputShape.Integer, CoercionAction.TryConvert)
        );
    }
}
