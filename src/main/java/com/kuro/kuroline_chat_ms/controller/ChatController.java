package com.kuro.kuroline_chat_ms.controller;

import com.kuro.kuroline_chat_ms.data.Discussion;
import com.kuro.kuroline_chat_ms.data.Message;
import com.kuro.kuroline_chat_ms.data.User;
import com.kuro.kuroline_chat_ms.service.DiscussionService;
import com.kuro.kuroline_chat_ms.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutionException;

@Slf4j
@Controller
public class ChatController {

    @Autowired
    private final MessageService messageService;
    @Autowired
    private final DiscussionService discussionService;

    public ChatController(MessageService messageService, DiscussionService discussionService) {
        this.messageService = messageService;
        this.discussionService = discussionService;
    }

    @MessageMapping("/send")
    @SendTo("/topic/public")
    public Message sendMessage(@Payload Message chatMessage, Principal principal) {
        User user;
        if (principal instanceof Authentication authentication) {
            user = (User) authentication.getPrincipal(); // Cast to your custom User object
//            chatMessage.setSenderId(user.getId()); // Use the user ID or any other user details
        } else {
            // Handle the case where the user is not authenticated
            throw new AccessDeniedException("User is not authenticated");
        }
        if (chatMessage.getReceiverId().isBlank()) {
            // Handle this situation, maybe log an error or send an error message to a dedicated topic
            log.warn("User \" " + user.getId() + "\" : Receiver must be provided");
            throw new IllegalArgumentException("Receiver must be provided");
        }
        if (chatMessage.getContent().isBlank()) {
            log.warn("User \"+ user.getId()  + \" :  Content cannot be blank");
            throw new IllegalArgumentException("Content cannot be blank");
        }
        Random i = new Random();
        if (i.nextBoolean()){
        chatMessage.setSenderId(user.getId());
        }else{
            chatMessage.setSenderId(chatMessage.getReceiverId());
        }

        log.info("User " + user.getId() + " sent message to user " + chatMessage.getReceiverId());
        messageService.save(chatMessage);

        // Get the discussion
        try {
            Optional<Discussion> discussionOptional = discussionService.findById(chatMessage.getDiscussionId());
            discussionOptional.ifPresent(discussion -> discussionService.addMessage(discussion, chatMessage));
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return chatMessage; // Returning the message to be broadcast to "/topic/public"
    }

}
