package com.romiiis;

import com.romiiis.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    private User customer;
    private User translator;
    private final byte[] file = new byte[1024];

    @BeforeEach
    void setUp() {
        customer = User.createCustomer("romisp", "romisp@students.zcu.cz");

        translator = User.createTranslator("barborkaM",
                "barborkaM@students.zcu.cz",
                new HashSet<>(Collections.singleton(Locale.ENGLISH)));

        new Random().nextBytes(file);
    }


    @DisplayName("assignTranslator should assign translator and change state to ASSIGNED")
    @Test
    void assignTranslator_shouldAssignAndChangeState() {
        Project project = new Project(customer, Locale.ENGLISH, "source.txt");

        project.assignTranslator(translator);

        assertEquals(ProjectState.ASSIGNED, project.getState());
        assertEquals(translator, project.getTranslator());
    }

    @DisplayName("assignTranslator should throw IllegalArgumentException for non-translator user")
    @Test
    void assignTranslator_shouldThrowForInvalidRole() {
        Project project = new Project(customer, Locale.ENGLISH, "source.txt");

        assertThrows(IllegalArgumentException.class, () -> project.assignTranslator(customer));
    }

    @DisplayName("assignTranslator should throw IllegalStateException if project not in CREATED state")
    @Test
    void assignTranslator_shouldThrowIfAlreadyAssigned() {
        Project project = new Project(customer, Locale.ENGLISH, "source.txt");
        project.assignTranslator(translator);

        assertThrows(IllegalStateException.class, () -> project.assignTranslator(translator));
    }


    @DisplayName("complete should mark project as COMPLETED when valid")
    @Test
    void complete_shouldMarkAsCompleted() {
        Project project = new Project(customer, Locale.ENGLISH, "source.txt");
        project.assignTranslator(translator);

        project.complete("translated.txt");

        assertEquals(ProjectState.COMPLETED, project.getState());
        assertEquals("translated.txt", project.getTranslatedFileName());
    }

    @DisplayName("complete should throw when state is not ASSIGNED")
    @Test
    void complete_shouldThrowWhenNotAssigned() {
        Project project = new Project(customer, Locale.ENGLISH, "source.txt");

        assertThrows(IllegalStateException.class, () -> project.complete("translated.txt"));
    }

    @DisplayName("complete should throw when translated file name is null")
    @Test
    void complete_shouldThrowWhenFileNull() {
        Project project = new Project(customer, Locale.ENGLISH, "source.txt");
        project.assignTranslator(translator);

        assertThrows(IllegalStateException.class, () -> project.complete(null));
    }


    @DisplayName("approve should set state to APPROVED when in COMPLETED state")
    @Test
    void approve_shouldSetApproved() {
        Project project = new Project(customer, Locale.ENGLISH, "source.txt");
        project.assignTranslator(translator);
        project.complete("translated.txt");

        project.approve();

        assertEquals(ProjectState.APPROVED, project.getState());
    }

    @DisplayName("approve should throw when not in COMPLETED state")
    @Test
    void approve_shouldThrowWhenInvalidState() {
        Project project = new Project(customer, Locale.ENGLISH, "source.txt");

        assertThrows(IllegalStateException.class, project::approve);
    }


    @DisplayName("reject should change state back to ASSIGNED and return feedback")
    @Test
    void reject_shouldChangeStateAndReturnFeedback() {
        Project project = new Project(customer, Locale.ENGLISH, "source.txt");
        project.assignTranslator(translator);
        project.complete("translated.txt");

        Feedback feedback = project.reject("Incorrect translation");

        assertNotNull(feedback);
        assertEquals(ProjectState.ASSIGNED, project.getState());
        assertEquals("Incorrect translation", feedback.getText());
    }

    @DisplayName("reject should throw when not in COMPLETED state")
    @Test
    void reject_shouldThrowWhenNotCompleted() {
        Project project = new Project(customer, Locale.ENGLISH, "source.txt");

        assertThrows(IllegalStateException.class, () -> project.reject("Feedback"));
    }

    @DisplayName("reject should throw when feedback text empty")
    @Test
    void reject_shouldThrowWhenFeedbackEmpty() {
        Project project = new Project(customer, Locale.ENGLISH, "source.txt");
        project.assignTranslator(translator);
        project.complete("translated.txt");

        assertThrows(IllegalStateException.class, () -> project.reject(""));
    }


    @DisplayName("close should change state to CLOSED when CREATED")
    @Test
    void close_shouldCloseFromCreated() {
        Project project = new Project(customer, Locale.ENGLISH, "source.txt");

        project.close();

        assertEquals(ProjectState.CLOSED, project.getState());
    }

    @DisplayName("close should change state to CLOSED when APPROVED")
    @Test
    void close_shouldCloseFromApproved() {
        Project project = new Project(customer, Locale.ENGLISH, "source.txt");
        project.assignTranslator(translator);
        project.complete("translated.txt");
        project.approve();

        project.close();

        assertEquals(ProjectState.CLOSED, project.getState());
    }

    @DisplayName("close should throw when state is not CREATED or APPROVED")
    @Test
    void close_shouldThrowWhenInvalidState() {
        Project project = new Project(customer, Locale.ENGLISH, "source.txt");
        project.assignTranslator(translator);

        assertThrows(IllegalStateException.class, project::close);
    }
}
