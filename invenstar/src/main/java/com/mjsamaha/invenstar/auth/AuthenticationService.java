package com.mjsamaha.invenstar.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjsamaha.invenstar.auth.dto.AuthResponse;
import com.mjsamaha.invenstar.auth.dto.LoginRequest;
import com.mjsamaha.invenstar.auth.dto.MessageResponse;
import com.mjsamaha.invenstar.auth.dto.RefreshRequest;
import com.mjsamaha.invenstar.auth.dto.RegisterRequest;
import com.mjsamaha.invenstar.config.JwtService;
import com.mjsamaha.invenstar.user.RefreshToken;
import com.mjsamaha.invenstar.user.User;
import com.mjsamaha.invenstar.user.UserService;

@Service
public class AuthenticationService {

    private final UserService            userService;
    private final JwtService             jwtService;
    private final RefreshTokenService    refreshTokenService;
    private final AuthenticationManager  authenticationManager;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    public AuthenticationService(
            UserService           userService,
            JwtService            jwtService,
            RefreshTokenService   refreshTokenService,
            AuthenticationManager authenticationManager
    ) {
        this.userService           = userService;
        this.jwtService            = jwtService;
        this.refreshTokenService   = refreshTokenService;
        this.authenticationManager = authenticationManager;
    }

    // Register a new user, then issue tokens

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Delegate persistence + uniqueness checks to UserService
        User user = userService.registerUser(
                request.getUser_username(),
                request.getUser_email(),
                request.getUser_password()
        );

        return buildAuthResponse(user);
    }

    // Login an existing user: verify credentials, revoke old tokens, issue new tokens

    @Transactional
    public AuthResponse login(LoginRequest request) {
        // AuthenticationManager handles credential verification + throws
        // BadCredentialsException automatically if invalid
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUser_username(),
                        request.getUser_password()
                )
        );

        // Principal is the User object returned by UserDetailsServiceImpl
        User user = (User) auth.getPrincipal();

        // Revoke all existing refresh tokens before issuing new ones
        // prevents a user from accumulating active sessions
        refreshTokenService.revokeAllUserTokens(user);

        return buildAuthResponse(user);
    }

    // Refresh tokens: verify the provided refresh token, then rotate it and issue a

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        // Verify token exists in DB, is not revoked, is not expired
        RefreshToken stored = refreshTokenService.verifyRefreshToken(
                request.getRefreshToken()
        );

        User user = stored.getUser();

        // Rotate — revokes old token, issues new one
        String newRawRefreshToken = refreshTokenService.rotateRefreshToken(
                request.getRefreshToken()
        );

        String newAccessToken = jwtService.generateAccessToken(user);

        return new AuthResponse(
                newAccessToken,
                newRawRefreshToken,
                user.getUser_role().name(),
                accessTokenExpiration
        );
    }

    // Logout: revoke the provided refresh token so it can no longer be used

    @Transactional
    public MessageResponse logout(RefreshRequest request) {
        refreshTokenService.revokeRefreshToken(request.getRefreshToken());
        return new MessageResponse("Logged out successfully");
    }

    // Helper method to build the AuthResponse with new tokens and user role

    private AuthResponse buildAuthResponse(User user) {
        String accessToken  = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getUser_role().name(),
                accessTokenExpiration
        );
    }
}