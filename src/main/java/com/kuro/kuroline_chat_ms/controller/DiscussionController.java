package com.kuro.kuroline_chat_ms.controller;

import com.kuro.kuroline_chat_ms.data.Discussion;
import com.kuro.kuroline_chat_ms.data.Message;
import com.kuro.kuroline_chat_ms.data.ResponseMessage;
import com.kuro.kuroline_chat_ms.data.User;
import com.kuro.kuroline_chat_ms.service.DiscussionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/discussions")
public class DiscussionController {

    private final DiscussionService discussionService;

    public DiscussionController(DiscussionService discussionService) {
        this.discussionService = discussionService;
    }

    /**
     * Starts a new discussion.
     *
     * @param discussion the discussion to start
     * @param user       the authenticated user
     * @return the created discussion or an error message
     */
    @PostMapping("")
    public ResponseEntity<Object> startDiscussion(
            @RequestBody Discussion discussion,
            @AuthenticationPrincipal User user) {
        if (!isValidDiscussion(discussion)) {
            return new ResponseEntity<>(new ResponseMessage("Please provide the contact id"), HttpStatus.BAD_REQUEST);
        }

        setDefaultValues(discussion, user);

        try {
            discussionService.add(discussion);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error starting discussion", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(discussion, HttpStatus.CREATED);
    }

    /**
     * Retrieves a discussion by contact ID.
     *
     * @param contactId the contact ID
     * @param user      the authenticated user
     * @return the found discussion or an error message
     */
    @GetMapping("/contacts/{id}")
    public ResponseEntity<Object> getDiscussionByContactId(
            @PathVariable("id") String contactId,
            @AuthenticationPrincipal User user) {
        try {
            Discussion discussion = findDiscussionByContactId(user.getId(), contactId);
            if (discussion == null) {
                discussion = findDiscussionByContactId(contactId, user.getId());
            }
            return new ResponseEntity<>(discussion, HttpStatus.OK);
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error retrieving discussion by contact ID", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates a message in a discussion.
     *
     * @param discussionId the discussion ID
     * @param message      the message to update
     * @return the updated message or an error message
     */
    @PatchMapping("/{id}/messages")
    public ResponseEntity<Object> updateMessage(
            @PathVariable("id") String discussionId,
            @RequestBody Message message) {
        try {
            Optional<Discussion> discussionOptional = discussionService.findById(discussionId);
            if (discussionOptional.isPresent()) {
                updateDiscussionMessage(discussionOptional.get(), message);
                return new ResponseEntity<>(message, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ResponseMessage("Discussion not found"), HttpStatus.NOT_FOUND);
            }
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error updating message", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isValidDiscussion(Discussion discussion) {
        return discussion.getContactId() != null && !discussion.getContactId().isBlank();
    }

    private void setDefaultValues(Discussion discussion, User user) {
        discussion.setOwnerId(user.getId());
        discussion.setAttachments(null);
        discussion.setMessages(null);
        discussion.setLastMessageId(null);
        discussion.setLastMessageSentBy(null);
        discussion.setLastMessageSentAt(null);
    }

    private Discussion findDiscussionByContactId(String userId, String contactId)
            throws ExecutionException, InterruptedException {
        return discussionService.findByContactId(userId, contactId);
    }

    private void updateDiscussionMessage(Discussion discussion, Message message)
            throws ExecutionException, InterruptedException {
        discussionService.updateMessage(discussion, message);
    }
}