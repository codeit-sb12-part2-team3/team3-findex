package com.codeit.findex.infra.openapi;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "openapi")
public class OpenApiProperties {
    private String serviceKey;
    private String baseUrl;
}
