package com.foreman.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foreman.dtos.UserDisplayResponseDto;
import com.foreman.entities.User;
import com.foreman.exception.DuplicateResourceException;
import com.foreman.exception.ResourceNotFoundException;
import com.foreman.repos.UserRepo;
import com.foreman.repos.WorkspaceMembershipRepo;

@Service
@Transactional
public class UserService {
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private WorkspaceMembershipRepo wmRepo;

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
	
	
	public void addOneUser(User user) {
		
		if(userRepo.existsByEmail(user.getEmail())){
			
			throw new DuplicateResourceException("User with email "+user.getEmail()+" already exists!");
		}
		
		user.setCreatedOn(LocalDate.now());
		userRepo.save(user);
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
}
