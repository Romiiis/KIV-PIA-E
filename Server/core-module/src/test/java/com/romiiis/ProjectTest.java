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
}