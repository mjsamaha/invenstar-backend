package com.mjsamaha.invenstar.user;

import java.util.Set;

public enum UserRole {

    USER(Set.of(
            Permission.INVENTORY_READ
    )),

    ADMIN(Set.of(
            Permission.INVENTORY_READ,
            Permission.INVENTORY_CREATE,
            Permission.INVENTORY_UPDATE,
            Permission.INVENTORY_DELETE,
            Permission.USER_READ,
            Permission.USER_CREATE,
            Permission.USER_UPDATE,
            Permission.USER_DELETE
    ));

    private final Set<Permission> permissions;

    UserRole(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }
}