package com.romiiis.configuration;

import com.romiiis.domain.UserRole;
import lombok.Getter;

/**
 * Filter criteria for querying users.
 *
 * @author Roman Pejs
 */
@Getter
public class UsersFilter {

    private UserRole role = null;
    private String languageCode = null;

    public UsersFilter() {
    }

    public UsersFilter setRole(UserRole role) {
        this.role = role;
        return this;
    }

    public UsersFilter setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
        return this;
    }


}
