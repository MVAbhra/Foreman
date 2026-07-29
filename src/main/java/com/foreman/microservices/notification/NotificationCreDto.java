package com.foreman.microservices.notification;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class NotificationCreDto {

	private String Title;
	private String Message;
	private boolean IsRead;
	private LocalDateTime CreatedOn;
	private Long ReceiverId;
	private String ReceiverEmail;
}
