package com.kuro.kuroline_chat_ms.controller;

import com.kuro.kuroline_chat_ms.data.Discussion;
import com.kuro.kuroline_chat_ms.data.ResponseMessage;
import com.kuro.kuroline_chat_ms.data.User;
import com.kuro.kuroline_chat_ms.service.DiscussionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(path = "/api/v1/discussions")
public class DiscussionController {

    private final DiscussionService discussionService;

    public DiscussionController(DiscussionService discussionService) {
        this.discussionService = discussionService;
    }

    @PostMapping("")
    public ResponseEntity<Object> startDiscussion(
            @RequestBody Discussion discussion,
            @AuthenticationPrincipal User user
    ) {
        if (discussion.getContactId().isBlank()) {
            return new ResponseEntity<>(new ResponseMessage("Please provide the contact id"), HttpStatus.BAD_REQUEST);
        }

        discussion.setOwnerId(user.getId());
        discussion.setAttachments(null);
        discussion.setMessages(null);
        discussion.setLastMessageId(null);
        discussion.setLastMessageSentBy(null);
        discussion.setLastMessageSentAt(null);

        try {
            discussionService.add(discussion);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(discussion, HttpStatus.CREATED);
    }

    @GetMapping("/contacts/{id}")
    public ResponseEntity<Object> getDiscussionByContacts(
            @PathVariable("id") String contactId,
            @AuthenticationPrincipal User user
    ) {
        Discussion discussion;
        try {
            discussion = discussionService.findByContacts(user.getId(), contactId);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(discussion, HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getDiscussions(
            @AuthenticationPrincipal User user
    ) {
        List<Discussion> discussions;
        try {
            discussions = discussionService.findAllByOwner(user.getId());
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(discussions, HttpStatus.OK);
    }

    @GetMapping("/owner/contacts")
    public ResponseEntity<Object> getContactsFromDiscussions(
            @AuthenticationPrincipal User user
    ) {
        List<Discussion> discussions;
        try {
            discussions = discussionService.findAllByOwner(user.getId());
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<String> contacts = new ArrayList<>();
        for (Discussion d : discussions) {
            contacts.add(d.getContactId());
        }
        return new ResponseEntity<>(contacts, HttpStatus.OK);
    }
}
