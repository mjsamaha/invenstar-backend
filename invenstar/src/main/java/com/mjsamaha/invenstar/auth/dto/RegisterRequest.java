package com.mjsamaha.invenstar.auth.dto;

public class RegisterRequest {

	private String user_username;
	private String user_email;
	private String user_password;

	public RegisterRequest() {
	}

	public RegisterRequest(String user_username, String user_email, String user_password) {
		this.user_username = user_username;
		this.user_email = user_email;
		this.user_password = user_password;
	}

	public String getUser_username() {
		return user_username;
	}

	public void setUser_username(String user_username) {
		this.user_username = user_username;
	}

	public String getUser_email() {
		return user_email;
	}

	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}

	public String getUser_password() {
		return user_password;
	}

	public void setUser_password(String user_password) {
		this.user_password = user_password;
	}
}