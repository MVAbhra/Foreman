package com.foreman.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.foreman.dtos.WorkspaceCreationAndUpdationDto;
import com.foreman.entities.User;
import com.foreman.entities.Workspace;
import com.foreman.entities.WorkspaceMembership;
import com.foreman.enums.WorkspaceRole;
import com.foreman.exception.ResourceNotFoundException;
import com.foreman.repos.WorkspaceMembershipRepo;
import com.foreman.repos.WorkspaceRepo;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@Validated
@RequiredArgsConstructor
public class WorkspaceService {

	private final WorkspaceRepo wRepo;
	private final WorkspaceMembershipRepo wMRepo;
	private final UserService uService;
	private final AutherizationService azService;

	public List<Workspace> getAllWorkspaces() {

		User currentUser = uService.getLoggedInUser();

		return wRepo.findAll().stream()
				.filter(w -> wMRepo.existsByWorkspace_IdAndUser_Id(w.getId(), currentUser.getId())).toList();
	}

	public Workspace getOneWorkspace(Long wrkspcId) {

		Workspace w = wRepo.findById(wrkspcId)
				.orElseThrow(() -> new ResourceNotFoundException("Workspace " + wrkspcId + " does not exist!"));

		azService.checkIfWorkspaceMember(wrkspcId);

		return w;
	}

	public void addOneWorkspace(WorkspaceCreationAndUpdationDto dto) {

		User currentUser = uService.getLoggedInUser();

		Workspace w = new Workspace(dto.getName());

		w = wRepo.save(w);

		WorkspaceMembership wm = new WorkspaceMembership(w, currentUser, WorkspaceRole.OWNER);

		wMRepo.save(wm);
	}

	public void updateOneWorkspace(Long wrkspcId, WorkspaceCreationAndUpdationDto dto) {

		Workspace w = wRepo.findById(wrkspcId)
				.orElseThrow(() -> new ResourceNotFoundException("Workspace " + wrkspcId + " does not exist!"));

		azService.checkIfOwner(wrkspcId);

		w.setName(dto.getName());
	}

	public void deleteOneWorkspace(Long wrkspcId) {

		Workspace w = wRepo.findById(wrkspcId)
				.orElseThrow(() -> new ResourceNotFoundException("Workspace " + wrkspcId + " does not exist!"));

		azService.checkIfOwner(wrkspcId);

		wMRepo.deleteByWorkspace_Id(wrkspcId);

		wRepo.delete(w);
	}
}
