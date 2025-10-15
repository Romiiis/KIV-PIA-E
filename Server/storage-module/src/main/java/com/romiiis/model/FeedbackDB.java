package com.romiiis.model;

import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

/**
 * Feedback database entity
 */
@Document(collection = "feedbacks")
@Data
public class FeedbackDB {
    @Id
    private UUID id;
    @DBRef
    private ProjectDB projectDB;
    private String text;
    private Instant createdAt;

    public FeedbackDB() {
    }
}
