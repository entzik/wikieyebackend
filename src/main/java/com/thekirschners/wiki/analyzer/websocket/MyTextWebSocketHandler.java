package com.thekirschners.wiki.analyzer.websocket;

import com.google.common.eventbus.Subscribe;
import com.thekirschners.wiki.analyzer.connector.WikiMetric;
import com.thekirschners.wiki.analyzer.connector.adapter.keywords.TopWordsMetric;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
class MyTextWebSocketHandler extends TextWebSocketHandler {
	private ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

	@Subscribe
	public void handleWikiMetric(WikiMetric wikiMetric) {
		String payload = wikiMetric.toJSon();
		broadcast(payload);
	}

	@Subscribe
	public void handleTopWordMetric(TopWordsMetric topWordsMetric) {
		String payload = topWordsMetric.toJSon();
		broadcast(payload);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		super.handleTextMessage(session, message);
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessions.put(session.getId(), session);
		super.afterConnectionEstablished(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session.getId());
		super.afterConnectionClosed(session, status);
	}

	private void broadcast(String msg) {
		sessions.values().forEach(
				session -> {
					try {
						if (session.isOpen())
							session.sendMessage(new TextMessage(msg));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
		);
	}
}
