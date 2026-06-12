package com.mjsamaha.invenstar.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
	
	Optional<User> findByUsername(String user_username);
	
	Optional<User> findByUserEmail(String user_email);
	
	boolean existsByUsername(String user_username);
	
	boolean existsByUserEmail(String user_email);

}
