package com.romiiis.security;

import com.romiiis.util.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Loads security rules from external YAML file (security-rules.yaml).
 * Automatically binds properties with prefix "security".
 */
@Component
@PropertySource(value = "classpath:security-rules.yaml", factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "security")
@Data
public class SecurityRulesLoader {

    /**
     * List of configured security rules.
     * Always initialized to avoid NullPointerExceptions.
     */
    private List<SecurityRule> rules = new ArrayList<>();

    @Data
    public static class SecurityRule {
        private String path;
        private String method;
        private String access;
        private List<String> roles;
    }
}
