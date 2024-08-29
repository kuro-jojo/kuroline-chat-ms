package com.kuro.kuroline_chat_ms.repository;

import com.kuro.kuroline_chat_ms.data.Discussion;
import com.kuro.kuroline_chat_ms.data.Message;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public interface DiscussionRepository {
    Optional<Discussion> findById(String discussionId) throws ExecutionException, InterruptedException;

    void add(Discussion discussion) throws ExecutionException, InterruptedException;
    void updateId(Discussion discussion);
    void addMessage(Discussion discussion, Message message);
    void delete(Discussion discussion);
    Discussion findByContacts(String ownerId, String contactId) throws ExecutionException, InterruptedException;
    List<Discussion> findAllByOwner(String ownerId) throws ExecutionException, InterruptedException;
}
