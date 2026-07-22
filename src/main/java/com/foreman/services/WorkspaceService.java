package com.foreman.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foreman.dtos.WorkspaceRequestDto;
import com.foreman.dtos.WrkMemInvDto;
import com.foreman.entities.User;
import com.foreman.entities.Workspace;
import com.foreman.entities.WorkspaceMembership;
import com.foreman.enums.WorkspaceRole;
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
				new RuntimeException("No such workspace exists with that id"));
		
		return workspace;
	}

	public void addOneWorkspace(WorkspaceRequestDto dto) {

		User owner = userRepo.findById(dto.getUserId()).orElseThrow(() ->
			new RuntimeException("No such user with that id!")); 
		
		Workspace w = new Workspace();
		w.setName(dto.getName());
		w.setCreatedOn(LocalDate.now());
		w.setOwner(owner);
		
		workspaceRepo.save(w);
		
		WorkspaceMembership wm = new WorkspaceMembership(w, owner, WorkspaceRole.OWNER);
		
		wmRepo.save(wm);
	}

	public Workspace updateOneWorkspace(Long id, WorkspaceRequestDto dto) {

		Workspace w = workspaceRepo.findById(id).orElseThrow(() -> 
			new RuntimeException("No such workspace exists with that id"));
		
		User owner = userRepo.findById(dto.getUserId()).orElseThrow(() ->
			new RuntimeException("No such user with that id!"));
		
		w.setName(dto.getName());
		w.setOwner(owner);
		workspaceRepo.save(w);
		
		return w;
	}

	public void deleteOneWorkspace(Long id) {
		
		Workspace w = workspaceRepo.findById(id).orElseThrow(() -> 
			new RuntimeException("No such workspace exists with that id"));
		
		wmRepo.deleteByWorkspace_Id(w.getId());
		
		workspaceRepo.delete(w);
	}

	public void addOneMember(Long id, WrkMemInvDto dto) {
		
		Long workspaceId = id;
		Long userId = dto.getUserId();
		WorkspaceRole workspaceRole = dto.getWorkspaceRole();
		
		Workspace w = workspaceRepo.findById(workspaceId).orElseThrow(() -> 
			new RuntimeException("No such workspace exists with id " + workspaceId + "!"));
		
		User u = userRepo.findById(userId).orElseThrow(() -> 
			new RuntimeException("No such user exists with id " + userId + "!"));
		
		boolean check = wmRepo.existsByWorkspace_IdAndUser_Id(w.getId(), u.getId());
		
		if(check == false) {
			
			WorkspaceMembership wm = new WorkspaceMembership(w, u, workspaceRole);
			wmRepo.save(wm);
		}
		else {
			
			throw new RuntimeException("User "+userId+" is already a member of workspace "+workspaceId+ "with role "+dto.getWorkspaceRole().name());
		}
	}
}
