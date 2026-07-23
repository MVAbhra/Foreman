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

import com.foreman.dtos.ProjMemInvDto;
import com.foreman.dtos.UserDisplayResponseDto;
import com.foreman.services.ProjectMembershipService;

@RestController
@RequestMapping("/api/workspaces/{wrkspcId}/projects/{projId}/members")
public class ProjectMembershipController {

	@Autowired
	private ProjectMembershipService projMemService;
	
	
	@GetMapping
	public ResponseEntity<List<UserDisplayResponseDto>> getAllProjectMembers(
				@PathVariable Long wrkspcId,
				@PathVariable Long projId
	){
		
		List<UserDisplayResponseDto> projMembers = projMemService.getAllProjectMembers(wrkspcId, projId);
		
		return ResponseEntity.status(HttpStatus.OK).body(projMembers);
	}
	
	
	@GetMapping("/{memId}")
	public ResponseEntity<UserDisplayResponseDto> getOneProjectMember(
				@PathVariable Long wrkspcId,
				@PathVariable Long projId,
				@PathVariable Long memId
	){
		
		UserDisplayResponseDto projMember = projMemService.getOneProjectMember(wrkspcId, projId, memId);
		
		return ResponseEntity.status(HttpStatus.OK).body(projMember);
	}
	
	
	@PostMapping
	public ResponseEntity<String> addOneProjectMember(
			@PathVariable Long wrkspcId, 
			@PathVariable Long projId,
			@RequestBody ProjMemInvDto projMemInvDto){
		
		projMemService.addOneProjectMember(wrkspcId, projId, projMemInvDto);
		
		return ResponseEntity.status(HttpStatus.CREATED).body("User "+projMemInvDto.getUserId()+" added to project "
				+projId+" with role "+projMemInvDto.getProjectRole().name()+"!");
	}
	
	
	@PutMapping
	public ResponseEntity<String> updateOneProjectMember(
			@PathVariable Long wrkspcId, 
			@PathVariable Long projId,
			@RequestBody ProjMemInvDto projMemInvDto){
		
		projMemService.updateOneProjectMember(wrkspcId, projId, projMemInvDto);
		
		return ResponseEntity.status(HttpStatus.CREATED).body("User "+projMemInvDto.getUserId()+" inside project "
				+projId+" was updated!");
	}
	
	
	@DeleteMapping("/{memId}")
	public ResponseEntity<String> deleteOneProjectMember(
			@PathVariable Long wrkspcId, 
			@PathVariable Long projId,
			@PathVariable Long memId){
		
		projMemService.deleteOneProjectMember(wrkspcId, projId, memId);
		
		return ResponseEntity.status(HttpStatus.CREATED).body("User "+memId+" removed from project "+projId+"!");
	}
}
