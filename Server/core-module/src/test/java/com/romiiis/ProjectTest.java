package com.romiiis;

import com.romiiis.domain.Project;
import com.romiiis.domain.ProjectState;
import com.romiiis.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ProjectTest {

    private User customer;
    private User translator;
    private final byte[] file = new byte[1024];

    @BeforeEach
    void setUp() {
        customer = User.createCustomer("romisp",
                "romisp@students.zcu.cz");

        translator = User.createTranslator("barborkaM",
                "barborkaM@students.zcu.cz",
                new HashSet<>(Collections.singleton(Locale.ENGLISH)));


        new Random().nextBytes(file);

    }

    @DisplayName("Assign translator successfully")
    @Test
    void assignTranslator_1() {
        Project project = new Project(customer, Locale.ENGLISH, file);
        // Check if the translator is empty before
        assert project.getTranslator() == null;
        project.assignTranslator(translator);
        assert project.getTranslator().equals(translator);
    }

    @DisplayName("Close successfully States CREATED & APPROVED")
    @Test
    void close_1() {
        Project project = new Project(customer, Locale.ENGLISH, file);

        // Check if the state is CREATED
        assert project.getState() == ProjectState.CREATED;

        // Close the project in CREATED state
        project.close();

        // Check if the state is CLOSED
        assert project.getState() == ProjectState.CLOSED;


        // Recreate the project for the next test
        project = new Project(customer, Locale.ENGLISH, file);

        // Check if the state is CREATED
        assert project.getState() == ProjectState.CREATED;

        // Assign translator and complete the project
        project.assignTranslator(translator);
        project.complete(file);

        // Approve the project
        project.approve();

        // Check if the state is APPROVED
        assert project.getState() == ProjectState.APPROVED;

        // Close the project in APPROVED state
        project.close();

        // Check if the state is CLOSED
        assert project.getState() == ProjectState.CLOSED;
    }

    @DisplayName("Close unsuccessfully - invalid states")
    @Test
    void close_2() {
        Project project = new Project(customer, Locale.ENGLISH, file);
        project.assignTranslator(translator);

        assert project.getState() == ProjectState.ASSIGNED;

        // This should throw an exception because the state is ASSIGNED
        assertThrows(IllegalStateException.class, project::close);

        project.complete(file);

        assert project.getState() == ProjectState.COMPLETED;

        // This should throw an exception because the state is COMPLETED
        assertThrows(IllegalStateException.class, project::close);

        project.approve();

        project.close();

        assert project.getState() == ProjectState.CLOSED;

        assertThrows(IllegalStateException.class, project::close);
    }


    @DisplayName("Complete successfully")
    @Test
    void complete_1() {
        Project project = new Project(customer, Locale.ENGLISH, file);
        project.assignTranslator(translator);

        assert project.getState() == ProjectState.ASSIGNED;

        project.complete(file);

        assert project.getState() == ProjectState.COMPLETED;

    }

    @DisplayName("Complete unsuccessfully - invalid state")
    @Test
    void complete_2() {
        Project project = new Project(customer, Locale.ENGLISH, file);

        assert project.getState() == ProjectState.CREATED;

        // This should throw an exception because the state is CREATED
        assertThrows(IllegalStateException.class, () -> project.complete(file));

        project.assignTranslator(translator);
        project.complete(file);

        assert project.getState() == ProjectState.COMPLETED;

        // This should throw an exception because the state is COMPLETED
        assertThrows(IllegalStateException.class, () -> project.complete(file));

        project.approve();
        project.close();

        assert project.getState() == ProjectState.CLOSED;

        // This should throw an exception because the state is CLOSED
        assertThrows(IllegalStateException.class, () -> project.complete(file));
    }

    @DisplayName("Complete unsuccessfully - invalid file")
    @Test
    void complete_3() {
        Project project = new Project(customer, Locale.ENGLISH, file);
        project.assignTranslator(translator);

        assert project.getState() == ProjectState.ASSIGNED;

        // This should throw an exception because the file is null
        assertThrows(IllegalStateException.class, () -> project.complete(null));

        // This should throw an exception because the file is empty
        assertThrows(IllegalStateException.class, () -> project.complete(new byte[0]));

        // This should throw an exception because the file is too big
        assertThrows(IllegalStateException.class, () -> project.complete(new byte[Project.MAX_FILE_SIZE + 1]));

    }

    @DisplayName("Approve successfully")
    @Test
    void approve_1() {
        Project project = new Project(customer, Locale.ENGLISH, file);
        project.assignTranslator(translator);
        project.complete(file);

        assert project.getState() == ProjectState.COMPLETED;

        project.approve();

        assert project.getState() == ProjectState.APPROVED;
    }


    @DisplayName("Approve unsuccessfully - invalid state")
    @Test
    void approve_2() {
        Project project = new Project(customer, Locale.ENGLISH, file);

        assert project.getState() == ProjectState.CREATED;

        // This should throw an exception because the state is CREATED
        assertThrows(IllegalStateException.class, project::approve);

        project.assignTranslator(translator);

        assert project.getState() == ProjectState.ASSIGNED;

        // This should throw an exception because the state is ASSIGNED
        assertThrows(IllegalStateException.class, project::approve);

        project.complete(file);

        assert project.getState() == ProjectState.COMPLETED;

        project.approve();

        assert project.getState() == ProjectState.APPROVED;

        // This should throw an exception because the state is APPROVED
        assertThrows(IllegalStateException.class, project::approve);

        project.close();

        assert project.getState() == ProjectState.CLOSED;

        // This should throw an exception because the state is CLOSED
        assertThrows(IllegalStateException.class, project::approve);
    }



    @DisplayName("Reject successfully")
    @Test
    void reject_1() {
        Project project = new Project(customer, Locale.ENGLISH, file);
        project.assignTranslator(translator);
        project.complete(file);

        assert project.getState() == ProjectState.COMPLETED;

        project.reject("Not good enough");

        assert project.getState() == ProjectState.ASSIGNED;
    }

    @DisplayName("Reject unsuccessfully - invalid state")
    @Test
    void reject_2() {
        Project project = new Project(customer, Locale.ENGLISH, file);

        assert project.getState() == ProjectState.CREATED;

        // This should throw an exception because the state is CREATED
        assertThrows(IllegalStateException.class, () -> project.reject("Not good enough"));

        project.assignTranslator(translator);
        assert project.getState() == ProjectState.ASSIGNED;

        // This should throw an exception because the state is ASSIGNED
        assertThrows(IllegalStateException.class, () -> project.reject("Not good enough"));

        project.complete(file);
        assert project.getState() == ProjectState.COMPLETED;

        project.reject("Not good enough");
        assert project.getState() == ProjectState.ASSIGNED;

        project.complete(file);
        project.approve();

        assert project.getState() == ProjectState.APPROVED;

        // This should throw an exception because the state is APPROVED
        assertThrows(IllegalStateException.class, () -> project.reject("Not good enough"));
    }
}