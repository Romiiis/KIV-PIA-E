package com.romiiis.model;

import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Document(collection = "projects")
public class ProjectDB {
    @Id
    private UUID id;
    private UserDB customer;
    private UserDB translator;
    private Locale targetLanguage;
    private byte[] sourceFile;
    private byte[] translatedFile;
    private ProjectStateDB state;
    private Instant createdAt;

    public ProjectDB() {
    }

}
