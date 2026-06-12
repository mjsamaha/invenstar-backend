package com.mjsamaha.invenstar.user;

public enum Permission {
	
	// Inventory Permissions
	INVENTORY_READ("inventory:read"),
	INVENTORY_CREATE("inventory:create"),
	INVENTORY_UPDATE("inventory:update"),
	INVENTORY_DELETE("inventory:delete"),
	
	// User Management Permissions (admin only)
	USER_READ("user:read"),
	USER_CREATE("user:create"),
	USER_UPDATE("user:update"),
	USER_DELETE("user:delete");
	
	private final String permission;
	
	Permission(String permission) {
		this.permission = permission;
	}
	
	public String getPermission() {
		return permission;
	}
	
}
