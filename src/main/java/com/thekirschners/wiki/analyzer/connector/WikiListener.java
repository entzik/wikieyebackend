package com.thekirschners.wiki.analyzer.connector;

import com.thekirschners.wiki.analyzer.Config;
import org.pircbotx.*;
import org.pircbotx.cap.TLSCapHandler;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.core.GenericMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.zalando.stups.boot.eventbus.EventBusSupport;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.concurrent.Executors;

@Service
public class WikiListener extends ListenerAdapter {
	static Logger LOGGER = LoggerFactory.getLogger(WikiListener.class);

	private final EventBusSupport eventBusSupport;

	private GenericMessagingTemplate messagingTemplate;

	private NumberFormat integerInstance;

	public WikiListener(EventBusSupport eventBusSupport) throws IOException {
		this.eventBusSupport = eventBusSupport;
		integerInstance = NumberFormat.getIntegerInstance();
		integerInstance.setMinimumIntegerDigits(6);

		messagingTemplate = new GenericMessagingTemplate();

		Executors.newSingleThreadExecutor().submit((Runnable) () -> {
			try {
				setupThisBot();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	@Async
	private void setupThisBot() throws IOException {
		final Configuration.Builder builder = new Configuration.Builder()
				.setName("wikimon") //Set the nick of the bot. CHANGE IN YOUR CODE
				.setLogin("wikimon") //login part of hostmask, eg name:login@host
				.setAutoNickChange(true) //Automatically change nick when the current one is in use
				.setCapEnabled(true) //Enable CAP features
				.addCapHandler(new TLSCapHandler(new UtilSSLSocketFactory().trustAllCertificates(), true))
				.addListener(new ListenerAdapter() {
					@Override
					public void onMessage(MessageEvent event) throws Exception {
						Channel channel = event.getChannel();
						String message = Colors.removeFormattingAndColors(event.getMessage());

						String lang = channel.getName().substring(1, 3);
						final WikiEvent wikiEvent = MessageParser.parseMessage(System.currentTimeMillis(), lang, message);

						if (wikiEvent != null) {
							eventBusSupport.postAsync(wikiEvent);
						}
					}
				});
		builder.setServerHostname("irc.wikimedia.org");
		for (String lang : Config.WIKI_DOMAINS)
			builder.addAutoJoinChannel("#" + lang + ".wikipedia");
		Configuration configuration = builder
				.buildConfiguration();
		try {
			PircBotX bot = new PircBotX(configuration);
			bot.startBot();
		} catch (IrcException e) {

		}
	}
}

