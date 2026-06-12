package com.mjsamaha.invenstar.user;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImplementation(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User registerUser(String user_username, String user_email, String user_raw_password) {
        if (userRepository.existsByUsername(user_username)) {
            throw new IllegalArgumentException("Username already taken: " + user_username);
        }
        if (userRepository.existsByUserEmail(user_email)) {
            throw new IllegalArgumentException("Email already registered: " + user_email);
        }

        User user = new User(
                user_username,
                user_email,
                passwordEncoder.encode(user_raw_password),
                UserRole.USER
        );

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUsername(String user_username) {
        return userRepository.findByUsername(user_username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + user_username));
    }

    @Override
    @Transactional
    public User updateEmail(UUID id, String user_email) {
        if (userRepository.existsByUserEmail(user_email)) {
            throw new IllegalArgumentException("Email already in use: " + user_email);
        }

        User user = getUserById(id);
        user.setUser_email(user_email);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updatePassword(UUID id, String user_raw_password) {
        User user = getUserById(id);
        user.setUser_password(passwordEncoder.encode(user_raw_password));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deactivateUser(UUID id) {
        User user = getUserById(id);
        user.setUser_enabled(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void reactivateUser(UUID id) {
        User user = getUserById(id);
        user.setUser_enabled(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public User changeRole(UUID id, UserRole user_role) {
        User user = getUserById(id);
        user.setUser_role(user_role);
        return userRepository.save(user);
    }
}