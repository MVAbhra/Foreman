package com.foreman.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foreman.dtos.CommentCreAndUpDto;
import com.foreman.dtos.CommentDisplayResponseDto;
import com.foreman.services.CommentService;

@RestController
@RequestMapping("/api/workspaces/{wrkspcId}/projects/{projId}/tasks/{taskId}/comments")
public class CommentController {

    @Autowired
    private CommentService cService;


    @GetMapping
    public ResponseEntity<List<CommentDisplayResponseDto>> getAllCommentsInTask(
            @PathVariable Long wrkspcId,
            @PathVariable Long projId,
            @PathVariable Long taskId){

        return new ResponseEntity<>(
                cService.getAllCommentsInTask(wrkspcId, projId, taskId),
                HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<String> addOneComment(
            @PathVariable Long wrkspcId,
            @PathVariable Long projId,
            @PathVariable Long taskId,
            @RequestBody CommentCreAndUpDto dto){

        cService.addOneComment(wrkspcId, projId, taskId, dto);

        return new ResponseEntity<>(
                "Comment was added by user "+dto.getUserId()+" on task "+taskId+" in project "+projId+" successfully!",
                HttpStatus.CREATED);
    }


    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDisplayResponseDto> getOneCommentInTask(
            @PathVariable Long wrkspcId,
            @PathVariable Long projId,
            @PathVariable Long taskId,
            @PathVariable Long commentId){

        return new ResponseEntity<>(
                cService.getOneCommentInTask(wrkspcId, projId, taskId, commentId),
                HttpStatus.OK);
    }


    @PutMapping("/{commentId}")
    public ResponseEntity<String> updateOneComment(
            @PathVariable Long wrkspcId,
            @PathVariable Long projId,
            @PathVariable Long taskId,
            @PathVariable Long commentId,
            @RequestBody CommentCreAndUpDto dto){

        cService.updateOneComment(wrkspcId, projId, taskId, commentId, dto);

        return new ResponseEntity<>(
                "Comment " + commentId + " was updated!",
                HttpStatus.OK);
    }


    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteOneComment(
            @PathVariable Long wrkspcId,
            @PathVariable Long projId,
            @PathVariable Long taskId,
            @PathVariable Long commentId){

        cService.deleteOneComment(wrkspcId, projId, taskId, commentId);

        return new ResponseEntity<>(
                "Comment " + commentId + " was deleted!",
                HttpStatus.OK);
    }

}