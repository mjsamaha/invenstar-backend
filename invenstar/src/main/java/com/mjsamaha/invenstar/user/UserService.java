package com.mjsamaha.invenstar.user;

import java.util.UUID;

public interface UserService {

    User registerUser(String user_username, String user_email, String user_raw_password);

    User getUserById(UUID id);

    User getUserByUsername(String user_username);

    User updateEmail(UUID id, String user_email);

    User updatePassword(UUID id, String user_raw_password);

    void deactivateUser(UUID id);

    void reactivateUser(UUID id);

    User changeRole(UUID id, UserRole user_role);
}