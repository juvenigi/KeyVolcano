package foo.bar.keyvolcano;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

public class ContemplatingWsHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ContemplatingWsHandler.class);
    private final TrayWebSocketUI trayWebSocketUI;

    public ContemplatingWsHandler(TrayWebSocketUI trayWebSocketUI) {
        this.trayWebSocketUI = trayWebSocketUI;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {

        IntStream.rangeClosed(0, 9).forEach(i -> {
            try {
                session.sendMessage(new TextMessage(i + 1 + "\n"));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });

    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, TextMessage message) throws Exception {
        CompletableFuture<Boolean> booleanCompletableFuture = trayWebSocketUI.promptIncomingConnection(message.getPayload());

        if (booleanCompletableFuture.join()) {
            session.sendMessage(new TextMessage("success\n"));
        } else {
            try (session) {
                session.sendMessage(new TextMessage("rejection\n"));
            }
        }
        log.info("handled");
    }
}
