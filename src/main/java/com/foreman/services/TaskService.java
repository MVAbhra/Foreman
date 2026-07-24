package com.foreman.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.foreman.entities.Project;
import com.foreman.entities.Task;
import com.foreman.entities.User;
import com.foreman.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foreman.dtos.TaskCreAndUpDto;
import com.foreman.dtos.TaskDisplayResponseDto;
import com.foreman.repos.TaskRepo;

@Service
@Transactional
public class TaskService {

	@Autowired
	private TaskRepo tRepo;
	
	@Autowired
	private ProjectService pService;
	
	@Autowired
	private ProjectMembershipService pmService;

	public List<TaskDisplayResponseDto> getAllTasksInProject(Long wrkspcId, Long projId) {
		
		//performing checks before going further. 
		//These methods will either return objects or throw exceptions, halting progression.
		pService.checkProjectExistenceInWorkspace(wrkspcId, projId);
		
		
		List<TaskDisplayResponseDto> tasks = tRepo.getAllTasksInProject(projId);
		
		return tasks;
	}

	
	public void addOneTask(Long wrkspcId, Long projId, TaskCreAndUpDto dto) {
		
		//performing checks before going further. 
		//These methods will either return objects or throw exceptions, halting progression.
		Project p = pService.checkProjectExistenceInWorkspace(wrkspcId, projId);
		User u = pmService.checkUserExistenceInProject(wrkspcId, projId, dto.getUserId());
		
		Task t = new Task(dto.getTitle(), 
				dto.getDescription(), 
				p, 
				u, 
				dto.getPriority(), 
				dto.getStatus(), 
				dto.getDueDate());
		
		tRepo.save(t);
	}
	

	public TaskDisplayResponseDto getOneTaskInProject(Long wrkspcId, Long projId, Long taskId) {

		//performing checks before going further. 
		//These methods will either return objects or throw exceptions, halting progression.
		pService.checkProjectExistenceInWorkspace(wrkspcId, projId);
		
		
		//this method is in the utility section of this class.
		//this either returns a Task object or throws an exception halting further progress.
		Task t = checkTaskExistenceInProject(taskId, projId);
		
		TaskDisplayResponseDto dto = new TaskDisplayResponseDto(
				t.getId(), t.getTitle(), 
				t.getDescription(), t.getCreatedOn(), 
				t.getPriority(), t.getDueDate(), t.getStatus(), 
				projId, t.getUser().getId(), wrkspcId);
		
		return dto;
	}
	
	
	public void updateOneTask(Long wrkspcId, Long projId, Long taskId, TaskCreAndUpDto dto) {
		
		//performing checks before going further. 
		//These methods will either return objects or throw exceptions, halting progression.
		pService.checkProjectExistenceInWorkspace(wrkspcId, projId);
		User u = pmService.checkUserExistenceInProject(wrkspcId, projId, dto.getUserId());
		
		Task t = checkTaskExistenceInProject(taskId, projId);
		
		t.setTitle(dto.getTitle());
		t.setDescription(dto.getDescription());
		t.setPriority(dto.getPriority());
		t.setDueDate(dto.getDueDate());
		t.setStatus(dto.getStatus());
		t.setUser(u);
	}
	
	
	public void deleteOneTask(Long wrkspcId, Long projId, Long taskId) {
		
		pService.checkProjectExistenceInWorkspace(wrkspcId, projId);
		
		Task t = checkTaskExistenceInProject(taskId, projId);
		
		tRepo.delete(t);
	}
	
	//---------------------------------------- Utility methods -----------------------------------
	
	public Task checkTaskExistenceInProject(Long taskId, Long projId) {
		
		Task t = tRepo.findByIdAndProject_Id(taskId, projId).orElseThrow(() -> 
			new ResourceNotFoundException("Task "+taskId+" does not exist in project "+projId+"!"));
		
		return t;
	}
}
