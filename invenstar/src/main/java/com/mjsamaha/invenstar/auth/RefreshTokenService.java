package com.mjsamaha.invenstar.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mjsamaha.invenstar.user.RefreshToken;
import com.mjsamaha.invenstar.user.RefreshTokenRepository;
import com.mjsamaha.invenstar.user.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;

	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;

	public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
		this.refreshTokenRepository = refreshTokenRepository;
	}

	// Create a new refresh token for a user: generate a random token, hash it, and
	// store the hash with metadata

	@Transactional
	public String createRefreshToken(User user) {
		// Generate a raw random token to hand to the client
		String rawToken = UUID.randomUUID().toString();

		// Only the hash is persisted — raw token never touches the DB
		RefreshToken refreshToken = new RefreshToken(hash(rawToken), user,
				LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000));

		refreshTokenRepository.save(refreshToken);
		return rawToken; // return raw so it can be sent to the client
	}

	// Verify a refresh token: check if it exists, is valid, and not revoked/expired

	@Transactional(readOnly = true)
	public RefreshToken verifyRefreshToken(String rawToken) {
		RefreshToken stored = refreshTokenRepository.findByToken(hash(rawToken))
				.orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

		if (!stored.isValid()) {
			throw new IllegalArgumentException(
					stored.isRevoked() ? "Refresh token has been revoked" : "Refresh token has expired");
		}

		return stored;
	}

	// Rotate a refresh token: revoke the old one and issue a new one for the same
	// user

	@Transactional
	public String rotateRefreshToken(String rawOldToken) {
		RefreshToken old = verifyRefreshToken(rawOldToken);

		// Revoke the old one
		old.setRevoked(true);
		refreshTokenRepository.save(old);

		// Issue a fresh one for the same user
		return createRefreshToken(old.getUser());
	}

	// Revoke a specific token (e.g. on logout) or all tokens for a user (e.g. on
	// password change)

	@Transactional
	public void revokeRefreshToken(String rawToken) {
		RefreshToken stored = refreshTokenRepository.findByToken(hash(rawToken))
				.orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

		stored.setRevoked(true);
		refreshTokenRepository.save(stored);
	}

	@Transactional
	public void revokeAllUserTokens(User user) {
		refreshTokenRepository.deleteByUser(user);
	}

	// Hashing the token before storing it adds a layer of security — even if the DB
	// is compromised, raw tokens can't be used

	private String hash(String raw) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashed = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(hashed);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 not available", e);
		}
	}
}