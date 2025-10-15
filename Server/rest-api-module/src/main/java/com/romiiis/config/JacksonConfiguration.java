package com.romiiis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.romiiis.util.ByteArrayResourceDeserializer;
import com.romiiis.util.ByteArrayResourceSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Configuration class for customizing Jackson's ObjectMapper.
 * This configuration registers custom serializers and deserializers
 * for handling ByteArrayResource objects.
 */
@Configuration
public class JacksonConfiguration {

    /**
     * Configures the ObjectMapper with custom serializers and deserializers.
     *
     * @param builder the Jackson2ObjectMapperBuilder used to build the ObjectMapper
     * @return a customized ObjectMapper instance
     */
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.build();

        SimpleModule module = new SimpleModule();
        module.addDeserializer(Resource.class, new ByteArrayResourceDeserializer());
        module.addDeserializer(ByteArrayResource.class, new ByteArrayResourceDeserializer());
        module.addSerializer(ByteArrayResource.class, new ByteArrayResourceSerializer());
        objectMapper.registerModule(module);

        return objectMapper;
    }
}
