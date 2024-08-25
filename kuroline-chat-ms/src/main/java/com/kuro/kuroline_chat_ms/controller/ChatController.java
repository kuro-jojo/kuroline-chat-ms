package com.kuro.kuroline_chat_ms.controller;

import com.kuro.kuroline_chat_ms.data.Message;
import com.kuro.kuroline_chat_ms.data.ResponseMessage;
import com.kuro.kuroline_chat_ms.data.User;
import com.kuro.kuroline_chat_ms.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @Autowired
    private final MessageService messageService;

    public ChatController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/send")
    @SendTo("/topic/public")
    public Message sendMessage(@Payload Message chatMessage, @AuthenticationPrincipal User user) {
        if (chatMessage.getReceiverId().isBlank()) {
            // Handle this situation, maybe log an error or send an error message to a dedicated topic
            throw new IllegalArgumentException("Receiver must be provided");
        }
        if (chatMessage.getContent().isBlank()) {
            throw new IllegalArgumentException("Content cannot be blank");
        }
        chatMessage.setSenderId(user.getId());
        Message m = messageService.add(chatMessage);
        return m; // Returning the message to be broadcast to "/topic/public"
    }

}
