package com.kuro.kuroline_chat_ms.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String id;
    private String senderId;
    private String receiverId;
    private String groupId;
    private String content;
    private Date sendAt;
    private MessageStatus messageStatus;
    private MessageType messageType;
}
