package com.kuro.kuroline_chat_ms.repository;

import com.kuro.kuroline_chat_ms.data.Message;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link Message} entities.
 */
@Repository
public interface MessageRepository{

    /**
     * Finds a message by its unique identifier.
     *
     * @param messageId the unique identifier of the message
     * @return the message with the given identifier, or null if not found
     */
    Message findById(String messageId);

    /**
     * Finds all messages in a discussion, ordered by their timestamp in ascending
     * order.
     *
     * @param discussionId the unique identifier of the discussion
     * @return a list of messages in the discussion, ordered by timestamp
     */
    List<Message> findByDiscussionIdOrderByTimestampAsc(String discussionId);

    /**
     * Saves a new message to the repository.
     *
     * @param message the message to save
     */
    void save(Message message);

    /**
     * Updates an existing message in the repository.
     *
     * @param message the message to update
     */
    void update(Message message);

    /**
     * Deletes a message from the repository.
     *
     * @param message the message to delete
     */
    void delete(Message message);
}