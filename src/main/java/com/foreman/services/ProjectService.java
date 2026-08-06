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
import com.foreman.enums.ProjectRole;
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
	private final AutherizationService azService;

	public List<Project> getAllProjects(Long wrkspcId) {
		
		List<Project> projects = null;
		
		try {
			
			//if the current user is OWNER, they can see all the projects in the workspace
			azService.checkIfOwner(wrkspcId);
			return pRepo.findByWorkspace_Id(wrkspcId);
		}
		catch(InvalidActionException ex) {
			
			//if the current user is a NON-OWNER workspace member, 
			//then they can only see projects inside the workspace, CREATED BY THEM.
			azService.checkIfWorkspaceMember(wrkspcId);					
				
			User u = uService.getLoggedInUser();
			
			//check the project memberships table and find all the memberships 
			//with the current user's id and where the person is a MANAGER
			List<ProjectMembership> pms = pMRepo.findAllByUser_Id(u.getId());
			
			projects = pms.stream()
						//extract projects from each memberships
						.map(pm -> pm.getProject())
						//filter in all the projects which have this workspace id
						.filter(p -> p.getWorkspace().getId().equals(wrkspcId))
						.toList();
		}
		
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
		azService.checkIfProjectMemberOrOwner(wrkspcId, projId);
		
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
		azService.checkIfProjectManagerOrOwner(wrkspcId, projId);
		
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
		azService.checkIfProjectManagerOrOwner(wrkspcId, projId);
		
		//find all the memberships related to the project
		List<ProjectMembership> pms = pMRepo.findByProject_Id(projId);
		
		//delete all related memberships
		pMRepo.deleteAll(pms);
		
		//then delete project
		pRepo.delete(p);
	}
}
