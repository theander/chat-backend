package com.cursochat.cursochat.handler;

import com.cursochat.cursochat.services.TicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component

public class WebSocketHandler extends TextWebSocketHandler {

    private final TicketService ticketService;
    private final Map<String, WebSocketSession> sessions;

    public WebSocketHandler(TicketService ticketService) {
        this.ticketService = ticketService;
        sessions = new ConcurrentHashMap<>(); //concurrentHashMap porque vai ser tratado em diferentes threads

    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        log.info("[ afterConnectionEstablished ] session id" + session.getId());

        final var ticket =this.ticketOf(session);
        if(ticket.isEmpty() || ticket.get().isBlank()){
            log.warn("session"+ session.getId()+"without ticket");
            close(session);
            return;
        }
        final var userId =ticketService.getUserById(ticket.get());
        if (userId.isEmpty()){
            log.warn("session "+ session.getId() + " with invalid ticket");
            close(session);
        }
sessions.put(userId.get(),session);

        log.info("session"+session.getId()+"was bind to user"+ userId.get());

    }

    private static void close(WebSocketSession session) throws IOException {

        session.close(CloseStatus.POLICY_VIOLATION);
    }

    private Optional<String> ticketOf(WebSocketSession webSocketSession){
        return Optional.ofNullable(webSocketSession.getUri())
                .map(UriComponentsBuilder::fromUri)
                .map(UriComponentsBuilder::build)
                .map(UriComponents::getQueryParams)
                .map(it->it.get("ticket"))
                .flatMap(it->it.stream().findFirst())
                .map(String::trim);
}

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("handleTextMessage" + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("afterConnectionClosed:" + session.getId());
    }
}
