package com.romiiis.security;

import com.romiiis.domain.User;
import com.romiiis.service.interfaces.IUserService;
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
import java.nio.file.attribute.UserPrincipal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExecutionContextFilter extends OncePerRequestFilter {

    private final CallerContextProvider callerContextProvider;
    private final IUserService userService;

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

                var user = callerContextProvider.runAsSystem(() -> userService.getUserById(userId));

                if (user != null) {
                    callerContextProvider.setCaller(user);
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            callerContextProvider.clear();
        }
    }
}
