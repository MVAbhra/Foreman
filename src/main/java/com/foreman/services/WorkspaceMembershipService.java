package com.foreman.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foreman.dtos.UserDisplayResponseDto;
import com.foreman.dtos.WrkMemInvDto;
import com.foreman.entities.User;
import com.foreman.entities.Workspace;
import com.foreman.entities.WorkspaceMembership;
import com.foreman.enums.WorkspaceRole;
import com.foreman.exception.DuplicateResourceException;
import com.foreman.exception.InvalidActionException;
import com.foreman.exception.ResourceNotFoundException;
import com.foreman.repos.UserRepo;
import com.foreman.repos.WorkspaceMembershipRepo;

@Service
@Transactional
public class WorkspaceMembershipService {
	
	@Autowired
	private WorkspaceMembershipRepo wMRepo;
	
	@Autowired
	private UserRepo uRepo;
	
	@Autowired
	private WorkspaceService wService;

	
	public List<UserDisplayResponseDto> getWorkspaceMembers(Long wrkspcId) {
		
		//this method is in WorkspaceService class in this package. Re-using this to see if 
		//any workspace with the id exists or not. If not, it'll throw an exception halting further progression
		wService.getOneWorkspace(wrkspcId);

		List<UserDisplayResponseDto> members = wMRepo.getWorkspaceMembers(wrkspcId);
		
		return members;
	}

	
	public void addOneMember(Long wrkspcId, WrkMemInvDto dto) {
		
		//checking whether the user is already a part of the workspace
		if(wMRepo.existsByWorkspace_IdAndUser_Id(wrkspcId, dto.getUserId()) == true) {
			
			throw new DuplicateResourceException("User "+dto.getUserId()+" already exists in workspace "+wrkspcId+"!");
		}

		//already explained in getWorkspaceMembers()
		Workspace w  = wService.getOneWorkspace(wrkspcId);
		
		//checking and fetching user with the id
		User u = uRepo.findById(dto.getUserId()).orElseThrow(() ->
				new ResourceNotFoundException("User with id "+dto.getUserId()+" does not exist!"));
		
		//creating the membership to add user to the workspace and saving it
		wMRepo.save(new WorkspaceMembership(w, u, dto.getWorkspaceRole()));
	}

	
	public void updateOneMember(Long wrkspcId, WrkMemInvDto dto) {
		
		//already explained in getWorkspaceMembers()
		wService.getOneWorkspace(wrkspcId);
		
		//checking user with the id
		uRepo.findById(dto.getUserId()).orElseThrow(() ->
				new ResourceNotFoundException("User with id "+dto.getUserId()+" does not exist!"));
		
		WorkspaceMembership wm = wMRepo.findByWorkspace_IdAndUser_Id(wrkspcId, dto.getUserId()).orElseThrow(() ->
				new ResourceNotFoundException("User "+dto.getUserId()+" does not exist in workspace "+wrkspcId+"!"));
		
		//the dto contains workspaceRole aside from userId for now, so...
		wm.setWorkspaceRole(dto.getWorkspaceRole());
	}
	
	
	public UserDisplayResponseDto getOneWorkspaceMember(Long wrkspcId, Long memId) {

		//fetching the row from table workspace_memberships containing the workspace_id and member_id
		//the table looks like this
		//| membership_id | joined_on  | workspace_role | member_id | workspace_id |
		UserDisplayResponseDto dto = wMRepo.getOneWorkspaceMember(wrkspcId, memId).orElseThrow(() ->
				new ResourceNotFoundException("User "+memId+" does not exist in workspace "+wrkspcId+"!"));
		
		return dto;
	}


	public void deleteOneMember(Long wrkspcId, Long memId) {

		//already explained in getWorkspaceMembers()
		wService.getOneWorkspace(wrkspcId);
		
		//checking user with the id
		uRepo.findById(memId).orElseThrow(() ->
				new ResourceNotFoundException("User with id "+memId+" does not exist!"));
		
		WorkspaceMembership wm = wMRepo.findByWorkspace_IdAndUser_Id(wrkspcId, memId).orElseThrow(() ->
				new ResourceNotFoundException("User "+memId+" does not exist in workspace "+wrkspcId+"!"));
		
		if(wm.getWorkspaceRole() == WorkspaceRole.OWNER) {
			
			throw new InvalidActionException("Can not remove owner (user "+memId+") from the workspace!");
		}
		
		wMRepo.delete(wm);
	}
}
