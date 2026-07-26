package com.foreman.controllers;

import java.util.List;

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

import com.foreman.dtos.WorkspaceCreationAndUpdationDto;
import com.foreman.entities.Workspace;
import com.foreman.services.WorkspaceService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

	private final WorkspaceService workspaceService;

	
	@GetMapping
	public ResponseEntity<List<Workspace>> getAllWorkspaces() {
		
		List<Workspace> workspaces = workspaceService.getAllWorkspaces();
		
		return ResponseEntity.status(HttpStatus.OK).body(workspaces);
	}
	
	
	@GetMapping("/{wrkspcId}")
	public ResponseEntity<Workspace> getOneWorkspace(@PathVariable Long wrkspcId) {
		
		Workspace workspace = workspaceService.getOneWorkspace(wrkspcId);
		
		return ResponseEntity.status(HttpStatus.OK).body(workspace);
	}
	
	
	@PostMapping
	public ResponseEntity<String> addOneWorkspace(@RequestBody @Valid WorkspaceCreationAndUpdationDto dto) {
		
		workspaceService.addOneWorkspace(dto);
		
		return ResponseEntity.status(HttpStatus.ACCEPTED).body("Workspace created");
	}
	
	
	@PutMapping("/{wrkspcId}/update")
	public ResponseEntity<String> updateOneWorkspace(@PathVariable Long wrkspcId, 
			@RequestBody @Valid WorkspaceCreationAndUpdationDto dto) {
		
		workspaceService.updateOneWorkspace(wrkspcId, dto);
		
		return ResponseEntity.status(HttpStatus.OK).body("Workspace "+wrkspcId+" was updated!");
	}
	
	
	@DeleteMapping("/{wrkspcId}/delete")
	public ResponseEntity<String> deleteOneWorkspace(@PathVariable Long wrkspcId) {
		
		workspaceService.deleteOneWorkspace(wrkspcId);
		
		return ResponseEntity.status(HttpStatus.OK).body("Workspace deleted!");
	}
}
