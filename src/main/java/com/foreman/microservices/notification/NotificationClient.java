package com.foreman.microservices.notification;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class NotificationClient {
	
	private final RestClient restClient;

	 public NotificationClient(
			RestClient.Builder builder,
			@Value("${notification.service.url}") String notifUrl) {
        
	    	this.restClient = builder
	                .baseUrl(notifUrl)
	                .build();
	
			System.out.println(notifUrl);
    }
    
    
    public void sendNotification(
    		String title, 
    		String message,
    		Long receiverId,
    		String receiverEmail) {
    	
    	NotificationCreDto dto = new NotificationCreDto(title, 
    			message, false, LocalDateTime.now(), 
    			receiverId, receiverEmail);

		System.out.println("Created mail.");
   
    	restClient.post()
        .uri("/api/notifications")
        .body(dto)
        .retrieve()
        .toBodilessEntity();

		System.out.println("Passed mail to microservice.");
    }
}
