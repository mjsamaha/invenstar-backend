package com.mjsamaha.invenstar.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String user_username) throws UsernameNotFoundException {
        // Spring Security calls this during authentication AND on every
        // validated JWT request — keep it a simple, single-purpose lookup
        return userRepository.findByUsername(user_username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No user found with username: " + user_username
                ));
    }
}