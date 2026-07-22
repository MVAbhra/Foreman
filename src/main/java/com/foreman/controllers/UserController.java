package com.foreman.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foreman.dtos.UserResponseDto;
import com.foreman.entities.User;
import com.foreman.services.UserService;


@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserService userService;

	@GetMapping("/all")
	public ResponseEntity<List<User>> getAllUsers() {
		
		List<User> users = userService.getAllUsers();
		
		return ResponseEntity.ok(users);
	} 
	
	
	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDto> getOneUser(@PathVariable Long id) {
		
		UserResponseDto userResponseDto = userService.getOneUser(id);
		
		return ResponseEntity.ok(userResponseDto);
	}
	
	
	@PostMapping("/register")
	public ResponseEntity<String> addOneUser(@RequestBody User user) {
		
		userService.addOneUser(user);
		
		return ResponseEntity.status(HttpStatus.ACCEPTED).body("User created");
	}
	
	
	@PutMapping("/{id}/update")
	public ResponseEntity<User> updateOneUser(@RequestBody User dto, @PathVariable Long id) {
		
		User updatedUser = userService.updateOneUser(dto, id); 
		
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedUser);
	}
	
	
	@DeleteMapping("/{id}/delete")
	public ResponseEntity<User> deleteOneUser(@PathVariable Long id) {
		
		User deletedUser = userService.deleteOneUser(id);
		
		return ResponseEntity.status(HttpStatus.OK).body(deletedUser);
	}
}
