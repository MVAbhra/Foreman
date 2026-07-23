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

import com.foreman.dtos.ProjectCreationAndUpdationDto;
import com.foreman.dtos.ProjectDisplayDto;
import com.foreman.entities.Project;
import com.foreman.services.ProjectService;


@RestController
@RequestMapping("/api/workspaces/{wrkspcId}/projects")
public class ProjectController {
	
	@Autowired
	private ProjectService projService;	
	
	
	@GetMapping
	public ResponseEntity<List<Project>> getAllProjects(@PathVariable Long wrkspcId) {
		
		List<Project> projects = projService.getAllProjects(wrkspcId);
		
		return ResponseEntity.status(HttpStatus.OK).body(projects);
	}

	
	@GetMapping("/{projId}")
	public ResponseEntity<ProjectDisplayDto> getOneProject(@PathVariable Long wrkspcId, @PathVariable Long projId) {
		
		ProjectDisplayDto displayDto = projService.getOneProject(wrkspcId, projId);
		
		return ResponseEntity.status(HttpStatus.OK).body(displayDto);
	}
	
	
	@PostMapping
	public ResponseEntity<String> createOneProject(@PathVariable Long wrkspcId, 
			@RequestBody ProjectCreationAndUpdationDto dto) {
		
		projService.createOneProject(wrkspcId, dto);
		
		return ResponseEntity.status(HttpStatus.CREATED).body("Project created!");
	}
	
	
	@PutMapping("/{projId}/update")
	public ResponseEntity<ProjectDisplayDto> updateOneProject(@PathVariable Long wrkspcId, @PathVariable Long projId,
			@RequestBody ProjectCreationAndUpdationDto dto) {
		
		ProjectDisplayDto updatedProjectDisplayDto = projService.updateOneProject(wrkspcId, projId, dto);
		
		return ResponseEntity.status(HttpStatus.OK).body(updatedProjectDisplayDto);
	}
	
	
	@DeleteMapping("/{projId}/delete")
	public ResponseEntity<String> deleteOneProject(@PathVariable Long wrkspcId, @PathVariable Long projId) {
		
		projService.deleteOneProject(wrkspcId, projId);
		
		return ResponseEntity.status(HttpStatus.OK).body("Project "+projId+" deleted!");
	}
}
