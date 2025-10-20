package com.romiiis.configuration;

import com.romiiis.service.DefaultJwtServiceImpl;
import com.romiiis.service.interfaces.IJwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    public IJwtService jwtService(JwtProperties props) {
        return new DefaultJwtServiceImpl(props);
    }
}
