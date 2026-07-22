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

import com.foreman.dtos.WorkspaceRequestDto;
import com.foreman.dtos.WrkMemInvDto;
import com.foreman.entities.Workspace;
import com.foreman.services.WorkspaceService;

@RestController
@RequestMapping("/api/workspace")
public class WorkspaceController {

	@Autowired
	private WorkspaceService workspaceService;

	
	@GetMapping("/all")
	public ResponseEntity<List<Workspace>> getAllWorkspaces() {
		
		List<Workspace> workspaces = workspaceService.getAllWorkspaces();
		
		return ResponseEntity.status(HttpStatus.OK).body(workspaces);
	}
	
	
	@GetMapping("/{id}")
	public ResponseEntity<Workspace> getOneWorkspace(@PathVariable Long id) {
		
		Workspace workspace = workspaceService.getOneWorkspace(id);
		
		return ResponseEntity.status(HttpStatus.OK).body(workspace);
	}
	
	
	@PostMapping("/create")
	public ResponseEntity<String> addOneWorkspace(@RequestBody WorkspaceRequestDto dto) {
		
		workspaceService.addOneWorkspace(dto);
		
		return ResponseEntity.status(HttpStatus.ACCEPTED).body("Workspace created");
	}
	
	
	@PutMapping("/{id}/update")
	public ResponseEntity<Workspace> updateOneWorkspace(@PathVariable Long id, @RequestBody WorkspaceRequestDto dto) {
		
		Workspace w = workspaceService.updateOneWorkspace(id, dto);
		
		return ResponseEntity.status(HttpStatus.OK).body(w);
	}
	
	
	@DeleteMapping("/{id}/delete")
	public ResponseEntity<String> deleteOneWorkspace(@PathVariable Long id) {
		
		workspaceService.deleteOneWorkspace(id);
		
		return ResponseEntity.status(HttpStatus.OK).body("Workspace deleted!");
	}
	
	
	@PostMapping("/{id}/member/add")
	public ResponseEntity<String> addOneMember(@PathVariable Long id, @RequestBody WrkMemInvDto dto) {
		
		workspaceService.addOneMember(id, dto);
		
		return ResponseEntity.status(HttpStatus.OK).body("Member "+dto.getUserId()+" added to workspace "+id);
	}
	
	
//	@DeleteMapping("/{id}/member/delete/{memid}")
//	public ResponseEntity<String> deleteOneMember(@PathVariable Long id, @PathVariable Long memid) {
//		
//		return ResponseEntity.status(HttpStatus.OK).body("Member "+memid+" removed from workspace "+id);
//	}
}
