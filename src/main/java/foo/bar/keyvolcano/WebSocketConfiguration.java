package foo.bar.keyvolcano;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration {

    @Bean
    public WebSocketConfigurer registerWebSocketHandlers(TrayWebSocketUI trayWebSocketUI) {
        return registry -> {
            registry.addHandler(new ContemplatingWsHandler(trayWebSocketUI), "/repeat").setAllowedOrigins("*");
        };
    }
}
