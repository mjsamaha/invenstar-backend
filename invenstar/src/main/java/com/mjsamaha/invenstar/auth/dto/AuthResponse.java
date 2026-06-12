package com.mjsamaha.invenstar.auth.dto;

public class AuthResponse {

	private String accessToken;
	private String refreshToken;
	private String role;
	private long expiresIn; // milliseconds

	public AuthResponse() {
	}

	public AuthResponse(String accessToken, String refreshToken, String role, long expiresIn) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.role = role;
		this.expiresIn = expiresIn;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}
}