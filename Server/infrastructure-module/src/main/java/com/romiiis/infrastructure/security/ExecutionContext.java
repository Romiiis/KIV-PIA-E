package com.romiiis.infrastructure.security;

import com.romiiis.domain.User;
import com.romiiis.port.IExecutionContextProvider;

/**
 * Thread-local context storing current user
 */
public class ExecutionContext implements IExecutionContextProvider {

    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();

    public void setCaller(User user) {
        currentUser.set(user);
    }

    public void setSystem() {
        currentUser.remove();
    }

    public User getCaller() {
        return currentUser.get();
    }

    public void clear() {
        currentUser.remove();
    }


}
