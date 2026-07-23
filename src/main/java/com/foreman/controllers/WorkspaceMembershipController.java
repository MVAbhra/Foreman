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

import com.foreman.dtos.UserDisplayResponseDto;
import com.foreman.dtos.WrkMemInvDto;
import com.foreman.services.WorkspaceMembershipService;

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
	public ResponseEntity<String> addOneMember(@PathVariable Long wrkspcId, @RequestBody WrkMemInvDto dto) {
		
		wMService.addOneMember(wrkspcId, dto);
		
		return ResponseEntity.status(HttpStatus.OK).body("User "+dto.getUserId()+" added to workspace "+wrkspcId);
	}
	
	
	@PutMapping
	public ResponseEntity<String> updateOneMember(@PathVariable Long wrkspcId, @RequestBody WrkMemInvDto dto) {
		
		wMService.updateOneMember(wrkspcId, dto);
		
		return ResponseEntity.status(HttpStatus.OK).body("User "+dto.getUserId()+" in workspace "+wrkspcId+" was updated!");
	}
	
	
	@GetMapping("/{memId}")
	public ResponseEntity<UserDisplayResponseDto> getOneWorkspaceMember(
			@PathVariable Long wrkspcId, 
			@PathVariable Long memId) {
		
		UserDisplayResponseDto member = wMService.getOneWorkspaceMember(wrkspcId, memId);
		
		return ResponseEntity.status(HttpStatus.OK).body(member);
	}
		

	@DeleteMapping("/{memId}")
	public ResponseEntity<String> deleteOneMember(
			@PathVariable Long wrkspcId, 
			@PathVariable Long memId) {
		
		wMService.deleteOneMember(wrkspcId, memId);
		
		return ResponseEntity.status(HttpStatus.OK).body("User "+memId+" removed from workspace "+wrkspcId);
	}
}
