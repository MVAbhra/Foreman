package com.foreman.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

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
import com.foreman.repos.WorkspaceRepo;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@Validated
@RequiredArgsConstructor
public class WorkspaceMembershipService {

	private final WorkspaceMembershipRepo wMRepo;
	private final UserRepo uRepo;
	private final WorkspaceRepo wRepo;
	private final UserService uService;

	public List<UserDisplayResponseDto> getWorkspaceMembers(Long wrkspcId) {

		checkIfWorkspaceMember(wrkspcId);

		return wMRepo.getWorkspaceMembers(wrkspcId);
	}

	
	public void addOneMember(Long wrkspcId, WrkMemInvDto dto) {

		Workspace workspace = wRepo.findById(wrkspcId)
				.orElseThrow(() -> new ResourceNotFoundException("Workspace " + wrkspcId + " does not exist!"));

		checkIfOwner(wrkspcId);

		User u = uRepo.findByEmail(dto.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User " + dto.getEmail() + " does not exist!"));

		if (wMRepo.existsByWorkspace_IdAndUser_Id(wrkspcId, u.getId()))
			throw new DuplicateResourceException(
					"User " + u.getId() + " already belongs to workspace " + wrkspcId + "!");

		wMRepo.save(new WorkspaceMembership(workspace, u, dto.getWorkspaceRole()));
		}
	

	public void updateOneMember(Long wrkspcId, WrkMemInvDto dto) {

		checkIfOwner(wrkspcId);

		User u = uRepo.findByEmail(dto.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User " + dto.getEmail() + " does not exist!"));

		WorkspaceMembership wm = wMRepo.findByWorkspace_IdAndUser_Id(wrkspcId, u.getId())
				.orElseThrow(() -> new InvalidActionException(
						"User " + dto.getEmail() + " does not belong to workspace " + wrkspcId + "!"));

		wm.setWorkspaceRole(dto.getWorkspaceRole());
	}

	
	public void deleteOneMember(Long wrkspcId, Long memId) {

		checkIfOwner(wrkspcId);

		uRepo.findById(memId).orElseThrow(() -> new ResourceNotFoundException("User " + memId + " does not exist!"));

		WorkspaceMembership wm = wMRepo.findByWorkspace_IdAndUser_Id(wrkspcId, memId).orElseThrow(
				() -> new InvalidActionException("User " + memId + " does not belong to workspace " + wrkspcId + "!"));

		if (wm.getWorkspaceRole() == WorkspaceRole.OWNER)
			throw new InvalidActionException("Owner cannot be removed from the workspace.");

		wMRepo.delete(wm);
	}

	// ------------------------- utility methods ------------------------------------

	public void checkIfWorkspaceMember(Long wrkspcId) {

		User currentUser = uService.getLoggedInUser();

		WorkspaceMembership wm = wMRepo.findByWorkspace_IdAndUser_Id(wrkspcId, currentUser.getId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"You (" + currentUser.getEmail() + ") do not belong to workspace " + wrkspcId + "!"));

		if (wm != null)
			return;

		throw new InvalidActionException(
				"You (" + currentUser.getEmail() + ") are not authorized to access workspace " + wrkspcId + "!");
	}

	public void checkIfOwner(Long wrkspcId) {

		User currentUser = uService.getLoggedInUser();

		WorkspaceMembership wm = wMRepo.findByWorkspace_IdAndUser_Id(wrkspcId, currentUser.getId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"You (" + currentUser.getEmail() + ") do not belong to workspace " + wrkspcId + "!"));

		if (wm.getWorkspaceRole() == WorkspaceRole.OWNER)
			return;

		throw new InvalidActionException("You (" + currentUser.getEmail() + ") require ownership of workspace "
				+ wrkspcId + " to perform the action!");
	}
}
