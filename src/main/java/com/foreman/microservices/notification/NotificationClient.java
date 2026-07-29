package com.foreman.microservices.notification;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class NotificationClient {
	
	private final RestClient restClient;

    public NotificationClient(RestClient.Builder builder) {
        
    	this.restClient = builder
                .baseUrl("http://localhost:5092")
                .build();
    }
    
    
    public void sendNotification(
    		String title, 
    		String message,
    		Long receiverId,
    		String receiverEmail) {
    	
    	NotificationCreDto dto = new NotificationCreDto(title, 
    			message, false, LocalDateTime.now(), 
    			receiverId, receiverEmail);
   
    	restClient.post()
        .uri("/api/notifications")
        .body(dto)
        .retrieve()
        .toBodilessEntity();
    }
}
