package es.ucm.fdi.iw.turbochess.Configurations;

import es.ucm.fdi.iw.turbochess.model.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;



/**
 * Similar a SecurityConfig, pero para websockets con STOMP.
 *
 */
@Configuration
public class WebSocketSecurityConfig
        extends AbstractSecurityWebSocketMessageBrokerConfigurer {


    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .simpSubscribeDestMatchers("/topic/admin")	// solo admines pueden suscribirse a este topic
                .hasRole(User.Role.ADMIN.toString())
                .anyMessage().authenticated(); 				// todo tiene que proceder de sesiones autenticadas
    }
}
