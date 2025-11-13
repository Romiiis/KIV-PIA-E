package com.romiiis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@SpringBootApplication
@Slf4j
public class CoreUiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreUiApplication.class, args);
    }

    @Bean
    CommandLineRunner logStartupInfo(
            Environment env,
            @Value("${spring.data.mongodb.database}") String mongoUri
    ) {
        return args -> {

            String os = System.getProperty("os.name") + " " + System.getProperty("os.version");
            String java = System.getProperty("java.version");
            String userDir = System.getProperty("user.dir");

            String port = env.getProperty("server.port", "8080");
            String address = env.getProperty("server.address", "localhost");
            String activeProfile = Arrays.toString(env.getActiveProfiles());

            String dbName = mongoUri.isEmpty() ? "unknown" :
                    mongoUri.substring(mongoUri.lastIndexOf("/") + 1)
                            .replaceAll("\\?.*$", "");

            log.info("""
                    \n
                    =============================================================
                        🚀  APPLICATION STARTED SUCCESSFULLY
                    =============================================================

                        🧩 Environment   : {}
                        💻 OS            : {}
                        ☕ Java version  : {}
                        📦 Working dir   : {}

                        🌐 Server URL    : http://{}:{}
                        🗄️  Database      : MongoDB ({})
                       \s
                    =============================================================
                   \s""",
                    activeProfile,
                    os,
                    java,
                    userDir,
                    address,
                    port,
                    dbName
            );
        };
    }

}
