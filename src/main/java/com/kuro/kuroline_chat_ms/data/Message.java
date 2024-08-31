package com.kuro.kuroline_chat_ms.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.cloud.firestore.annotation.Exclude;
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
    private String content;
    private String groupId;
    // This will ensure discussionId is only included during deserialization (request)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String discussionId;
    private Date sentAt;
    private MessageStatus status;
    private MessageType type;

    @Exclude
    public String getDiscussionId() {
        return discussionId;
    }
}
