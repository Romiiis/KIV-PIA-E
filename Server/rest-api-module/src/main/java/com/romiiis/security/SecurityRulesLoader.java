package com.romiiis.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "security")
@Data
public class SecurityRulesLoader {
    private List<SecurityRule> rules;

    @Data
    public static class SecurityRule {
        private String path;
        private String method;
        private String access;
        private List<String> roles;
    }
}

