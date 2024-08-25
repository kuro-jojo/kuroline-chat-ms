package com.kuro.kuroline_chat_ms.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Discussion {
    private String id;
    private String ownerId;
    private String contactId; // the contact with whom the user is having a discussion;
    private List<Message> messages;
    private String lastMessageId;
    private Date lastMessageSendAt;
    private String lastMessageSentBy; // the user who sent the last message
    private List<Attachment> attachments;
}
