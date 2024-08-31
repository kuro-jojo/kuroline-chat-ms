package com.kuro.kuroline_chat_ms.repository;

import com.kuro.kuroline_chat_ms.data.Discussion;
import com.kuro.kuroline_chat_ms.data.Message;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Repository interface for managing Discussion entities.
 */
public interface DiscussionRepository {

    /**
     * Finds a discussion by its ID.
     *
     * @param discussionId the ID of the discussion
     * @return an Optional containing the found Discussion, or empty if not found
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while
     *                              waiting
     */
    Optional<Discussion> findById(String discussionId) throws ExecutionException, InterruptedException;

    /**
     * Adds a new discussion.
     *
     * @param discussion the discussion to add
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while
     *                              waiting
     */
    void add(Discussion discussion) throws ExecutionException, InterruptedException;

    /**
     * Updates an existing discussion by its ID.
     *
     * @param discussion the discussion to update
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while
     *                              waiting
     */
    void updateById(Discussion discussion) throws ExecutionException, InterruptedException;

    /**
     * Updates a specific message within a discussion.
     *
     * @param discussion the discussion containing the message
     * @param message    the message to update
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while
     *                              waiting
     */
    void updateMessage(Discussion discussion, Message message) throws ExecutionException, InterruptedException;

    /**
     * Updates multiple messages within a discussion.
     *
     * @param discussion the discussion containing the messages
     * @param messages   the list of messages to update
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while
     *                              waiting
     */
    void updateMessages(Discussion discussion, List<Message> messages) throws ExecutionException, InterruptedException;

    /**
     * Adds a new message to a discussion.
     *
     * @param discussion the discussion to add the message to
     * @param message    the message to add
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while
     *                              waiting
     */
    void addMessage(Discussion discussion, Message message) throws ExecutionException, InterruptedException;

    /**
     * Deletes a discussion.
     *
     * @param discussion the discussion to delete
     */
    void delete(Discussion discussion);

    /**
     * Finds a discussion by the owner's ID and contact's ID.
     *
     * @param ownerId   the ID of the owner
     * @param contactId the ID of the contact
     * @return the found Discussion
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while
     *                              waiting
     */
    Discussion findByContactId(String ownerId, String contactId) throws ExecutionException, InterruptedException;
}