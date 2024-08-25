package com.kuro.kuroline_chat_ms.service;

import com.kuro.kuroline_chat_ms.data.Message;
import com.kuro.kuroline_chat_ms.data.MessageStatus;
import com.kuro.kuroline_chat_ms.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MessageService implements MessageRepository {
    @Override
    public Message findById(String messageId) {
        return null;
    }

    @Override
    public List<Message> findByDiscussionIdOrderByTimestampAsc(String discussionId) {
        return null;
    }

    @Override
    public Message add(Message message) {
        message.setSendAt(new Date());
        message.setMessageStatus(MessageStatus.SENT);

        return message;
    }

    @Override
    public void update(Message message) {

    }

    @Override
    public void delete(Message message) {

    }
}
