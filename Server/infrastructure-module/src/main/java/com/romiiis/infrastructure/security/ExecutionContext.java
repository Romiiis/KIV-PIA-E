package com.romiiis.infrastructure.security;

import com.romiiis.domain.User;
import com.romiiis.port.IExecutionContextProvider;

/**
 * Thread-local context storing current user and/or system mode.
 */
public class ExecutionContext implements IExecutionContextProvider {

    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();
    private static final ThreadLocal<Boolean> systemMode = ThreadLocal.withInitial(() -> false);

    public void setCaller(User user) {

        currentUser.set(user);
        systemMode.set(false);
    }

    public void setSystem() {
        currentUser.remove();
        systemMode.set(true);
    }

    public User getCaller() {
        return currentUser.get();
    }

    public boolean isSystem() {
        return systemMode.get();
    }

    public void clear() {
        currentUser.remove();
        systemMode.remove();
    }

    public <T> T runAsSystem(java.util.function.Supplier<T> action) {
        User prevUser = currentUser.get();
        boolean prevSystem = systemMode.get();
        try {
            setSystem();
            return action.get();
        } finally {
            if (prevUser != null) setCaller(prevUser);
            else if (prevSystem) setSystem();
            else clear();
        }
    }


}
