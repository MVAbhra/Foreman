package com.foreman.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foreman.dtos.UserResponseDto;
import com.foreman.entities.User;
import com.foreman.repos.UserRepo;
import com.foreman.repos.WorkspaceMembershipRepo;
import com.foreman.repos.WorkspaceRepo;

@Service
@Transactional
public class UserService {
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private WorkspaceRepo wRepo;
	
	@Autowired
	private WorkspaceMembershipRepo wmRepo;

	public List<User> getAllUsers() {
		
		List<User> users = userRepo.findAll();
		
		return users;
	}

	
	public UserResponseDto getOneUser(Long id) {
		
		User result = userRepo.findById(id).orElseThrow(() -> 
							new RuntimeException("No such user exists with that id"));
		
		UserResponseDto userResponseDto = new UserResponseDto
				
				(
					result.getId(),
					result.getFirstName(),
					result.getLastName(),
					result.getEmail()
				);
		
		return userResponseDto;
	}
	
	
	public void addOneUser(User user) {
		
		user.setCreatedOn(LocalDate.now());
		userRepo.save(user);
	}


	public User updateOneUser(User dto, Long id) {

		User userToBeUpdated = userRepo.findById(id).orElseThrow(() -> 
								new RuntimeException("No such user exists with that id"));
		
		userToBeUpdated.setFirstName(dto.getFirstName());
		userToBeUpdated.setLastName(dto.getLastName());
		userToBeUpdated.setEmail(dto.getEmail());
		userToBeUpdated.setPassword(dto.getPassword());
		
		userRepo.save(userToBeUpdated);
		
		return userToBeUpdated;
	}


	public User deleteOneUser(Long id) {
		
		User u = userRepo.findById(id).orElseThrow(() -> 
							new RuntimeException("No such user exists with that id"));
		
		
		wmRepo.deleteByUser_Id(u.getId());
		
		wRepo.deleteByOwner_Id(u.getId());
		
		userRepo.delete(u);
		
		return u;
	}
}
