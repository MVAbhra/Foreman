package com.foreman.services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
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
import com.foreman.microservices.notification.NotificationClient;
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
	private final NotificationClient notifClient;
	private final AutherizationService azService;
	
	@Value("${app.frontend-domain}")
	private String frontendDomain;

	public List<UserDisplayResponseDto> getWorkspaceMembers(Long wrkspcId) {

		azService.checkIfWorkspaceMember(wrkspcId);

		return wMRepo.getWorkspaceMembers(wrkspcId);
	}

	
	public String inviteOneMember(Long wrkspcId, WrkMemInvDto dto) {

		//check if the workspace is real
		Workspace w = wRepo.findById(wrkspcId)
				.orElseThrow(() -> new ResourceNotFoundException("Workspace " + wrkspcId + " does not exist!"));

		azService.checkIfOwner(wrkspcId);
		
		//need the owner to attach to the mail
		User owner = uService.getLoggedInUser();

		//check if the user is real
		User u = uRepo.findByEmail(dto.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User " + dto.getEmail() + " does not exist!"));
		
		//check if user already belongs to workspace
		if (wMRepo.existsByWorkspace_IdAndUser_Id(wrkspcId, u.getId()))
			throw new DuplicateResourceException(
					"User " + u.getId() + " already belongs to workspace " + wrkspcId + "!");
		
		//create the invitation link. Clicking on which sends a request to React.
		//React catches it and extracts workspaceid and email
		//then it builds the backend url and axios getcalls it
		//which fires the corresponding addOneMember() method here
		String invitationLink = 
				frontendDomain
				+"/join?wrkspc="+w.getId()
				+"&email="+URLEncoder.encode(u.getEmail(), StandardCharsets.UTF_8);
		
		//create the email body
		String mailMessage = 
				"Dear "+u.getFirstName()+", \nYou were invited to be a member of"
				+" workspace "+w.getName()
				+" by "+owner.getFirstName()+" "+owner.getLastName()+" ("+owner.getEmail()+")!"
				+"\nInvitation link: "+invitationLink
				+"\nClick on the link join the Workspace.";
				
		//send email to the user's email address
		notifClient.sendNotification(
				"New workspace invitation!",
				mailMessage, 
				u.getId(), 
				u.getEmail());
		
		return "Sent invitation mail to the user to join the workspace!";
	}
	
	
	public void addOneMember(Long wrkspcId, String email) {
		
		//check if the workspace is real
		Workspace w = wRepo.findById(wrkspcId)
				.orElseThrow(() -> new ResourceNotFoundException("Workspace " + wrkspcId + " does not exist!"));

		//check if the user is real
		User u = uRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User " + email + " does not exist!"));
		
		//check if user already belongs to workspace
		if (wMRepo.existsByWorkspace_IdAndUser_Id(wrkspcId, u.getId()))
			throw new DuplicateResourceException(
					"User " + u.getId() + " already belongs to workspace " + wrkspcId + "!");
		
		//check if the user with invitation is logged in and trying to joining workspace
		if(email.equals(uService.getLoggedInUser().getEmail()) == false)
				throw new InvalidActionException("Please login with "+email+" to use the invitation!");

		wMRepo.save(new WorkspaceMembership(w, u, WorkspaceRole.MEMBER));
	}
	

	public void updateOneMember(Long wrkspcId, WrkMemInvDto dto) {

		azService.checkIfOwner(wrkspcId);

		User u = uRepo.findByEmail(dto.getEmail())
				.orElseThrow(() -> new ResourceNotFoundException("User " + dto.getEmail() + " does not exist!"));

		WorkspaceMembership wm = wMRepo.findByWorkspace_IdAndUser_Id(wrkspcId, u.getId())
				.orElseThrow(() -> new InvalidActionException(
						"User " + dto.getEmail() + " does not belong to workspace " + wrkspcId + "!"));

		wm.setWorkspaceRole(dto.getWorkspaceRole());
	}

	
	public void deleteOneMember(Long wrkspcId, Long memId) {

		azService.checkIfOwner(wrkspcId);

		uRepo.findById(memId).orElseThrow(() -> new ResourceNotFoundException("User " + memId + " does not exist!"));

		WorkspaceMembership wm = wMRepo.findByWorkspace_IdAndUser_Id(wrkspcId, memId).orElseThrow(
				() -> new InvalidActionException("User " + memId + " does not belong to workspace " + wrkspcId + "!"));

		if (wm.getWorkspaceRole() == WorkspaceRole.OWNER)
			throw new InvalidActionException("Owner cannot be removed from the workspace.");

		wMRepo.delete(wm);
	}
}
