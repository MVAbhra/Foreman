package com.foreman.services;

import java.util.List;

import com.foreman.entities.Comment;
import com.foreman.entities.Project;
import com.foreman.entities.ProjectMembership;
import com.foreman.entities.Task;
import com.foreman.entities.User;
import com.foreman.entities.WorkspaceMembership;
import com.foreman.enums.ProjectRole;
import com.foreman.enums.TaskStatus;
import com.foreman.enums.WorkspaceRole;
import com.foreman.exception.InvalidActionException;
import com.foreman.exception.ResourceNotFoundException;
import com.foreman.microservices.notification.NotificationClient;
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
	private final NotificationClient notifClient;
	private final AutherizationService azService;
	

	public List<TaskDisplayResponseDto> getAllTasksInProject(Long wrkspcId, Long projId) {
		
		//method defined in utility section below
		azService.checkIfProjectMemberOrOwner(wrkspcId, projId);
		
		List<TaskDisplayResponseDto> tasks = tRepo.getAllTasksInProject(projId);
		
		return tasks;
	}

	
	public List<TaskDisplayResponseDto> getSearchedTasksInProject(Long wrkspcId, Long projId, String keyword) {
		
		List<TaskDisplayResponseDto> tasks = getAllTasksInProject(wrkspcId, projId);
		
		return tasks.stream()
				.filter(t -> t.getTitle().contains(keyword) 
						|| t.getDescription().contains(keyword))
				.toList();
	}

	
	public void addOneTask(Long wrkspcId, Long projId, TaskCreAndUpDto dto) {
		
		//method defined in utility section below
		azService.checkIfProjectManagerOrOwner(wrkspcId, projId);
		
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
		
		notifClient.sendNotification(
				
					//title
					"New task assigned to you!",
					
					//message
					"Project: "+p.getTitle()
					+"\nTask "+dto.getTitle()+" was assigned to you ("+u.getEmail()+")."
					+"\nPriority: "+dto.getPriority()
					+"\nDue date:"+dto.getDueDate(),
					
					//receiverId
					u.getId(), 
					
					//receiverEmail
					u.getEmail()
		);
	}
	
	
	public void updateOneTask(Long wrkspcId, Long projId, Long taskId, TaskCreAndUpDto dto) {
		
		//method defined in utility section below
		azService.checkIfProjectMemberOrOwner(wrkspcId, projId);
		
				
		//find the task
		Task t = tRepo.findByIdAndProject_Id(taskId, projId).orElseThrow(() -> 
				new ResourceNotFoundException("Task "+taskId+" does not  in project "+projId+"!"));
		
		
		//find current user
		User currentUser = uService.getLoggedInUser();
		
		
		//find current user's membership in workspace
		WorkspaceMembership wm = wMRepo.findByWorkspace_IdAndUser_Id(wrkspcId, currentUser.getId()).orElseThrow();
		
		
		//find current user's membership in project if not owner
		ProjectMembership pm = null;
		if(wm.getWorkspaceRole() != WorkspaceRole.OWNER)
			pm = pMRepo.findByProject_IdAndUser_Id(projId, currentUser.getId()).orElseThrow();
		
		
		//check if current user is workspace's OWNER or project's PROJECT_MANAGER
		//if yes, then they can modify the following fields in a Task
		if(wm.getWorkspaceRole() == WorkspaceRole.OWNER || pm.getProjectRole() == ProjectRole.PROJECT_MANAGER) {
			
			//get back the message based on the changes made
			String mailMessage = checkTaskChanges(dto, t);
			
			t.setTitle(dto.getTitle());
			t.setDescription(dto.getDescription());
			t.setPriority(dto.getPriority());
			t.setDueDate(dto.getDueDate());
			t.setStatus(dto.getStatus());
			
			//send a mail to the assignee/developer of the task about it
			if(mailMessage != null)
				notifClient.sendNotification(
						"Task modified!", 
						
						mailMessage, 
						
						t.getUser().getId(), 
						
						t.getUser().getEmail());
		}
		
		//if current user is a DEVELOPER
		else {
			
			t.setStatus(dto.getStatus());
			
			//if the developer completes the task then find the manager and send them a mail about it
			if(t.getStatus() == TaskStatus.DONE) {
				
				ProjectMembership managerMemberhsip = pMRepo.findByProject_IdAndProjectRole(projId, ProjectRole.PROJECT_MANAGER);
				
				if(managerMemberhsip != null) {
					
					User receiver = managerMemberhsip.getUser();
					
					notifClient.sendNotification(
							"Task modified",
							
							"Project: "+t.getProject().getTitle()
							+"\nTask "+t.getTitle()+" was completed by "
							+t.getUser().getFirstName()+" "+t.getUser().getLastName(), 
							
							receiver.getId(),
							
							receiver.getEmail());
				}
			}
		}
	}
	
	
	public void deleteOneTask(Long wrkspcId, Long projId, Long taskId) {
		
		//method defined in utility section below
		azService.checkIfProjectManagerOrOwner(wrkspcId, projId);
		
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
	
	
	//------------------- utility methods --------------------
	
	public String checkTaskChanges(TaskCreAndUpDto dto, Task t) {
		
		boolean changesMade = false;
		
		StringBuilder mailMessage = new StringBuilder(
				"Project: "+t.getProject().getTitle()
				+"\nFollowing changes were made to task "
				+t.getTitle()+":\n");
		
		if(!dto.getTitle().equals(t.getTitle())
				|| !dto.getDescription().equals(t.getDescription())) {
			
			mailMessage.append("\n\tTitle or description were changed");
			changesMade = true;
		}
		if(dto.getDueDate().equals(t.getDueDate())) {
			
			mailMessage.append("\n\tDue date changed to: "+dto.getDueDate());
			changesMade = true;
		}
		if(dto.getPriority() != t.getPriority()) {
			
			mailMessage.append("\n\tPriority changed to: "+dto.getPriority());
			changesMade = true;
		}
		if(dto.getStatus() != t.getStatus()) {
			
			mailMessage.append("\n\tStatus changed to: "+dto.getStatus());
			changesMade = true;
		}
		
		if(changesMade == true)
			return mailMessage.toString();
		
		else
			return null;
	}
}
