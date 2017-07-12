package com.thekirschners.wiki.analyzer.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

	private MyTextWebSocketHandler webSocketHandler;


	@Autowired
	public WebSocketConfiguration setWebSocketHandler(MyTextWebSocketHandler webSocketHandler) {
		this.webSocketHandler = webSocketHandler;
		return this;
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(webSocketHandler, "/api/wikimetrics").setAllowedOrigins("*").setHandshakeHandler(new DefaultHandshakeHandler());
	}
}
