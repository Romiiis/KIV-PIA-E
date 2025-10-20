package com.romiiis.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

/**
 * Project domain object
 * Represents a translation project
 *
 * @author Roman Pejs
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@Slf4j
public class Project {
    private UUID id;
    private User customer;
    private User translator;
    private Locale targetLanguage;
    private String originalFileName;
    private String translatedFileName;
    private ProjectState state;
    private Instant createdAt;


    // constructor used when referencing the object in other domain objects where only ID is known
    public Project(UUID id) {
        this.id = id;
    }

    // constructor used when referencing the full object
    public Project(User customer, Locale targetLanguage, String originalFileName) {
        this.id = UUID.randomUUID();
        this.customer = customer;
        this.translator = null;
        this.targetLanguage = targetLanguage;
        this.originalFileName = originalFileName;
        this.state = ProjectState.CREATED;
        this.createdAt = Instant.now();
    }

    /**
     * Assigns a translator to the project and changes the state to ASSIGNED.
     * <br><br>
     * REQUIREMENTS:
     * <ol>
     *     <li>User must have the role of TRANSLATOR.</li>
     *     <li>Project state must be CREATED.</li>
     * </ol>
     *
     * @param user The translator to be assigned
     * @throws IllegalArgumentException if the user is not a translator
     */
    public void assignTranslator(User user) {
        if (user == null || user.getRole() != UserRole.TRANSLATOR) {
            log.error("User is not a translator");
            throw new IllegalArgumentException("Invalid user role");
        }

        if (this.getState() != ProjectState.CREATED) {
            log.error("Project is already created");
            throw new IllegalStateException("Project is already created");
        }

        this.translator = user;
        this.state = ProjectState.ASSIGNED;
    }

    /**
     * Closes the project and changes the state to CLOSED.
     * <br><br>
     * REQUIREMENTS:
     *
     * <ol>
     *     <li>Project state must be either CREATED or APPROVED.</li>
     *     </ol>
     *
     * @throws IllegalStateException if the project is not in the correct state
     *
     */
    public void close() {
        if (this.getState() != ProjectState.CREATED && this.getState() != ProjectState.APPROVED) {
            log.error("Project is not CREATED or ASSIGNED");
            throw new IllegalStateException("Project cannot be closed");
        }

        this.state = ProjectState.CLOSED;
    }

    /**
     * Completes the project by uploading the translated file and changing the state to COMPLETED.
     * <br><br>
     * REQUIREMENTS:
     * <ol>
     *     <li>Project state must be ASSIGNED.</li>
     *     <li>translatedFile must not be empty and it must not be too big.</li>
     * </ol>
     *
     * @param translatedFileName The translated file to be uploaded
     * @throws IllegalStateException if the project is not in the correct state or if the translated file is invalid
     */
    public void complete(String translatedFileName) {
        if (this.getState() != ProjectState.ASSIGNED) {
            log.error("Project is not in state ASSIGNED");
            throw new IllegalStateException("Project cannot be completed");
        }

        if (translatedFileName == null) {

            log.error("Translated file name is empty");
            throw new IllegalStateException("Translated file name is empty");
        }

        this.translatedFileName = translatedFileName;
        this.state = ProjectState.COMPLETED;
    }


    /**
     * Approves the project and changes the state to APPROVED.
     * <br><br>
     * REQUIREMENTS:
     * <ol>
     *     <li>Project state must be COMPLETED.</li>
     *     </ol>
     *
     * @throws IllegalStateException if the project is not in the correct state
     */
    public void approve() {
        if (this.getState() != ProjectState.COMPLETED) {
            log.error("Project is not in state COMPLETED");
            throw new IllegalStateException("Project cannot be approved");
        }

        this.state = ProjectState.APPROVED;
    }

    /**
     * Rejects the project, changes the state back to ASSIGNED, and creates a Feedback object.
     * <br><br>
     * REQUIREMENTS:
     * <ol>
     *     <li>Project state must be COMPLETED.</li>
     *     <li>feedbackText must not be empty.</li>
     *     </ol>
     *
     * @param feedbackText the feedback text explaining the rejection
     * @return a Feedback object containing the rejection details
     * @throws IllegalStateException if the project is not in the correct state or if the feedback text is empty
     */
    public Feedback reject(String feedbackText) {
        if (this.getState() != ProjectState.COMPLETED) {
            log.error("Project is not in state COMPLETED");
            throw new IllegalStateException("Project cannot be rejected");
        }

        if (feedbackText == null || feedbackText.isEmpty()) {
            log.error("Feedback text is empty");
            throw new IllegalStateException("Feedback text  is empty");
        }

        this.state = ProjectState.ASSIGNED;

        return new Feedback(this, feedbackText);
    }

}
