package com.romiiis;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Slf4j
public class Project {
    private UUID id;
    private User customer;
    private User translator;
    private Locale targetLanguage;
    private byte[] sourceFile;
    private byte[] translatedFile;
    private ProjectState state;
    private Instant createdAt;

    // MAX 5MB
    public static final int MAX_FILE_SIZE = 1024 * 1024 * 5;

    public Project() {

    }

    // constructor used when referencing the object in other domain objects where only ID is known
    public Project(UUID id) {
        this.id = id;
    }

    // constructor used when referencing the full object
    public Project(User customer, Locale targetLanguage, byte[] sourceFile) {
        this.id = UUID.randomUUID();
        this.customer = customer;
        this.translator = null;
        this.targetLanguage = targetLanguage;
        this.sourceFile = sourceFile;
        this.translatedFile = null;
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
        // TODO: check that user is a TRANSLATOR
        // TODO: check that project state is CREATED

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
        // TODO: check that project state is CREATED or APPROVED

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
     * @param translatedFile The translated file to be uploaded
     * @throws IllegalStateException if the project is not in the correct state or if the translated file is invalid
     */
    public void complete(byte[] translatedFile) {
        // TODO: check that project state is ASSIGNED
        // TODO: check that translatedFile is not empty but also not too big

        if (this.getState() != ProjectState.ASSIGNED) {
            log.error("Project is not in state ASSIGNED");
            throw new IllegalStateException("Project cannot be completed");
        }

        if (translatedFile == null || translatedFile.length > MAX_FILE_SIZE || translatedFile.length == 0) {

            log.error("Translated file is empty or too big");
            throw new IllegalStateException("Translated file is empty or too big");
        }

        this.translatedFile = translatedFile;
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
        // TODO: check that project state is COMPLETED
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
        // TODO: check that project state is COMPLETED
        // TODO: check that feedbackText is not empty

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

    //<editor-fold desc="getters" defaultstate="collapsed">
    public User getCustomer() {
        return customer;
    }

    public User getTranslator() {
        return translator;
    }

    public Locale getTargetLanguage() {
        return targetLanguage;
    }

    public byte[] getSourceFile() {
        return sourceFile;
    }

    public byte[] getTranslatedFile() {
        return translatedFile;
    }

    public ProjectState getState() {
        return state;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    //</editor-fold>
}
