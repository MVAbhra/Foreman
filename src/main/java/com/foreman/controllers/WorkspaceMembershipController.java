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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foreman.dtos.UserDisplayResponseDto;
import com.foreman.dtos.WrkMemInvDto;
import com.foreman.services.WorkspaceMembershipService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/workspaces/{wrkspcId}/members")
public class WorkspaceMembershipController {

	@Autowired
	private WorkspaceMembershipService wMService;

	
	@GetMapping
	public ResponseEntity<List<UserDisplayResponseDto>> getWorkspaceMembers(@PathVariable Long wrkspcId) {
		
		List<UserDisplayResponseDto> members = wMService.getWorkspaceMembers(wrkspcId);
		
		return ResponseEntity.status(HttpStatus.OK).body(members);
	}
	
	
	@PostMapping
	public ResponseEntity<String> inviteOneMember(
			@PathVariable Long wrkspcId, 
			@RequestBody @Valid WrkMemInvDto dto) {
		
		String message = wMService.inviteOneMember(wrkspcId, dto);
		
		return new ResponseEntity<>(message, HttpStatus.OK);
	}
	
	
	@GetMapping("/join")
	public ResponseEntity<String> addOneMember(
			@PathVariable Long wrkspcId,
			@RequestParam String email) {
		
		wMService.addOneMember(wrkspcId, email);
		
		return new ResponseEntity<>(
				"User ("+email+") added to workspace "+wrkspcId+"!", 
				HttpStatus.CREATED);
	}
	
	
	@PutMapping
	public ResponseEntity<String> updateOneMember(
			@PathVariable Long wrkspcId, 
			@RequestBody @Valid WrkMemInvDto dto) {
		
		wMService.updateOneMember(wrkspcId, dto);
		
		return ResponseEntity.status(HttpStatus.OK).body("User "+dto.getEmail()+" in workspace "+wrkspcId+" was updated!");
	}
		

	@DeleteMapping("/{memId}")
	public ResponseEntity<String> deleteOneMember(
			@PathVariable Long wrkspcId, 
			@PathVariable Long memId) {
		
		wMService.deleteOneMember(wrkspcId, memId);
		
		return ResponseEntity.status(HttpStatus.OK).body("User "+memId+" removed from workspace "+wrkspcId);
	}
}
