package com.romiiis.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

/**
 * ProjectDB entity representing a translation project in the database.
 * Contains references to customer and translator, language details, files, state, and timestamps.
 *
 * @author Roman Pejs
 */
@Document(collection = "projects")
@Data
@NoArgsConstructor
public class ProjectDB {
    @Id
    private UUID id;

    @DBRef
    private UserDB customer;
    @DBRef
    private UserDB translator;

    private Locale targetLanguage;
    private String originalFileName;
    private String translatedFileName;
    private ProjectStateDB state;
    private Instant createdAt;

}
