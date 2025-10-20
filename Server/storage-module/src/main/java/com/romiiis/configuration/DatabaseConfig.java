package com.romiiis.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Configuration class for setting up MongoDB repositories.
 * This class enables the scanning of MongoDB repositories in the specified base package.
 *
 * @author Roman Pejs
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.romiiis.repository")
public class DatabaseConfig {
    // Configuration class for MongoDB repositories
}


