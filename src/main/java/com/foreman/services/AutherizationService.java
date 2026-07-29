package com.foreman.services;

import org.springframework.stereotype.Service;

import com.foreman.entities.ProjectMembership;
import com.foreman.entities.User;
import com.foreman.entities.WorkspaceMembership;
import com.foreman.enums.ProjectRole;
import com.foreman.enums.WorkspaceRole;
import com.foreman.exception.InvalidActionException;
import com.foreman.exception.ResourceNotFoundException;
import com.foreman.repos.ProjectMembershipRepo;
import com.foreman.repos.WorkspaceMembershipRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutherizationService {

	private final UserService uService;
	private final WorkspaceMembershipRepo wMRepo;
	private final ProjectMembershipRepo pMRepo;

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

	public void checkIfProjectMemberOrOwner(Long wrkspcId, Long projId) {

		// get logged in user
		User currentUser = uService.getLoggedInUser();

		// get the user's membership in the workspace
		WorkspaceMembership wm = wMRepo.findByWorkspace_IdAndUser_Id(wrkspcId, currentUser.getId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"You (" + currentUser.getEmail() + ") do not belong to workspace " + wrkspcId + "!"));

		// if user is workspace's OWNER then return/authorize
		if (wm.getWorkspaceRole() == WorkspaceRole.OWNER)
			return;

		// get the user's membership in the project
		ProjectMembership pm = pMRepo.findByProject_IdAndUser_Id(projId, currentUser.getId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"You (" + currentUser.getEmail() + ") do not belong to project " + projId + "!"));

		// if user is project's member then return/authorize
		if (pm != null)
			return;

		// if user is neither workspace's OWNER or project's member
		throw new InvalidActionException(
				"You (" + currentUser.getEmail() + ") are not authorized to view project " + projId + "!");
	}

	public void checkIfProjectManagerOrOwner(Long wrkspcId, Long projId) {

		// get logged in user
		User currentUser = uService.getLoggedInUser();

		// get the user's membership in the workspace
		WorkspaceMembership wm = wMRepo.findByWorkspace_IdAndUser_Id(wrkspcId, currentUser.getId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"You (" + currentUser.getEmail() + ") do not belong to workspace " + wrkspcId + "!"));

		// if user is workspace's OWNER then return/authorize
		if (wm.getWorkspaceRole() == WorkspaceRole.OWNER)
			return;

		// get the user's membership in the project
		ProjectMembership pm = pMRepo.findByProject_IdAndUser_Id(projId, currentUser.getId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"You (" + currentUser.getEmail() + ") do not belong to project " + projId + "!"));

		// if user is project's PROJECT_MANAGER then return/authorize
		if (pm.getProjectRole() == ProjectRole.PROJECT_MANAGER)
			return;

		// if user is neither workspace's OWNER or project's PROJECT_MANAGER
		throw new InvalidActionException(
				"You (" + currentUser.getEmail() + ") are not authorized to update or delete project " + projId + "!");
	}
}
