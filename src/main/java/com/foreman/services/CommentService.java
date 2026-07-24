package com.foreman.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foreman.dtos.CommentCreAndUpDto;
import com.foreman.dtos.CommentDisplayResponseDto;
import com.foreman.entities.Comment;
import com.foreman.entities.Task;
import com.foreman.entities.User;
import com.foreman.exception.ResourceNotFoundException;
import com.foreman.repos.CommentRepo;

@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentRepo cRepo;

    @Autowired
    private TaskService tService;

    @Autowired
    private ProjectMembershipService pmService;


    public List<CommentDisplayResponseDto> getAllCommentsInTask(
            Long wrkspcId,
            Long projId,
            Long taskId){

        tService.checkTaskExistenceInProject(taskId, projId);

        return cRepo.getAllCommentsInTask(taskId);
    }


    public void addOneComment(
            Long wrkspcId,
            Long projId,
            Long taskId,
            CommentCreAndUpDto dto){

        Task task = tService.checkTaskExistenceInProject(taskId, projId);

        User user = pmService.checkUserExistenceInProject(
                wrkspcId,
                projId,
                dto.getUserId());

        Comment c = new Comment(
                dto.getMessage(),
                task,
                user);

        cRepo.save(c);
    }


    public CommentDisplayResponseDto getOneCommentInTask(
            Long wrkspcId,
            Long projId,
            Long taskId,
            Long commentId){

        tService.checkTaskExistenceInProject(taskId, projId);

        Comment c = checkCommentExistenceInTask(commentId, taskId);

        return new CommentDisplayResponseDto(
                c.getId(),
                c.getMessage(),
                c.getCreatedOn(),
                taskId,
                c.getUser().getId(),
                projId,
                wrkspcId);
    }


    public void updateOneComment(
            Long wrkspcId,
            Long projId,
            Long taskId,
            Long commentId,
            CommentCreAndUpDto dto){

        tService.checkTaskExistenceInProject(taskId, projId);

        User user = pmService.checkUserExistenceInProject(
                wrkspcId,
                projId,
                dto.getUserId());

        Comment c = checkCommentExistenceInTask(commentId, taskId);

        c.setMessage(dto.getMessage());
        c.setUser(user);
    }


    public void deleteOneComment(
            Long wrkspcId,
            Long projId,
            Long taskId,
            Long commentId){

        tService.checkTaskExistenceInProject(taskId, projId);

        Comment c = checkCommentExistenceInTask(commentId, taskId);

        cRepo.delete(c);
    }


    //-------------------------------- Utility methods --------------------------------

    public Comment checkCommentExistenceInTask(
            Long commentId,
            Long taskId){

        return cRepo.findByIdAndTask_Id(commentId, taskId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Comment " + commentId +
                                " does not exist in task " + taskId + "!"));
    }

}