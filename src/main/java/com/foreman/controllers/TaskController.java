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

import com.foreman.dtos.TaskCreAndUpDto;
import com.foreman.dtos.TaskDisplayResponseDto;
import com.foreman.services.TaskService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/workspaces/{wrkspcId}/projects/{projId}/tasks")
public class TaskController {

	@Autowired
	private TaskService tService;
	
	
	@GetMapping
	public ResponseEntity<List<TaskDisplayResponseDto>> getAllTasksInProject(
			@PathVariable Long wrkspcId, 
			@PathVariable Long projId) {
		
		List<TaskDisplayResponseDto> tasks = tService.getAllTasksInProject(wrkspcId, projId);
		
		return new ResponseEntity<>(tasks, HttpStatus.OK);
	}
	
	
	@PostMapping
	public ResponseEntity<String> addOneTask(
			@PathVariable Long wrkspcId, 
			@PathVariable Long projId,
			@RequestBody @Valid TaskCreAndUpDto dto) {
		
		tService.addOneTask(wrkspcId, projId, dto);
		
		return new ResponseEntity<>("Task was added to user "+dto.getUserId()+" in project "+projId+"!", HttpStatus.CREATED);
	}
	
	
//	@GetMapping("/{taskId}")
//	public ResponseEntity<TaskDisplayResponseDto> getOneTaskInProject(
//			@PathVariable Long wrkspcId, 
//			@PathVariable Long projId,
//			@PathVariable Long taskId) {
//		
//		TaskDisplayResponseDto dto = tService.getOneTaskInProject(wrkspcId, projId, taskId);
//		
//		return new ResponseEntity<>(dto, HttpStatus.OK);
//	}
	
	
	@PutMapping("/{taskId}")
	public ResponseEntity<String> updateOneTask(
			@PathVariable Long wrkspcId, 
			@PathVariable Long projId,
			@PathVariable Long taskId,
			@RequestBody @Valid TaskCreAndUpDto dto) {
		
		tService.updateOneTask(wrkspcId, projId, taskId, dto);
		
		return new ResponseEntity<>("Task "+taskId+" of user "+dto.getUserId()+" in project "+projId+" was updated!",
				HttpStatus.OK);
	}
	
	
	@DeleteMapping("/{taskId}")
	public ResponseEntity<String> deleteOneTask(
			@PathVariable Long wrkspcId, 
			@PathVariable Long projId,
			@PathVariable Long taskId) {
		
		tService.deleteOneTask(wrkspcId, projId, taskId);
		
		return new ResponseEntity<>("Task "+taskId+" in project "+projId+" was deleted!", HttpStatus.OK);
	}
	
	
	@GetMapping("/search")
	public ResponseEntity<List<TaskDisplayResponseDto>> searchTasks(
			@PathVariable Long wrkspcId, 
			@PathVariable Long projId,
			@RequestParam String keyword) {
		
		List<TaskDisplayResponseDto> tasks = tService.getSearchedTasksInProject(wrkspcId, projId, keyword);
	
		return new ResponseEntity<>(tasks, HttpStatus.OK);
	}
}
