package com.kuro.kuroline_chat_ms.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.kuro.kuroline_chat_ms.data.Discussion;
import com.kuro.kuroline_chat_ms.data.Message;
import com.kuro.kuroline_chat_ms.repository.DiscussionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class DiscussionService implements DiscussionRepository {

    private final CollectionReference collection;

    public DiscussionService(Firestore firestore) {
        collection = firestore.collection("discussions");
    }

    @Override
    public Optional<Discussion> findById(String discussionId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = collection.document(discussionId);
        ApiFuture<DocumentSnapshot> future = docRef.get();

        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return Optional.ofNullable(document.toObject(Discussion.class));
        }
        return Optional.empty();
    }

    @Override
    public void add(Discussion discussion) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> addedDocRef = collection.add(discussion);
        discussion.setId(addedDocRef.get().getId());
        updateId(discussion);
    }

    @Override
    public void updateId(Discussion discussion) {
        updateField(discussion, "id", discussion.getId());
    }

    public void updateField(Discussion discussion, String field, Object value) {
        DocumentReference docRef = collection.document(discussion.getId());
        docRef.update(field, value);
    }

    @Override
    public void addMessage(Discussion discussion, Message message) {
        updateField(discussion,"messages", FieldValue.arrayUnion(message));
        updateField(discussion,"lastMessageId", message.getId());
        updateField(discussion,"lastMessageSentAt", message.getSentAt());
        updateField(discussion,"lastMessageSentBy", message.getSenderId());
    }

    @Override
    public void delete(Discussion discussion) {

    }

    @Override
    public Discussion findByContacts(String ownerId, String contactId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = collection
                .whereEqualTo("ownerId", ownerId)
                .whereEqualTo("contactId", contactId)
                .get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        if (documents.isEmpty()) {
            return null;
        }
        return documents.get(0).toObject(Discussion.class);
    }

    @Override
    public List<Discussion> findAllByOwner(String ownerId) throws ExecutionException, InterruptedException {
        return findAllBy("ownerId", ownerId);
    }

    private List<Discussion> findAllBy(String field, String value) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = collection.whereEqualTo(field, value).get();
        return future.get().toObjects(Discussion.class);
    }
}
