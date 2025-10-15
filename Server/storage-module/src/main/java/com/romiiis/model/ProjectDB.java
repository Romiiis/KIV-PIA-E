package com.romiiis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Document(collection = "projects")
@Data
public class ProjectDB {
    @Id
    private UUID id;

    @DBRef
    private UserDB customer;
    @DBRef
    private UserDB translator;
    private Locale targetLanguage;
    private byte[] sourceFile;
    private byte[] translatedFile;
    private ProjectStateDB state;
    private Instant createdAt;

    public ProjectDB() {
    }

}
