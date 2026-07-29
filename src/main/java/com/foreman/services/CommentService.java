package com.foreman.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.foreman.dtos.CommentCreAndUpDto;
import com.foreman.dtos.CommentDisplayResponseDto;
import com.foreman.entities.Comment;
import com.foreman.entities.Task;
import com.foreman.entities.User;
import com.foreman.exception.InvalidActionException;
import com.foreman.exception.ResourceNotFoundException;
import com.foreman.repos.CommentRepo;
import com.foreman.repos.TaskRepo;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@Validated
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepo cRepo;
    private final UserService uService;
    private final TaskRepo tRepo;
    private final AutherizationService azService;
    


    public List<CommentDisplayResponseDto> getAllCommentsInTask(Long wrkspcId, Long projId, Long taskId){

    	azService.checkIfProjectMemberOrOwner(wrkspcId, projId);
        
        //check if the task belongs to the project
        if(tRepo.existsByIdAndProject_Id(taskId, projId) == false)
        	throw new ResourceNotFoundException("Task "+taskId+" does not  in project "+projId+"!");

        return cRepo.getAllCommentsInTask(taskId);
    }


    public String addOneComment(Long wrkspcId, Long projId, Long taskId, CommentCreAndUpDto dto){

    	azService.checkIfProjectMemberOrOwner(wrkspcId, projId);
       
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
    	
    	azService.checkIfProjectMemberOrOwner(wrkspcId, projId);

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
    	
    	azService.checkIfProjectMemberOrOwner(wrkspcId, projId);

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
}