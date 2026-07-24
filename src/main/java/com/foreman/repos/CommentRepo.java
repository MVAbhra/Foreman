package com.foreman.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.foreman.dtos.CommentDisplayResponseDto;
import com.foreman.entities.Comment;

@Repository
public interface CommentRepo extends JpaRepository<Comment, Long> {

    @Query("""
        SELECT new com.foreman.dtos.CommentDisplayResponseDto(
            c.id,
            c.message,
            c.createdOn,
            c.task.id,
            c.user.id,
            c.task.project.id,
            c.task.project.workspace.id
        )
        FROM Comment c
        WHERE c.task.id = :taskId
        ORDER BY c.createdOn ASC
    """)
    List<CommentDisplayResponseDto> getAllCommentsInTask(Long taskId);


    Optional<Comment> findByIdAndTask_Id(Long commentId, Long taskId);

}
