package com.foreman.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foreman.dtos.ProjMemInvDto;
import com.foreman.dtos.UserDisplayResponseDto;

import com.foreman.entities.Project;
import com.foreman.entities.ProjectMembership;
import com.foreman.entities.User;

import com.foreman.enums.ProjectRole;
import com.foreman.exception.DuplicateResourceException;
import com.foreman.exception.ResourceNotFoundException;

import com.foreman.repos.ProjectMembershipRepo;
import com.foreman.repos.ProjectRepo;
import com.foreman.repos.UserRepo;
import com.foreman.repos.WorkspaceMembershipRepo;

@Service
@Transactional
public class ProjectMembershipService {

	@Autowired
	private ProjectRepo projRepo;
	
	@Autowired
	private ProjectMembershipRepo projMemRepo;
	
	@Autowired
	private WorkspaceMembershipRepo wmRepo;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private ProjectService projService;
	

	public List<UserDisplayResponseDto> getAllProjectMembers(Long wrkspcId, Long projId) {
		
		List<UserDisplayResponseDto> projMembers = projMemRepo.getAllProjectMembers(wrkspcId, projId);
		
		return projMembers;
	}
	

	public UserDisplayResponseDto getOneProjectMember(Long wrkspcId, Long projId, Long memId) {

		UserDisplayResponseDto userDisplayResponseDto = projMemRepo.getOneProjectMember(wrkspcId, projId, memId).orElseThrow(() -> 
				new ResourceNotFoundException("User "+memId+" does not exist in workspace "+wrkspcId+" and project "+projId+"!"));
		
		return userDisplayResponseDto;
	}


	public void addOneProjectMember(Long wrkspcId, Long projId, ProjMemInvDto projMemInvDto) {
		
		//just variables
		Long userId = projMemInvDto.getUserId();
		ProjectRole projRole = projMemInvDto.getProjectRole();
		
		//finding the row in workspace_memberships table having the workspace_id and member_id
		//the table looks like this
		//| membership_id | joined_on  | workspace_role | member_id | workspace_id |
		if(wmRepo.existsByWorkspace_IdAndUser_Id(wrkspcId, userId) == false) {
			
			throw new ResourceNotFoundException("User "+userId+" does not exist in workspace "+wrkspcId+"!");
		}
		
		//this method is in this very class
		//using this to see whether the member already is a part of the project
		//if yes (not null) then throw exception and avoid re-adding the user into the project
		if(getOneProjectMember(wrkspcId, projId, userId) != null) {
			
			throw new DuplicateResourceException("User "+userId+" alreeady exists in project "+projId+"!");
		}
		
		//finding the user with the id. Not throwing an exception because the first if block already checks whether the user exists or not
		User member = userRepo.findById(userId).orElseThrow();
		
		//finding project inside the workspace
		Project p = projRepo.findByIdAndWorkspace_Id(projId, wrkspcId).orElseThrow(() -> 
		new ResourceNotFoundException("Workspace "+wrkspcId+" does not contain project "+projId+"!"));
		
		//adding user to project
		projMemRepo.save(new ProjectMembership(p, member, projRole));
	}


	public void updateOneProjectMember(Long wrkspcId, Long projId, ProjMemInvDto projMemInvDto) {
		
		//this method is ProjectService class in this package
		//using this to check whether the project exists inside the workspace
		//otherwise this throws a ResourceNotFoundException saying that the Workspace doesn't contain the Project
		projService.getOneProject(wrkspcId, projId);
		
		//fetching the row from project_memberships table which have the specified project_id and member_id
		//The table looks like this
		//| membership_id | joined_on  | project_role    | project_id | member_id |
		ProjectMembership pm = projMemRepo.findByProject_IdAndUser_Id(projId, projMemInvDto.getUserId()).orElseThrow(() ->
				new ResourceNotFoundException("User "+projMemInvDto.getUserId()+" does not exist in project "+projId+"!"));
		
		//one project can have only one manager. So before making someone else the new "PROJECT_MANAGER"
		//the old manager has to be changed to "DEVELOPER"
		//---pending
		
		//this dto only contains role field aside from user/member id. So...
		pm.setProjectRole(projMemInvDto.getProjectRole());
	}


	public void deleteOneProjectMember(Long wrkspcId, Long projId, Long memId) {
		
		//check updateOneProjectMember() for description
		projService.getOneProject(wrkspcId, projId);
		
		ProjectMembership pm = projMemRepo.findByProject_IdAndUser_Id(projId, memId).orElseThrow(() ->
				new ResourceNotFoundException("User "+memId+" does not exist in project "+projId+"!"));
		
		projMemRepo.delete(pm);
	}
	
	
	//-----------------------------------------Utility methods--------------------------------------------------
	
	
	public User checkUserExistenceInProject(Long wrkspcId, Long projId, Long memId) {
		
		projService.checkProjectExistenceInWorkspace(wrkspcId, projId);
		
		ProjectMembership pm = projMemRepo.findByProject_IdAndUser_Id(projId, memId).orElseThrow(() -> 
				new ResourceNotFoundException("User "+memId+" does not exist in project "+projId+"!"));
		
		return pm.getUser();
	}
}
