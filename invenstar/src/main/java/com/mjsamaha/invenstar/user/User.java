package com.mjsamaha.invenstar.user;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "user_id", updatable = false, nullable = false)
	private UUID user_id;

	@Column(name = "user_username", unique = true, nullable = false, length = 50)
	private String user_username;

	@Column(name = "user_email", unique = true, nullable = false, length = 100)
	private String user_email;

	@Column(name = "user_password", nullable = false)
	private String user_password;

	@Enumerated(EnumType.STRING)
	@Column(name = "user_role", nullable = false)
	private UserRole user_role;

	@Column(name = "user_enabled", nullable = false)
	private boolean user_enabled;

	@Column(name = "user_created_at", updatable = false)
	private LocalDateTime user_created_at;

	@Column(name = "user_updated_at", nullable = false)
	private LocalDateTime user_updated_at;

	// Lifecycle Hooks - Let the DB own the timestamps, not app logic

	@PrePersist
	protected void onCreate() {
		user_created_at = LocalDateTime.now();
		user_updated_at = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		user_updated_at = LocalDateTime.now();
	}

	// UserDetails - Spring Security reads these on every request
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// "ROLE_" prefix is required by Spring Security's hasRole() checks
		return List.of(new SimpleGrantedAuthority("ROLE_" + user_role.name()));
	}

	@Override
	public String getPassword() {
		return user_password;
	}

	// Spring uses this as the unique identity key during authentication
	@Override
	public String getUsername() {
		return user_username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true; // extend later if you add account expiry logic
	}

	@Override
	public boolean isAccountNonLocked() {
		return true; // extend later for login-attempt lockout
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true; // extend later for password rotation policies
	}

	@Override
	public boolean isEnabled() {
		return user_enabled; // ties directly to your existing active flag
	}

	public User() {

	}

	public User(String user_username, String user_email, String user_password, UserRole user_role) {
		this.user_username = user_username;
		this.user_email = user_email;
		this.user_password = user_password;
		this.user_role = user_role;
	}

	public UUID getUser_id() {
		return user_id;
	}

	public void setUser_id(UUID user_id) {
		this.user_id = user_id;
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

	public UserRole getUser_role() {
		return user_role;
	}

	public void setUser_role(UserRole user_role) {
		this.user_role = user_role;
	}

	public boolean isUser_enabled() {
		return user_enabled;
	}

	public void setUser_enabled(boolean user_enabled) {
		this.user_enabled = user_enabled;
	}

	public LocalDateTime getUser_created_at() {
		return user_created_at;
	}

	public void setUser_created_at(LocalDateTime user_created_at) {
		this.user_created_at = user_created_at;
	}

	public LocalDateTime getUser_updated_at() {
		return user_updated_at;
	}

	public void setUser_updated_at(LocalDateTime user_updated_at) {
		this.user_updated_at = user_updated_at;
	}

}
