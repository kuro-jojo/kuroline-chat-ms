package com.kuro.kuroline_chat_ms.controller;

import com.kuro.kuroline_chat_ms.data.Discussion;
import com.kuro.kuroline_chat_ms.data.Message;
import com.kuro.kuroline_chat_ms.data.User;
import com.kuro.kuroline_chat_ms.service.DiscussionService;
import com.kuro.kuroline_chat_ms.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Slf4j
@Controller
public class ChatController {

    private final MessageService messageService;
    private final DiscussionService discussionService;

    public ChatController(MessageService messageService, DiscussionService discussionService) {
        this.messageService = messageService;
        this.discussionService = discussionService;
    }

    /**
     * Handles sending a message.
     *
     * @param chatMessage the message to be sent
     * @param principal   the principal of the authenticated user
     * @return the sent message
     */
    @MessageMapping("/send")
    @SendTo("/topic/message")
    public Message sendMessage(@Payload Message chatMessage, Principal principal) {
        User user = getAuthenticatedUser(principal);
        chatMessage.setSenderId(user.getId());
        validateMessage(chatMessage, user);

        log.info("User {} sent message to user {}", user.getId(), chatMessage.getReceiverId());

        try {
            if (chatMessage.getDiscussionId() == null) {
                throw new IllegalArgumentException("The discussion id is mandatory");
            }
            messageService.save(chatMessage);
            Optional<Discussion> discussionOptional = discussionService.findById(chatMessage.getDiscussionId());
            discussionOptional.ifPresent(discussion -> {
                if (!Arrays.asList(discussion.getParticipants()).contains(user.getId())) {
                    throw new AuthorizationDeniedException("User is not a participant of the discussion", () -> false);
                }
                try {
                    discussionService.addMessage(discussion, chatMessage);
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return chatMessage;
    }

    /**
     * Handles updating a message.
     *
     * @param chatMessage the message to be updated
     * @param principal   the principal of the authenticated user
     * @return the updated message
     */
    @MessageMapping("/update")
    @SendTo("/topic/message")
    public Message updateMessage(@Payload Message chatMessage, Principal principal) {
        try {
            if (chatMessage.getDiscussionId() == null) {
                throw new IllegalArgumentException("The discussion id is mandatory");
            }
            Optional<Discussion> discussionOptional = discussionService.findById(chatMessage.getDiscussionId());
            discussionOptional.ifPresent(discussion -> {
                if (discussion.getMessages().stream()
                        .noneMatch(message -> message.getId().equals(chatMessage.getId()))) {
                    throw new AuthorizationDeniedException("User doesn't have access to the message", () -> false);
                }
                try {
                    discussionService.updateMessage(discussion, chatMessage);
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.info("Updated message \"{}\"", chatMessage.getId());
            });
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        chatMessage.setDiscussionId(null);
        return chatMessage;
    }

    /**
     * Handles updating multiple messages.
     *
     * @param messages  the list of messages to be updated
     * @param principal the principal of the authenticated user
     * @return the list of updated messages
     */
    @MessageMapping("/update-messages")
    @SendTo("/topic/messages")
    public List<Message> updateMessages(@Payload ArrayList<Message> messages, Principal principal) {
        if (messages.isEmpty()) {
            return null;
        }

        try {
            String discussionId = messages.get(0).getDiscussionId();

            if (discussionId == null || discussionId.isBlank()) {
                throw new IllegalArgumentException("The discussion id is mandatory");
            }
            Optional<Discussion> discussionOptional = discussionService.findById(discussionId);
            discussionOptional.ifPresent(disc -> {
                if (!messages.stream().allMatch(
                        message -> disc.getMessages().stream().anyMatch(msg -> msg.getId().equals(message.getId())))) {
                    throw new AuthorizationDeniedException("User doesn't have access to the message", () -> false);
                }

                try {
                    discussionService.updateMessages(disc, messages);
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                messages.forEach(message -> log.info("Updated message \"{}\"", message.getId()));
            });
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return messages;
    }

    private User getAuthenticatedUser(Principal principal) {
        if (principal instanceof Authentication authentication) {
            return (User) authentication.getPrincipal();
        } else {
            throw new AccessDeniedException("User is not authenticated");
        }
    }

    private void validateMessage(Message chatMessage, User user) {
        if (chatMessage.getReceiverId().isBlank()) {
            log.warn("User \"{}\": Receiver must be provided", user.getId());
            throw new IllegalArgumentException("Receiver must be provided");
        }
        if (chatMessage.getContent().isBlank()) {
            log.warn("User \"{}\": Content cannot be blank", user.getId());
            throw new IllegalArgumentException("Content cannot be blank");
        }
    }
}