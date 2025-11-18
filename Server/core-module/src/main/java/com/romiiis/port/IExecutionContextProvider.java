package com.romiiis.port;

import com.romiiis.domain.User;

/**
 * Provides information about the current caller (authenticated user or system).
 */
public interface IExecutionContextProvider {

    /**
     * Sets the current caller user (for testing or system operations).
     *
     * @param user the user to set as the current caller
     */
    void setCaller(User user);


    /**
     * @return the current caller user, or null if running as system
     */
    User getCaller();


    /**
     * Clears the current caller context.
     */
    void clear();

}

