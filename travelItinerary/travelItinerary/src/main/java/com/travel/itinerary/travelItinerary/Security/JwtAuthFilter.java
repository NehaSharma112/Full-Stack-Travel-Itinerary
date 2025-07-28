package com.travel.itinerary.travelItinerary.Security;

import com.travel.itinerary.travelItinerary.Services.JwtHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtHelper jwtHelper;
    private final UserDetailsService userDetailsService;

    private final List<String> publicEndpoints = Arrays.asList(
            "/api/token/generate",
            "/api/auth/signup",
            "/api/auth/login", "/api/auth/google-auth",
            "/api/mail/send",
            "/api/payments/health",
            "/api/payments/config"
    );

    public JwtAuthFilter(JwtHelper jwtHelper, UserDetailsService userDetailsService) {
        this.jwtHelper = jwtHelper;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestPath = request.getRequestURI();

        if (isPublicEndpoint(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Authorization header missing or invalid");
            return;
        }

        try{
            String jwt = authHeader.substring(7);
            String username = jwtHelper.extractUserName(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtHelper.validateToken(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    request.setAttribute("authenticatedUsername", username);
                } else {
                    sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid or expired token");
                    return;
                }
            }

            filterChain.doFilter(request, response);
        }catch(Exception e){
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid token format: " + e.getMessage());
        }
    }

    private boolean isPublicEndpoint(String requestPath) {
        return publicEndpoints.stream().anyMatch(endpoint ->
                requestPath.equals(endpoint) || requestPath.startsWith(endpoint + "/")
        );
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"success\": false, \"message\": \"" + message + "\", \"data\": null}"
        );
    }
}
