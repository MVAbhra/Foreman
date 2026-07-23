package com.foreman.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foreman.dtos.ProjectCreationAndUpdationDto;
import com.foreman.dtos.ProjectDisplayDto;
import com.foreman.entities.Project;
import com.foreman.entities.ProjectMembership;
import com.foreman.entities.User;
import com.foreman.entities.Workspace;
import com.foreman.enums.ProjectRole;
import com.foreman.exception.ResourceNotFoundException;
import com.foreman.repos.ProjectMembershipRepo;
import com.foreman.repos.ProjectRepo;
import com.foreman.repos.UserRepo;
import com.foreman.repos.WorkspaceMembershipRepo;
import com.foreman.repos.WorkspaceRepo;

@Service
@Transactional
public class ProjectService {

	@Autowired
	private ProjectRepo projRepo;
	
	@Autowired
	private ProjectMembershipRepo projMemRepo;
	
	@Autowired
	private WorkspaceMembershipRepo wmRepo;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private WorkspaceRepo wRepo;

	public List<Project> getAllProjects(Long wrkspcId) {
		
		List<Project> projects = projRepo.findByWorkspace_Id(wrkspcId);
		
		return projects;
	}

	
	public void createOneProject(Long wrkspcId, ProjectCreationAndUpdationDto dto) {
		
		Long workspaceId = wrkspcId;
		Long managerId = dto.getManagerId();
		
		User manager = userRepo.findById(managerId).orElseThrow(() -> new ResourceNotFoundException("No such exists with id "+managerId+"!"));
		
		Workspace workspace = wRepo.findById(workspaceId).orElseThrow(() -> new ResourceNotFoundException("No such workspace exist with id "+workspaceId+"!"));
		
		if(wmRepo.existsByWorkspace_IdAndUser_Id(workspaceId, managerId) == false) {
			
			throw new ResourceNotFoundException("User "+managerId+" doesn't belong to the workspace "+workspaceId+"!");
		}
		
		Project project = projRepo.save(
				
				new Project(
				
						dto.getTitle(),
						dto.getDescription(),
						workspace
				)
		);
		
		projMemRepo.save(
		
				new ProjectMembership(project, manager, ProjectRole.PROJECT_MANAGER)
		);
	}

	
	public ProjectDisplayDto getOneProject(Long wrkspcId, Long projId) {
		
		if(wRepo.existsById(wrkspcId) == false) {
			
			throw new ResourceNotFoundException("Workspace "+wrkspcId+" does not exist!");
		}
		
		Project p = projRepo.findByIdAndWorkspace_Id(projId, wrkspcId).orElseThrow(() -> 
		new ResourceNotFoundException("Workspace "+wrkspcId+" does not contain project "+projId+"!")); 
		
//		ProjectMembership managerMembership = projMemRepo.findByProject_IdAndProjectRole(projId, ProjectRole.valueOf("PROJECT_MANAGER"));
		
		
		 
		ProjectDisplayDto displayDto = new ProjectDisplayDto(
				
				projId, 
				p.getTitle(), 
				p.getDescription(), 
				wrkspcId, 
				p.getWorkspace().getName()
//				managerMembership.getUser().getId()
		);
		
		return displayDto;
	}


	public ProjectDisplayDto updateOneProject(Long wrkspcId, Long projId, ProjectCreationAndUpdationDto dto) {
		
		if(wRepo.existsById(wrkspcId) == false) {
			
			throw new ResourceNotFoundException("Workspace "+wrkspcId+" does not exist!");
		}
		
		Project p = projRepo.findByIdAndWorkspace_Id(projId, wrkspcId).orElseThrow(() -> 
				new ResourceNotFoundException("Workspace "+wrkspcId+" does not contain project "+projId+"!")); 
		
		p.setTitle(dto.getTitle());
		p.setDescription(dto.getDescription());
		
		ProjectDisplayDto displayDto = getOneProject(wrkspcId, projId);
		
		return displayDto;
	}


	public void deleteOneProject(Long wrkspcId, Long projId) {

		if(wRepo.existsById(wrkspcId) == false) {
			
			throw new ResourceNotFoundException("Workspace "+wrkspcId+" does not exist!");
		}
		
		Project p = projRepo.findByIdAndWorkspace_Id(projId, wrkspcId).orElseThrow(() -> 
				new ResourceNotFoundException("Workspace "+wrkspcId+" does not contain project "+projId+"!"));
		
		List<ProjectMembership> pms = projMemRepo.findByProject_Id(projId);
		
		projMemRepo.deleteAll(pms);
		
		projRepo.delete(p);
	}
}
