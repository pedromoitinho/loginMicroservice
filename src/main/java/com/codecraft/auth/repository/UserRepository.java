package com.codecraft.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codecraft.auth.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);

	// Delete user by username
	void deleteByUsername(String username);

	// Check if user exists by username
	boolean existsByUsername(String username);
}
