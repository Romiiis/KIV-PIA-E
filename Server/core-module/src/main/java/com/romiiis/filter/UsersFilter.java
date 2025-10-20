package com.romiiis.filter;

import com.romiiis.domain.UserRole;
import lombok.Getter;

/**
 * Filter criteria for querying users.
 * @author Roman Pejs
 */
@Getter
public class UsersFilter {

    /** Filter by user role */
    private UserRole role = null;

    /** Filter by language code */
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
