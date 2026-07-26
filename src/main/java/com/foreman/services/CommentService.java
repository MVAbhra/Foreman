package com.foreman.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.foreman.dtos.CommentCreAndUpDto;
import com.foreman.dtos.CommentDisplayResponseDto;
import com.foreman.entities.Comment;
import com.foreman.entities.ProjectMembership;
import com.foreman.entities.Task;
import com.foreman.entities.User;
import com.foreman.entities.WorkspaceMembership;
import com.foreman.enums.ProjectRole;
import com.foreman.enums.WorkspaceRole;
import com.foreman.exception.InvalidActionException;
import com.foreman.exception.ResourceNotFoundException;
import com.foreman.repos.CommentRepo;
import com.foreman.repos.ProjectMembershipRepo;
import com.foreman.repos.TaskRepo;
import com.foreman.repos.WorkspaceMembershipRepo;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@Validated
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepo cRepo;
    private final UserService uService;
    private final WorkspaceMembershipRepo wMRepo;
    private final ProjectMembershipRepo pMRepo;
    private final TaskRepo tRepo;
    


    public List<CommentDisplayResponseDto> getAllCommentsInTask(Long wrkspcId, Long projId, Long taskId){

        checkIfProjectMemberOrOwner(wrkspcId, projId);
        
        //check if the task belongs to the project
        if(tRepo.existsByIdAndProject_Id(taskId, projId) == false)
        	throw new ResourceNotFoundException("Task "+taskId+" does not  in project "+projId+"!");

        return cRepo.getAllCommentsInTask(taskId);
    }


    public String addOneComment(Long wrkspcId, Long projId, Long taskId, CommentCreAndUpDto dto){

       checkIfProjectMemberOrOwner(wrkspcId, projId);
       
       //find the task
 		Task t = tRepo.findByIdAndProject_Id(taskId, projId).orElseThrow(() -> 
 				new ResourceNotFoundException("Task "+taskId+" does not  in project "+projId+"!"));
       
       //get the current user
       User currentUser = uService.getLoggedInUser();
       
       //create new comment
       Comment c = new Comment(dto.getMessage(), t, currentUser);
       
       //save the comment
       cRepo.save(c);
       
       return "Comment was added by user "+currentUser.getEmail()+" on task "+taskId+" in project "+projId+" successfully!";
    }


//    public CommentDisplayResponseDto getOneCommentInTask(
//            Long wrkspcId,
//            Long projId,
//            Long taskId,
//            Long commentId){
//
//        tService.checkTaskExistenceInProject(taskId, projId);
//
//        Comment c = checkCommentExistenceInTask(commentId, taskId);
//
//        return new CommentDisplayResponseDto(
//                c.getId(),
//                c.getMessage(),
//                c.getCreatedOn(),
//                taskId,
//                c.getUser().getId(),
//                projId,
//                wrkspcId);
//    }


    public void updateOneComment(
            Long wrkspcId,
            Long projId,
            Long taskId,
            Long commentId,
            CommentCreAndUpDto dto){
    	
    	checkIfProjectMemberOrOwner(wrkspcId, projId);

        User currentUser = uService.getLoggedInUser();
        
        //find the task
        tRepo.findByIdAndProject_Id(taskId, projId).orElseThrow(() -> 
 				new ResourceNotFoundException("Task "+taskId+" does not exist in project "+projId+"!"));
        
        //find the comment
        Comment c = cRepo.findByIdAndTask_Id(commentId, taskId).orElseThrow(() ->
        		new ResourceNotFoundException("Comment "+commentId+" does not exist in task "+taskId+"!"));
        
        //only the comment maker can edit the comment
        if(currentUser.getId() != c.getUser().getId())
        	throw new InvalidActionException("Comment "+commentId+" was not made by user ("+currentUser.getEmail()+")");
        
        //edit comment
        c.setMessage(dto.getMessage());
    }


    public void deleteOneComment(
            Long wrkspcId,
            Long projId,
            Long taskId,
            Long commentId){
    	
    	checkIfProjectMemberOrOwner(wrkspcId, projId);

    	User currentUser = uService.getLoggedInUser();
        
        //find the task
        tRepo.findByIdAndProject_Id(taskId, projId).orElseThrow(() -> 
 				new ResourceNotFoundException("Task "+taskId+" does not exist in project "+projId+"!"));
        
        //find the comment
        Comment c = cRepo.findByIdAndTask_Id(commentId, taskId).orElseThrow(() ->
        		new ResourceNotFoundException("Comment "+commentId+" does not exist in task "+taskId+"!"));
        
        //only the comment maker can edit the comment
        if(currentUser.getId() != c.getUser().getId())
        	throw new InvalidActionException("Comment "+commentId+" was not made by user ("+currentUser.getEmail()+")");

        cRepo.delete(c);
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