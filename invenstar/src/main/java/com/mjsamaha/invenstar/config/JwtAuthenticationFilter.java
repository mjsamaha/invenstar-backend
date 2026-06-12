package com.mjsamaha.invenstar.config;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mjsamaha.invenstar.user.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService             jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
        this.jwtService         = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest  request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain         filterChain
    ) throws ServletException, IOException {

        // 1. Pull the Authorization header
        String authHeader = request.getHeader("Authorization");

        // 2. If missing or not a Bearer token, skip — Spring handles it as unauthenticated
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Slice off "Bearer " to get the raw token
        String token    = authHeader.substring(7);
        String username = null;

        try {
            username = jwtService.extractUsername(token);
        } catch (Exception e) {
            // Malformed or tampered token — pass through as unauthenticated
            filterChain.doFilter(request, response);
            return;
        }

        // 4. Only authenticate if we have a username and no auth is set yet
        //    (avoids re-processing on the same request)
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 5. Validate signature, expiry, and username match
            if (jwtService.isTokenValid(token, userDetails)) {

                // 6. Build an authenticated token and attach request details
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,                          // credentials null — already verified
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 7. Push into SecurityContext — downstream knows this request is authenticated
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 8. Always continue the chain regardless of outcome
        filterChain.doFilter(request, response);
    }
}