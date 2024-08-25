package com.kuro.kuroline_chat_ms.repository;

import com.kuro.kuroline_chat_ms.data.Message;

import java.util.List;

public interface MessageRepository {
    Message findById(String messageId);
    List<Message> findByDiscussionIdOrderByTimestampAsc(String discussionId);
    Message add(Message message);
    void update(Message message);
    void delete(Message message);

}
