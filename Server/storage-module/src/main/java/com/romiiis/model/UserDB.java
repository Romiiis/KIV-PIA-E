package com.romiiis.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Document(collection = "users")
public class UserDB {

    @Id
    private UUID id;
    private String name;
    private String emailAddress;
    private UserRoleDB role;
    private Set<Locale> languages;
    private Instant createdAt;

    public UserDB() {
    }


}
