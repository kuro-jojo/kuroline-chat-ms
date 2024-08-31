package com.kuro.kuroline_chat_ms.service;

import com.kuro.kuroline_chat_ms.data.Message;
import com.kuro.kuroline_chat_ms.data.MessageStatus;
import com.kuro.kuroline_chat_ms.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Service class for managing messages.
 */
@Service
public class MessageService implements MessageRepository {

    @Override
    public Message findById(String messageId) {
        return null;
    }

    /**
     * Finds messages by discussion ID, ordered by timestamp in ascending order.
     * 
     * @param discussionId the ID of the discussion
     * @return a list of messages in the discussion
     */
    public List<Message> findByDiscussionIdOrderByTimestampAsc(String discussionId) {
        return null;
    }

    /**
     * Saves a new message.
     * 
     * @param message the message to save
     */
    public void save(Message message) {
        message.setSentAt(new Date());
        message.setStatus(MessageStatus.SENT);
        message.setId(UUID.randomUUID().toString());
    }

    /**
     * Updates an existing message.
     * 
     * @param message the message to update
     */
    public void update(Message message) {
    }

    /**
     * Deletes a message.
     * 
     * @param message the message to delete
     */
    public void delete(Message message) {
    }

}