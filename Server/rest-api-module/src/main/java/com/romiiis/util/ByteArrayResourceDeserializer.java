package com.romiiis.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.util.Base64;

/**
 * Custom deserializer for ByteArrayResource.
 * Converts a Base64-encoded string from JSON into a ByteArrayResource object.
 */
public class ByteArrayResourceDeserializer extends JsonDeserializer<ByteArrayResource> {
    @Override
    public ByteArrayResource deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        String binaryString = parser.getText();

        byte[] bytes = Base64.getDecoder().decode(binaryString);

        return new ByteArrayResource(bytes);
    }
}
