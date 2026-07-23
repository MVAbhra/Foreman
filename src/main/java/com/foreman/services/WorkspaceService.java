package com.foreman.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foreman.dtos.UserDisplayResponseDto;
import com.foreman.dtos.WorkspaceCreationAndUpdationDto;
import com.foreman.dtos.WrkMemInvDto;
import com.foreman.entities.User;
import com.foreman.entities.Workspace;
import com.foreman.entities.WorkspaceMembership;
import com.foreman.enums.WorkspaceRole;
import com.foreman.exception.DuplicateResourceException;
import com.foreman.exception.ResourceNotFoundException;
import com.foreman.repos.UserRepo;
import com.foreman.repos.WorkspaceMembershipRepo;
import com.foreman.repos.WorkspaceRepo;

@Service
@Transactional
public class WorkspaceService {

	@Autowired
	private WorkspaceRepo workspaceRepo;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private WorkspaceMembershipRepo wmRepo;

	public List<Workspace> getAllWorkspaces() {

		return workspaceRepo.findAll();
	}

	public Workspace getOneWorkspace(Long id) {

		Workspace workspace = workspaceRepo.findById(id).orElseThrow(() -> 
				new ResourceNotFoundException("No such workspace exists with that id"));
		
		return workspace;
	}

	public void addOneWorkspace(WorkspaceCreationAndUpdationDto dto) {

		User owner = userRepo.findById(dto.getOwnerId()).orElseThrow(() ->
			new ResourceNotFoundException("No such user with that id!")); 
		
		Workspace w = new Workspace(dto.getName());
		
		workspaceRepo.save(w);
		
		WorkspaceMembership wm = new WorkspaceMembership(w, owner, WorkspaceRole.OWNER);
		
		wmRepo.save(wm);
	}

	public Workspace updateOneWorkspace(Long id, WorkspaceCreationAndUpdationDto dto) {

		Workspace w = workspaceRepo.findById(id).orElseThrow(() -> 
			new ResourceNotFoundException("No such workspace exists with that id"));
		
		w.setName(dto.getName());
		workspaceRepo.save(w);
		
		return w;
	}

	public void deleteOneWorkspace(Long id) {
		
		Workspace w = workspaceRepo.findById(id).orElseThrow(() -> 
			new ResourceNotFoundException("No such workspace exists with that id"));
		
		wmRepo.deleteByWorkspace_Id(w.getId());
		
		workspaceRepo.delete(w);
	}

	public void addOneMember(Long id, WrkMemInvDto dto) {
		
		Long workspaceId = id;
		Long userId = dto.getUserId();
		WorkspaceRole workspaceRole = dto.getWorkspaceRole();
		
		Workspace w = workspaceRepo.findById(workspaceId).orElseThrow(() -> 
			new ResourceNotFoundException("No such workspace exists with id " + workspaceId + "!"));
		
		User u = userRepo.findById(userId).orElseThrow(() -> 
			new ResourceNotFoundException("No such user exists with id " + userId + "!"));
		
		boolean check = wmRepo.existsByWorkspace_IdAndUser_Id(w.getId(), u.getId());
		
		if(check == false) {
			
			WorkspaceMembership wm = new WorkspaceMembership(w, u, workspaceRole);
			wmRepo.save(wm);
		}
		else {
			
			throw new DuplicateResourceException("User "+userId+" is already a member of workspace "+workspaceId+ "with role "+dto.getWorkspaceRole().name());
		}
	}

	public List<UserDisplayResponseDto> getAllMembersOfOneWrkSpc(Long id) {
		
		if(workspaceRepo.existsById(id) == false) {
			
			throw new ResourceNotFoundException("No such workspace exists with id " + id + "!");
		}

		List<UserDisplayResponseDto> members = wmRepo.findWorkspaceMembers(id);
		
		return members;
	}
}
