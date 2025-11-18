package com.romiiis.security;

import com.romiiis.domain.User;
import com.romiiis.port.IExecutionContextProvider;
import com.romiiis.repository.IUserRepository;
import com.romiiis.service.api.IUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExecutionContextFilter extends OncePerRequestFilter {

    private final IExecutionContextProvider callerContextProvider;
    private final IUserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof String principal) {
                UUID userId = UUID.fromString(principal);

                Optional<User> userService = userRepository.getUserById(userId);

                if (userService.isEmpty()) {
                    log.warn("Authenticated user with ID {} not found in database", userId);
                } else {
                    callerContextProvider.setCaller(userService.get());
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            callerContextProvider.clear();
        }
    }
}
