package com.romiiis.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

public abstract class AbstractController {

    /**
     * Retrieves the current HTTP response from the request context.
     *
     * @return the current HttpServletResponse
     * @throws IllegalStateException if there is no active HTTP request context
     */
    protected HttpServletResponse getCurrentResponse() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            throw new IllegalStateException("No active HTTP request context");
        }
        return attrs.getResponse();
    }

    /**
     * Retrieves the current HTTP request from the request context.
     *
     * @return the current HttpServletRequest
     * @throws IllegalStateException if there is no active HTTP request context
     */
    protected HttpServletRequest getCurrentRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }
}
