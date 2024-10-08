package com.kuro.kuroline_chat_ms.config.ws;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.kuro.kuroline_chat_ms.data.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static java.util.Objects.isNull;

@Slf4j
public class AuthorizationSocketInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (isNull(authHeader) || !authHeader.startsWith("Bearer" + " ")) {
                log.error("Authentication token not provided");
                throw new AccessDeniedException(HttpStatus.UNAUTHORIZED.toString());
            }

            String token = authHeader.substring("Bearer".length() + 1);
            FirebaseToken decodedToken;
            try {
                decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            } catch (FirebaseAuthException e) {
                log.error(e.getMessage());
                throw new AccessDeniedException(HttpStatus.UNAUTHORIZED.toString());
            }

            User user = new User();
            user.setId(decodedToken.getUid());

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
            accessor.setUser(authentication);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        return message;
    }
}
