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
    protected UUID id;

    @DBRef
    protected UserDB customer;
    @DBRef
    protected UserDB translator;

    protected Locale targetLanguage;
    protected String originalFileName;
    protected String translatedFileName;
    protected ProjectStateDB state;
    protected Instant createdAt;

}
