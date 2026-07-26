package com.foreman.services;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.core.context.SecurityContextHolder;

import com.foreman.dtos.UserDisplayResponseDto;
import com.foreman.entities.User;
import com.foreman.exception.ResourceNotFoundException;
import com.foreman.repos.UserRepo;
import com.foreman.repos.WorkspaceMembershipRepo;
import com.foreman.security.CustomUserDetails;

@Service
@Transactional
@Validated
public class UserService {
	
	private final UserRepo userRepo;
	private final WorkspaceMembershipRepo wmRepo;
	
	UserService(UserRepo userRepo, 
			WorkspaceMembershipRepo wmRepo) {
		
		this.userRepo = userRepo;
		this.wmRepo = wmRepo;
	}
	

	public List<User> getAllUsers() {
		
		List<User> users = userRepo.findAll();
		
		return users;
	}

	
	public UserDisplayResponseDto getOneUser(Long id) {
		
		User result = userRepo.findById(id).orElseThrow(() -> 
							new ResourceNotFoundException("No such user exists with that id"));
		
		UserDisplayResponseDto userDisplayResponseDto = new UserDisplayResponseDto
				
				(
					result.getId(),
					result.getFirstName(),
					result.getLastName(),
					result.getEmail(),
					null,
					null,
					null,
					null
				);
		
		return userDisplayResponseDto;
	}


	public User updateOneUser(User dto, Long id) {

		User userToBeUpdated = userRepo.findById(id).orElseThrow(() -> 
								new ResourceNotFoundException("No such user exists with that id"));
		
		userToBeUpdated.setFirstName(dto.getFirstName());
		userToBeUpdated.setLastName(dto.getLastName());
		userToBeUpdated.setEmail(dto.getEmail());
		userToBeUpdated.setPassword(dto.getPassword());
		
		userRepo.save(userToBeUpdated);
		
		return userToBeUpdated;
	}


	public User deleteOneUser(Long id) {
		
		User u = userRepo.findById(id).orElseThrow(() -> 
							new ResourceNotFoundException("No such user exists with that id"));
		
		
		wmRepo.deleteByUser_Id(u.getId());
		
		userRepo.delete(u);
		
		return u;
	}
	
	
	public User getLoggedInUser() {

		Authentication authentication =
	            SecurityContextHolder.getContext().getAuthentication();

	    CustomUserDetails userDetails =
	            (CustomUserDetails) authentication.getPrincipal();

	    return userDetails.getUser();
	}
}
