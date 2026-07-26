package com.foreman.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.foreman.dtos.ProjMemInvDto;
import com.foreman.dtos.UserDisplayResponseDto;

import com.foreman.entities.Project;
import com.foreman.entities.ProjectMembership;
import com.foreman.entities.User;
import com.foreman.entities.WorkspaceMembership;
import com.foreman.enums.ProjectRole;
import com.foreman.enums.WorkspaceRole;
import com.foreman.exception.InvalidActionException;
import com.foreman.exception.ResourceNotFoundException;

import com.foreman.repos.ProjectMembershipRepo;
import com.foreman.repos.ProjectRepo;
import com.foreman.repos.UserRepo;
import com.foreman.repos.WorkspaceMembershipRepo;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@Validated
@RequiredArgsConstructor
public class ProjectMembershipService {

	private final ProjectRepo pRepo;
	private final ProjectMembershipRepo pMRepo;
	private final WorkspaceMembershipRepo wMRepo;
	private final UserRepo uRepo;
	private final UserService uService;
	

	public List<UserDisplayResponseDto> getAllProjectMembers(Long wrkspcId, Long projId) {
		
		checkIfProjectManagerOrOwner(wrkspcId, projId);
		
		List<UserDisplayResponseDto> projMembers = pMRepo.getAllProjectMembers(wrkspcId, projId);
		
		return projMembers;
	}


	public void addOneProjectMember(Long wrkspcId, Long projId, ProjMemInvDto dto) {
		
		//get the project with projId
		Project p = pRepo.findByIdAndWorkspace_Id(projId, wrkspcId).orElseThrow(() -> 
				new ResourceNotFoundException("Project "+projId+" does not exist in workspace "+wrkspcId+"!"));
		
		//method defined in utility section below
		checkIfProjectManagerOrOwner(wrkspcId, projId);
		
		//find the user to be added into the project
		User u = uRepo.findByEmail(dto.getEmail()).orElseThrow(() -> 
				new ResourceNotFoundException("User ("+dto.getEmail()+") does not exist"));
		
		//check if user exists in the workspace
		if(wMRepo.existsByWorkspace_IdAndUser_Id(wrkspcId, u.getId()) == false)
			throw new InvalidActionException("User ("+dto.getEmail()+") does not belong to workspace "+wrkspcId+"!");
		
		//add user into the project with role DEVELOPER by default
		ProjectMembership pm = new ProjectMembership(p, u, ProjectRole.DEVELOPER);
		pMRepo.save(pm);
	}


	public void updateOneProjectMember(Long wrkspcId, Long projId, ProjMemInvDto dto) {
		
		//method defined in utility section below
		checkIfProjectManagerOrOwner(wrkspcId, projId);
		
		//find the user whose membership is to updated
		User u = uRepo.findByEmail(dto.getEmail()).orElseThrow(() -> 
			new ResourceNotFoundException("User ("+dto.getEmail()+") does not exist"));
		
		//get user's membership in this project
		ProjectMembership pm = pMRepo.findByProject_IdAndUser_Id(projId, u.getId()).orElseThrow(() ->
				new InvalidActionException("User ("+dto.getEmail()+") does not belong to project "+projId+"!"));
		
		//one project can have max one PROJECT_MANAGER at a moment
		//yes project can have zero managers without any problem 
		//because workspace OWNER can still manage a project so the project is not abandoned
		if(dto.getProjectRole() == ProjectRole.PROJECT_MANAGER 
				&& pMRepo.existsByProject_IdAndProjectRole(projId, ProjectRole.PROJECT_MANAGER) == true)
			throw new InvalidActionException("Project already have a manager. Change current manager to DEVELOPER and try again.");
		
		pm.setProjectRole(dto.getProjectRole());
	}


	public void deleteOneProjectMember(Long wrkspcId, Long projId, Long memId) {
		
		//method defined in utility section below
		checkIfProjectMemberOrOwner(wrkspcId, projId);
		
		//get user's membership in this project
		ProjectMembership pm = pMRepo.findByProject_IdAndUser_Id(projId, memId).orElseThrow(() ->
				new InvalidActionException("User ("+memId+") does not belong to project "+projId+"!"));
		
		//if the action is being performed by a DEVELOPER then 
		//they should only be able to remove themselves from the project and nobody else, 
		//unlike PROJECT_MANAGER or OWNER
		//so get currentUser's membership role in project
		User currentUser = uService.getLoggedInUser();
		ProjectMembership currentUserPm = pMRepo.findByProject_IdAndUser_Id(projId, currentUser.getId()).orElseThrow();
		
		//if the current user is a DEVELOPER
		if(currentUserPm.getProjectRole() == ProjectRole.DEVELOPER) {
				
			//if the DEVELOPER is removing themself
			if(currentUser.getId() == memId) {
				
				pMRepo.delete(pm);
			}
			
			//if the DEVELOPER tried removing someone else
			else
				throw new InvalidActionException("Current user ("+currentUser.getEmail()+") is a DEVELOPER and "
						+ "can only remove themselves and nobody else!");
		}
		
		//if current user is someone other than DEVELOPER then
		else {
			
			pMRepo.delete(pm);	
		}
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
