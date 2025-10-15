package com.romiiis.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.beans.ConstructorProperties;
import java.time.Instant;
import java.util.UUID;

/**
 * Feedback database entity
 * Represents feedback given by a customer for a completed project
 */
@Document(collection = "feedbacks")
@Data
@NoArgsConstructor
public class FeedbackDB {
    @Id
    private UUID id;

    @DBRef
    private ProjectDB project;

    private String text;
    private Instant createdAt;
}
