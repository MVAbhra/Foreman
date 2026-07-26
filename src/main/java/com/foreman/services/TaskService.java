package com.foreman.services;

import java.util.List;

import com.foreman.entities.Comment;
import com.foreman.entities.Project;
import com.foreman.entities.ProjectMembership;
import com.foreman.entities.Task;
import com.foreman.entities.User;
import com.foreman.entities.WorkspaceMembership;
import com.foreman.enums.ProjectRole;
import com.foreman.enums.WorkspaceRole;
import com.foreman.exception.InvalidActionException;
import com.foreman.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.foreman.dtos.TaskCreAndUpDto;
import com.foreman.dtos.TaskDisplayResponseDto;
import com.foreman.repos.CommentRepo;
import com.foreman.repos.ProjectMembershipRepo;
import com.foreman.repos.ProjectRepo;
import com.foreman.repos.TaskRepo;
import com.foreman.repos.UserRepo;
import com.foreman.repos.WorkspaceMembershipRepo;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@Validated
@RequiredArgsConstructor
public class TaskService {

	private final TaskRepo tRepo;
	private final ProjectMembershipRepo pMRepo;
	private final WorkspaceMembershipRepo wMRepo;
	private final UserService uService;
	private final ProjectRepo pRepo;
	private final UserRepo uRepo;
	private final CommentRepo cRepo;
	

	public List<TaskDisplayResponseDto> getAllTasksInProject(Long wrkspcId, Long projId) {
		
		//method defined in utility section below
		checkIfProjectMemberOrOwner(wrkspcId, projId);
		
		List<TaskDisplayResponseDto> tasks = tRepo.getAllTasksInProject(projId);
		
		return tasks;
	}

	
	public void addOneTask(Long wrkspcId, Long projId, TaskCreAndUpDto dto) {
		
		//method defined in utility section below
		checkIfProjectManagerOrOwner(wrkspcId, projId);
		
		//get the project
		Project p = pRepo.findById(projId).orElseThrow();
		
		//get the user or assignee
		User u = uRepo.findById(dto.getUserId()).orElseThrow(() -> 
				new ResourceNotFoundException("User "+dto.getUserId()+" does not exist!"));
		
		//check if the user belongs to the project
		if(pMRepo.existsByProject_IdAndUser_Id(projId, u.getId()) == false) {
			
			throw new InvalidActionException("User "+u.getId()+" does not belong to project "+projId+"!");
		}
		
		//create the task
		Task t = new Task(dto.getTitle(), 
				dto.getDescription(), p, u, 
				dto.getPriority(), dto.getStatus(), 
				dto.getDueDate());
		
		//save the task
		tRepo.save(t);		
	}
	
	
	public void updateOneTask(Long wrkspcId, Long projId, Long taskId, TaskCreAndUpDto dto) {
		
		//method defined in utility section below
		checkIfProjectMemberOrOwner(wrkspcId, projId);
		
		//find the task
		Task t = tRepo.findByIdAndProject_Id(taskId, projId).orElseThrow(() -> 
				new ResourceNotFoundException("Task "+taskId+" does not  in project "+projId+"!"));
		
		//find current user
		User currentUser = uService.getLoggedInUser();
		
		//find current user's membership in workspace
		WorkspaceMembership wm = wMRepo.findByWorkspace_IdAndUser_Id(wrkspcId, currentUser.getId()).orElseThrow();
		
		//find current user's membership in project
		ProjectMembership pm = pMRepo.findByProject_IdAndUser_Id(projId, currentUser.getId()).orElseThrow();
		
		//check if current user is workspace's OWNER or project's PROJECT_MANAGER
		//if yes, then they can modify the following fields in a Task
		if(wm.getWorkspaceRole() == WorkspaceRole.OWNER || pm.getProjectRole() == ProjectRole.PROJECT_MANAGER) {
			
			t.setTitle(dto.getTitle());
			t.setDescription(dto.getDescription());
			t.setPriority(dto.getPriority());
			t.setDueDate(dto.getDueDate());
			t.setStatus(dto.getStatus());
		}
		
		//if current user is a DEVELOPER
		else {
			
			t.setStatus(dto.getStatus());
		}
	}
	
	
	public void deleteOneTask(Long wrkspcId, Long projId, Long taskId) {
		
		//method defined in utility section below
		checkIfProjectManagerOrOwner(wrkspcId, projId);
		
		//find the task
		Task t = tRepo.findByIdAndProject_Id(taskId, projId).orElseThrow(() -> 
				new ResourceNotFoundException("Task "+taskId+" does not exist in project "+projId+"!"));
		
		//find all the comments related to the task
		List<Comment> comments = cRepo.findByTask_Id(taskId);
		
		//delete all related comments before deleting the task
		cRepo.deleteAll(comments);
		
		//delete the task
		tRepo.delete(t);		
	}
	
	//-----------------------------------------Utility methods--------------------------------------------------
	
	
	public void checkIfProjectMemberOrOwner(Long wrkspcId, Long projId) {
			
			//get logged in user
			User currentUser = uService.getLoggedInUser();
			
			//get the user's membership in the workspace
			WorkspaceMembership wm = wMRepo.findByWorkspace_IdAndUser_Id(wrkspcId, currentUser.getId())
					.orElseThrow(() -> new ResourceNotFoundException("You ("+currentUser.getEmail()+") do not belong to workspace "+wrkspcId+"!"));
			
			//if user is workspace's OWNER then return/authorize
			if(wm.getWorkspaceRole() == WorkspaceRole.OWNER) return;
			
			//get the user's membership in the project
			ProjectMembership pm = pMRepo.findByProject_IdAndUser_Id(projId, currentUser.getId()).
					orElseThrow(() -> new ResourceNotFoundException("You ("+currentUser.getEmail()+") do not belong to project "+projId+"!"));
			
			//if user is project's member then return/authorize
			if(pm != null) return;
				
			//if user is neither workspace's OWNER or project's member
			throw new InvalidActionException("You ("+currentUser.getEmail()+") are not authorized to view project "+projId+"!");
	}
	
	
	public void checkIfProjectManagerOrOwner(Long wrkspcId, Long projId) {
		
		//get logged in user
		User currentUser = uService.getLoggedInUser();
		
		//get the user's membership in the workspace
		WorkspaceMembership wm = wMRepo.findByWorkspace_IdAndUser_Id(wrkspcId, currentUser.getId())
				.orElseThrow(() -> new ResourceNotFoundException("You ("+currentUser.getEmail()+") do not belong to workspace "+wrkspcId+"!"));
		
		//if user is workspace's OWNER then return/authorize
		if(wm.getWorkspaceRole() == WorkspaceRole.OWNER) return;
		
		//get the user's membership in the project
		ProjectMembership pm = pMRepo.findByProject_IdAndUser_Id(projId, currentUser.getId()).
				orElseThrow(() -> new ResourceNotFoundException("You ("+currentUser.getEmail()+") do not belong to project "+projId+"!"));
		
		//if user is project's PROJECT_MANAGER then return/authorize
		if(pm.getProjectRole() == ProjectRole.PROJECT_MANAGER) return;
		
		//if user is neither workspace's OWNER or project's PROJECT_MANAGER
		throw new InvalidActionException("You ("+currentUser.getEmail()+") are not authorized to update or delete project "+projId+"!");
	}
}
