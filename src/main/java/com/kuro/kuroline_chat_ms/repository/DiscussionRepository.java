package com.kuro.kuroline_chat_ms.repository;

import com.kuro.kuroline_chat_ms.data.Discussion;

import java.util.List;
import java.util.Optional;

public interface DiscussionRepository {
    Optional<Discussion> findById(String discussionId);
    void add(Discussion discussion);
    void update(Discussion discussion);
    void delete(Discussion discussion);
}
