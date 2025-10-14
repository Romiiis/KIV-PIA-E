package com.romiiis.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

/**
 * Feedback database entity
 */
@Document(collection = "feedbacks")
public class FeedbackDB {
    private UUID id;
    private ProjectDB projectDB;
    private String text;
    private Instant createdAt;

    public FeedbackDB() {
    }
}
