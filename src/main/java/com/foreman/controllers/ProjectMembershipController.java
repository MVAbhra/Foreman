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

import com.foreman.dtos.ProjMemInvDto;
import com.foreman.dtos.UserDisplayResponseDto;
import com.foreman.services.ProjectMembershipService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/workspaces/{wrkspcId}/projects/{projId}/members")
public class ProjectMembershipController {

	@Autowired
	private ProjectMembershipService pMService;
	
	
	@GetMapping
	public ResponseEntity<List<UserDisplayResponseDto>> getAllProjectMembers(
				@PathVariable Long wrkspcId,
				@PathVariable Long projId
	){
		
		List<UserDisplayResponseDto> projMembers = pMService.getAllProjectMembers(wrkspcId, projId);
		
		return ResponseEntity.status(HttpStatus.OK).body(projMembers);
	}
	
	
	@PostMapping
	public ResponseEntity<String> inviteOneProjectMember(
			@PathVariable Long wrkspcId, 
			@PathVariable Long projId,
			@RequestBody @Valid ProjMemInvDto projMemInvDto){
		
		String message = pMService.inviteOneProjectMember(wrkspcId, projId, projMemInvDto);
		
		return new ResponseEntity<>(message, HttpStatus.OK);
	}
	
	
	@GetMapping("/join")
	public ResponseEntity<String> addOneProjectMember(
			@PathVariable Long wrkspcId,
			@PathVariable Long projId,
			@RequestParam String email) {
		
		pMService.addOneProjectMember(wrkspcId, projId, email);
		
		return new ResponseEntity<>("User ("+email+") joined project "+projId+" in workspace "+wrkspcId+"!", HttpStatus.CREATED);
	}
	
	
	@PutMapping
	public ResponseEntity<String> updateOneProjectMember(
			@PathVariable Long wrkspcId, 
			@PathVariable Long projId,
			@RequestBody @Valid ProjMemInvDto projMemInvDto){
		
		pMService.updateOneProjectMember(wrkspcId, projId, projMemInvDto);
		
		return ResponseEntity.status(HttpStatus.CREATED).body("User "+projMemInvDto.getEmail()+" inside project "
				+projId+" was updated!");
	}
	
	
	@DeleteMapping("/{memId}")
	public ResponseEntity<String> deleteOneProjectMember(
			@PathVariable Long wrkspcId, 
			@PathVariable Long projId,
			@PathVariable Long memId){
		
		pMService.deleteOneProjectMember(wrkspcId, projId, memId);
		
		return ResponseEntity.status(HttpStatus.CREATED).body("User "+memId+" removed from project "+projId+"!");
	}
}
