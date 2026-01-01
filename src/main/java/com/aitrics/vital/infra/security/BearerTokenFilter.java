package com.aitrics.vital.infra.security;

import com.aitrics.vital.api.error.ErrorCode;
import com.aitrics.vital.api.error.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class BearerTokenFilter extends OncePerRequestFilter {
    
    @Value("${app.security.bearer-token:}")
    private String validToken;
    
    private final ObjectMapper objectMapper;
    
    public BearerTokenFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        
        if (isPublicPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorizedResponse(response, request.getRequestURI(), "Missing or invalid Authorization header");
            return;
        }
        
        String token = authHeader.substring(7).trim();
        
        if (validToken.isEmpty() || !validToken.equals(token)) {
            sendUnauthorizedResponse(response, request.getRequestURI(), "Invalid bearer token");
            return;
        }

        var authentication = new UsernamePasswordAuthenticationToken(
                "api-user",
                null,
                java.util.List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        filterChain.doFilter(request, response);
    }
    
    private boolean isPublicPath(String path) {
        return path.startsWith("/h2-console") || 
               path.startsWith("/swagger") || 
               path.startsWith("/v3/api-docs");
    }
    
    private void sendUnauthorizedResponse(HttpServletResponse response, String path, String message) 
            throws IOException {
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.UNAUTHORIZED, path, message);
        
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }
}