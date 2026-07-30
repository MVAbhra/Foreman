package com.foreman.services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.foreman.dtos.ProjMemInvDto;
import com.foreman.dtos.UserDisplayResponseDto;

import com.foreman.entities.Project;
import com.foreman.entities.ProjectMembership;
import com.foreman.entities.User;
import com.foreman.enums.ProjectRole;
import com.foreman.exception.DuplicateResourceException;
import com.foreman.exception.InvalidActionException;
import com.foreman.exception.ResourceNotFoundException;
import com.foreman.microservices.notification.NotificationClient;
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
	private final AutherizationService azService;
	private final NotificationClient notifClient;
	
	@Value("${app.frontend-domain}")
	private String frontendDomain;
	

	public List<UserDisplayResponseDto> getAllProjectMembers(Long wrkspcId, Long projId) {
		
		azService.checkIfProjectManagerOrOwner(wrkspcId, projId);
		
		List<UserDisplayResponseDto> projMembers = pMRepo.getAllProjectMembers(wrkspcId, projId);
		
		return projMembers;
	}


	public String inviteOneProjectMember(Long wrkspcId, Long projId, ProjMemInvDto dto) {
		
		//get the project with projId
		Project p = pRepo.findByIdAndWorkspace_Id(projId, wrkspcId).orElseThrow(() -> 
				new ResourceNotFoundException("Project "+projId+" does not exist in workspace "+wrkspcId+"!"));
		
		//method defined in utility section below
		azService.checkIfProjectManagerOrOwner(wrkspcId, projId);
		
		User currentUser = uService.getLoggedInUser();
		
		//find the user to be added into the project
		User u = uRepo.findByEmail(dto.getEmail()).orElseThrow(() -> 
				new ResourceNotFoundException("User ("+dto.getEmail()+") does not exist"));
		
		//check if user exists in the workspace
		if(wMRepo.existsByWorkspace_IdAndUser_Id(wrkspcId, u.getId()) == false)
			throw new InvalidActionException("User ("+dto.getEmail()+") does not belong to workspace "+wrkspcId+"!");
		
		//check if user already exists in the project
		if(pMRepo.existsByProject_IdAndUser_Id(projId, u.getId()) == true)
			throw new DuplicateResourceException("User ("+u.getEmail()+") already belongs to project "+p.getId()+"!");
		
		
		//create the invitation link. Clicking on which sends a request to React.
		//React catches it and extracts workspaceid and email
		//then it builds the backend url and axios getcalls it
		//which fires the corresponding addOneMember() method here
		String invitationLink = 
				frontendDomain
				+"/join-project?wrkspcId="+wrkspcId
				+"&projId="+projId
				+"&email="+URLEncoder.encode(u.getEmail(), StandardCharsets.UTF_8);
		
		//create the email body
		String mailMessage = 
				"Dear "+u.getFirstName()+", \nYou were invited to be a part of the development of"
				+"\nproject "+p.getTitle()
				+"\nby "+currentUser.getFirstName()+" "+currentUser.getLastName()+" ("+currentUser.getEmail()+")!"
				+"\nInvitation link: "+invitationLink
				+"\nClick on the link join the project.";
				
		//send email to the user's email address
		notifClient.sendNotification(
				"New project invitation!", 
				mailMessage, 
				u.getId(), 
				u.getEmail());
		
		return "User ("+u.getEmail()+") was invited to join project "+p.getId()+"!";
	}
	
	
	public void addOneProjectMember(Long wrkspcId, Long projId, String email) {

		//check if the user with invitation is logged in and trying to join the project
		if(email.equals(uService.getLoggedInUser().getEmail()) == false)
				throw new InvalidActionException("Please login with "+email+" to use the invitation!");
		
		//get the project with projId
		Project p = pRepo.findByIdAndWorkspace_Id(projId, wrkspcId).orElseThrow(() -> 
				new ResourceNotFoundException("Project "+projId+" does not exist in workspace "+wrkspcId+"!"));
		
		//find the user to be added into the project
		User u = uRepo.findByEmail(email).orElseThrow(() -> 
				new ResourceNotFoundException("User ("+email+") does not exist"));
		
		//check if user exists in the workspace
		if(wMRepo.existsByWorkspace_IdAndUser_Id(wrkspcId, u.getId()) == false)
			throw new InvalidActionException("User ("+u.getEmail()+") does not belong to workspace "+wrkspcId+"!");
		
		//check if user already exists in the project
		if(pMRepo.existsByProject_IdAndUser_Id(projId, u.getId()) == true)
			throw new DuplicateResourceException("User ("+u.getEmail()+") already belongs to project "+p.getId()+"!");
		
		//adding every user as DEVELOPER temporarily. Later role can be changed to PROJECT_MANAGER
		pMRepo.save(new ProjectMembership(p, u, ProjectRole.DEVELOPER));
	}


	public void updateOneProjectMember(Long wrkspcId, Long projId, ProjMemInvDto dto) {
		
		//method defined in utility section below
		azService.checkIfProjectManagerOrOwner(wrkspcId, projId);
		
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
		azService.checkIfProjectMemberOrOwner(wrkspcId, projId);
		
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
}
