package com.mjsamaha.invenstar.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.mjsamaha.invenstar.user.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.access-token-expiration}")
	private long accessTokenExpiration;

	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;

	// Key

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(secret.getBytes());
	}

	// Token Generation

	public String generateAccessToken(User user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", user.getUser_role().name()); // embed role in token
		claims.put("email", user.getUser_email());

		return buildToken(claims, user.getUsername(), accessTokenExpiration);
	}

	// Refresh token is just a random UUID — the real data lives in DB
	public String generateRefreshToken() {
		return UUID.randomUUID().toString();
	}

	private String buildToken(Map<String, Object> claims, String subject, long expiration) {
		long now = System.currentTimeMillis();

		return Jwts.builder().claims(claims).subject(subject).issuedAt(new Date(now))
				.expiration(new Date(now + expiration)).signWith(getSigningKey()).compact();
	}

	// Token Extraction

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public String extractRole(String token) {
		return extractClaim(token, claims -> claims.get("role", String.class));
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
	}

	// Token Validation

	public boolean isTokenValid(String token, UserDetails userDetails) {
		String username = extractUsername(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	public boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
}