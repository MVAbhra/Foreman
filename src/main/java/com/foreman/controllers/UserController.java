package com.foreman.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foreman.dtos.UserDisplayResponseDto;
import com.foreman.entities.User;
import com.foreman.services.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/users")
public class UserController {
	
	@Autowired
	private UserService userService;

	@GetMapping
	public ResponseEntity<List<User>> getAllUsers() {
		
		List<User> users = userService.getAllUsers();
		
		return ResponseEntity.ok(users);
	} 
	
	
	@GetMapping("/{id}")
	public ResponseEntity<UserDisplayResponseDto> getOneUser(@PathVariable Long id) {
		
		UserDisplayResponseDto userDisplayResponseDto = userService.getOneUser(id);
		
		return ResponseEntity.ok(userDisplayResponseDto);
	}
	
	
	@PutMapping("/{id}/update")
	public ResponseEntity<User> updateOneUser(@RequestBody @Valid User dto, @PathVariable Long id) {
		
		User updatedUser = userService.updateOneUser(dto, id); 
		
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedUser);
	}
	
	
	@DeleteMapping("/{id}/delete")
	public ResponseEntity<User> deleteOneUser(@PathVariable Long id) {
		
		User deletedUser = userService.deleteOneUser(id);
		
		return ResponseEntity.status(HttpStatus.OK).body(deletedUser);
	}
	
	//-------- post spring security ----------------
	
	@GetMapping("/me")
	public ResponseEntity<User> getLoggedInUser() {

	    return ResponseEntity.ok(userService.getLoggedInUser());
	}
}
