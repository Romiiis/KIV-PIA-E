package com.romiiis.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Feedback domain object
 * Represents feedback given by a customer for a completed project
 *
 * @author Roman Pejs
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
public class Feedback {
    private UUID id;
    private UUID projectId;
    private String text;
    private Instant createdAt;



    // constructor used when referencing the object in other domain objects where only ID is known
    public Feedback(UUID id) {
        this.id = id;
    }

    // constructor used when referencing the full object
    public Feedback(UUID projectId, String text) {
        this.id = UUID.randomUUID();
        this.projectId = projectId;
        this.text = text;
        this.createdAt = Instant.now();
    }


}
