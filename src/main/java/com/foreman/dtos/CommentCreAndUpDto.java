package com.foreman.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentCreAndUpDto {

    private String message;

    private Long userId;
}