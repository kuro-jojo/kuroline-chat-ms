package com.kuro.kuroline_chat_ms.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.kuro.kuroline_chat_ms.data.Discussion;
import com.kuro.kuroline_chat_ms.data.Message;
import com.kuro.kuroline_chat_ms.repository.DiscussionRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Service class for managing discussions.
 */
@Service
public class DiscussionService implements DiscussionRepository {

    private final CollectionReference collection;

    /**
     * Constructor for DiscussionService.
     *
     * @param firestore Firestore instance.
     */
    public DiscussionService(Firestore firestore) {
        collection = firestore.collection("discussions");
    }

    /**
     * Finds a discussion by its ID.
     *
     * @param discussionId The ID of the discussion.
     * @return An Optional containing the discussion if found, otherwise empty.
     * @throws ExecutionException   If an error occurs during execution.
     * @throws InterruptedException If the operation is interrupted.
     */
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

    /**
     * Adds a new discussion.
     *
     * @param discussion The discussion to add.
     * @throws ExecutionException   If an error occurs during execution.
     * @throws InterruptedException If the operation is interrupted.
     */
    @Override
    public void add(Discussion discussion) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> addedDocRef = collection.add(discussion);
        discussion.setId(addedDocRef.get().getId());
        updateById(discussion);
    }

    /**
     * Updates a discussion by its ID.
     *
     * @param discussion The discussion to update.
     * @throws ExecutionException   If an error occurs during execution.
     * @throws InterruptedException If the operation is interrupted.
     */
    @Override
    public void updateById(Discussion discussion) throws ExecutionException, InterruptedException {
        updateField(discussion, "id", discussion.getId());
    }

    /**
     * Updates a message in the discussion.
     *
     * @param discussion The discussion containing the message.
     * @param message    The message to update.
     * @throws ExecutionException   If an error occurs during execution.
     * @throws InterruptedException If the operation is interrupted.
     */
    @Override
    public void updateMessage(Discussion discussion, Message message) throws ExecutionException, InterruptedException {
        updateMessages(discussion, List.of(message));
    }

    /**
     * Updates multiple messages in the discussion.
     *
     * @param discussion The discussion containing the messages.
     * @param messages   The messages to update.
     * @throws ExecutionException   If an error occurs during execution.
     * @throws InterruptedException If the operation is interrupted.
     */
    @Override
    public void updateMessages(Discussion discussion, List<Message> messages)
            throws ExecutionException, InterruptedException {
        for (Message message : messages) {
            for (int i = 0; i < discussion.getMessages().size(); i++) {
                if (discussion.getMessages().get(i).getId().equals(message.getId())) {
                    discussion.getMessages().set(i, message);
                    break;
                }
            }
        }
        updateField(discussion, "messages", discussion.getMessages());
    }

    /**
     * Updates a field in the discussion.
     *
     * @param discussion      The discussion to update.
     * @param field           The field to update.
     * @param value           The value to set.
     * @param fieldsAndValues Additional fields and values to update.
     * @throws ExecutionException   If an error occurs during execution.
     * @throws InterruptedException If the operation is interrupted.
     */
    public void updateField(Discussion discussion, String field, Object value, @Nullable Object... fieldsAndValues)
            throws ExecutionException, InterruptedException {
        DocumentReference docRef = collection.document(discussion.getId());
        ApiFuture<WriteResult> future = docRef.update(field, value, fieldsAndValues);
        future.get();
    }

    /**
     * Adds a message to the discussion.
     *
     * @param discussion The discussion to update.
     * @param message    The message to add.
     * @throws ExecutionException   If an error occurs during execution.
     * @throws InterruptedException If the operation is interrupted.
     */
    @Override
    public void addMessage(Discussion discussion, Message message) throws ExecutionException, InterruptedException {
        updateField(
                discussion, "messages", FieldValue.arrayUnion(message),
                "lastMessageId", message.getId(),
                "lastMessageSentAt", message.getSentAt(),
                "lastMessageSentBy", message.getSenderId());
    }

    /**
     * Deletes a discussion.
     *
     * @param discussion The discussion to delete.
     */
    @Override
    public void delete(Discussion discussion) {
        collection.document(discussion.getId()).delete();
    }

    /**
     * Finds a discussion by owner ID and contact ID.
     *
     * @param ownerId   The owner ID.
     * @param contactId The contact ID.
     * @return The discussion if found, otherwise null.
     * @throws ExecutionException   If an error occurs during execution.
     * @throws InterruptedException If the operation is interrupted.
     */
    @Override
    public Discussion findByContactId(String ownerId, String contactId)
            throws ExecutionException, InterruptedException {
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

    /**
     * Finds all discussions by a specific field and value.
     *
     * @param field The field to query.
     * @param value The value to query.
     * @return A list of discussions matching the query.
     * @throws ExecutionException   If an error occurs during execution.
     * @throws InterruptedException If the operation is interrupted.
     */
    private List<Discussion> findAllBy(String field, String value) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = collection.whereEqualTo(field, value).get();
        return future.get().toObjects(Discussion.class);
    }
}