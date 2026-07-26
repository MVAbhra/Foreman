package com.foreman.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foreman.entities.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

	Optional<User> findById(Long id);

	boolean existsByEmail(String email);

	Optional<User> findByEmail(String username);
}
