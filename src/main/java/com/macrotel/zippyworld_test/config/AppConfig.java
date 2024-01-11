package com.macrotel.zippyworld_test.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AppConfig extends OncePerRequestFilter {
    @Value("${app.api.key}")
    private String apiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestApiKey = request.getHeader("x-api-key");
        String requestUrl = request.getRequestURI();

            if (requestApiKey == null || !requestApiKey.equals(apiKey)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"status\": false, \"error\": \"Invalid API key\"}");
                return;

            }
            filterChain.doFilter(request, response);
    }
}
