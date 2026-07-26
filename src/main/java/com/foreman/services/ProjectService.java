package com.foreman.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.foreman.dtos.ProjectCreationAndUpdationDto;
import com.foreman.dtos.ProjectDisplayDto;
import com.foreman.entities.Project;
import com.foreman.entities.ProjectMembership;
import com.foreman.entities.User;
import com.foreman.entities.Workspace;
import com.foreman.entities.WorkspaceMembership;
import com.foreman.enums.ProjectRole;
import com.foreman.enums.WorkspaceRole;
import com.foreman.exception.InvalidActionException;
import com.foreman.exception.ResourceNotFoundException;
import com.foreman.repos.ProjectMembershipRepo;
import com.foreman.repos.ProjectRepo;
import com.foreman.repos.WorkspaceMembershipRepo;
import com.foreman.repos.WorkspaceRepo;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@Validated
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectRepo pRepo;
	private final ProjectMembershipRepo pMRepo;
	private final WorkspaceMembershipRepo wMRepo;
	private final WorkspaceRepo wRepo;
	private final UserService uService;

	public List<Project> getAllProjects(Long wrkspcId) {
		
		checkIfOwner(wrkspcId);
		
		List<Project> projects = pRepo.findByWorkspace_Id(wrkspcId);
		
		return projects;
	}

	
	public void createOneProject(Long wrkspcId, ProjectCreationAndUpdationDto dto) {
		
		//get logged in user
		User currentUser = uService.getLoggedInUser();
		
		//the user is allowed to create a project only if they belong to the workspace
		boolean authorized = wMRepo.existsByWorkspace_IdAndUser_Id(wrkspcId, currentUser.getId());
		
		//get the workspace with wrkspcId
		Workspace w = wRepo.findById(wrkspcId)
				.orElseThrow(() -> 
					new ResourceNotFoundException("Workspace "+wrkspcId+" does not exist!"));
		
		if(authorized == true) {
			
			//create a new untracked project
			Project p = new Project(dto.getTitle(), dto.getDescription(), w);
			
			//push it into the projects table and get back its tracked version
			p = pRepo.save(p);
			
			//create a new project_membership to add the current user or creator as PROJECT_MANAGER
			ProjectMembership pm = new ProjectMembership(p, currentUser, ProjectRole.PROJECT_MANAGER);
			
			//push it into the project_memberships table
			pMRepo.save(pm);
		}
		else {
			
			throw new InvalidActionException("You ("+currentUser.getEmail()+") don't "
					+ "belong to workspace "+wrkspcId+"!");
		}
	}

	
	public ProjectDisplayDto getOneProject(Long wrkspcId, Long projId) {
		
		//get the project if the it belongs to the workspace
		Project p = pRepo.findByIdAndWorkspace_Id(projId, wrkspcId)
				.orElseThrow(() -> 
					new ResourceNotFoundException("Project "+projId+" does not belong to workspace "+wrkspcId+"!")
				);
		
		//method defined in utility section below
		checkIfProjectMemberOrOwner(wrkspcId, projId);
		
		//passed the checks and authorized to view the project
		ProjectDisplayDto dto = new ProjectDisplayDto(projId, p.getTitle(), p.getDescription(), wrkspcId);
		
		return dto;
	}


	public void updateOneProject(Long wrkspcId, Long projId, ProjectCreationAndUpdationDto dto) {
		
		//get the project if the it belongs to the workspace
		Project p = pRepo.findByIdAndWorkspace_Id(projId, wrkspcId)
				.orElseThrow(() -> 
					new ResourceNotFoundException("Project "+projId+" does not belong to workspace "+wrkspcId+"!")
				);
		
		//method defined in utility section below
		checkIfProjectManagerOrOwner(wrkspcId, projId);
		
		//passed the checks and authorized to update project's details
		p.setTitle(dto.getTitle());
		p.setDescription(dto.getDescription());
	}


	public void deleteOneProject(Long wrkspcId, Long projId) {

		//get the project if the it belongs to the workspace
		Project p = pRepo.findByIdAndWorkspace_Id(projId, wrkspcId)
				.orElseThrow(() -> 
					new ResourceNotFoundException("Project "+projId+" does not belong to workspace "+wrkspcId+"!")
				);
		
		//method defined in utility section below
		checkIfProjectManagerOrOwner(wrkspcId, projId);
		
		//find all the memberships related to the project
		List<ProjectMembership> pms = pMRepo.findByProject_Id(projId);
		
		//delete all related memberships
		pMRepo.deleteAll(pms);
		
		//then delete project
		pRepo.delete(p);
	}
	
	
	//----------------------------- Utility methods ---------------------------------------
	
	
	public Project checkProjectExistenceInWorkspace(Long wrkspcId, Long projId) {
		
		if(wRepo.existsById(wrkspcId) == false) {
			
			throw new ResourceNotFoundException("Workspace "+wrkspcId+" does not exist!");
		}
		
		Project p = pRepo.findByIdAndWorkspace_Id(projId, wrkspcId).orElseThrow(() -> 
		new ResourceNotFoundException("Workspace "+wrkspcId+" does not contain project "+projId+"!"));
		
		return p;
	}
	
	
	public void checkIfOwner(Long wrkspcId) {
		
		//get logged in user
		User currentUser = uService.getLoggedInUser();
		
		//get the user's membership in the workspace
		WorkspaceMembership wm = wMRepo.findByWorkspace_IdAndUser_Id(wrkspcId, currentUser.getId())
				.orElseThrow(() -> new ResourceNotFoundException("You ("+currentUser.getEmail()+") do not belong to workspace "+wrkspcId+"!"));
		
		//if user is workspace's OWNER then return/authorize
		if(wm.getWorkspaceRole() == WorkspaceRole.OWNER) return; 
		
		//if not OWNER
		throw new InvalidActionException("You ("+currentUser.getEmail()+") require ownership of workspace "+wrkspcId+" to perform the action!");
	}
	
	
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
