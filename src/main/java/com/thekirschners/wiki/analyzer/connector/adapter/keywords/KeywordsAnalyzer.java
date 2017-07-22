package com.thekirschners.wiki.analyzer.connector.adapter.keywords;

import com.google.common.eventbus.Subscribe;
import com.thekirschners.wiki.analyzer.Config;
import com.thekirschners.wiki.analyzer.connector.WikiEvent;
import org.pircbotx.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.stups.boot.eventbus.EventBusSupport;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class KeywordsAnalyzer extends ListenerAdapter {
	HashMap<String, SortedMap<Instant, String>> records;

	EventBusSupport eventBusSupport;

	@Autowired
	public KeywordsAnalyzer(EventBusSupport eventBusSupport) {
		this.eventBusSupport = eventBusSupport;
		this.records = new HashMap<>();

		for (String s : Config.WIKI_DOMAINS)
			records.put(s, new TreeMap<>());
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this::publishStats, 10, 10, TimeUnit.SECONDS);
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this::cleanup, 15, 20, TimeUnit.SECONDS);

	}

	@Subscribe
	public void onMessage(WikiEvent wikiEvent) throws Exception {
		try {
			Instant now = Instant.now();

			final String domain = wikiEvent.getDomain();
			final String action = wikiEvent.getAction();
			final String user = wikiEvent.getUser();
			final String topic = wikiEvent.getTopic();


			if ("update".equals(action)) {
				final String[] topics = sanitizeTopic(topic).split("\\s");
				synchronized (records) {
					for (String word : topics)
						records.get(domain).put(now, word);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String sanitizeTopic(String topic) {
		StringBuilder sb = new StringBuilder(topic.length() + 1);
		for (int i = 0; i < topic.length(); i ++) {
			char ch = topic.charAt(i);
			if (Character.isLetterOrDigit(ch))
				sb.append(ch);
			else if (!Character.isLetterOrDigit(topic.charAt(i))) {
				if (i == 0 || sb.charAt(sb.length() - 1) != ' ')
				sb.append(' ');
			}
		}
		return sb.toString();
	}

	private void publishStats() {
		synchronized (records) {
			Instant timestamp = Instant.now();
			try {
				for (String domain : Config.WIKI_DOMAINS) {
					final Map<String, StringCount> collect = records.get(domain).tailMap(Instant.now().minus(1, ChronoUnit.HOURS)).values().stream()
							.map(String::toLowerCase)
							.map(word -> new StringCount(word, 1))
							.collect(Collectors.toMap(StringCount::getWord, si -> si, (i, j) -> new StringCount(i.getWord(), i.getCount() + j.getCount())));
					final List<StringCount> topWords = collect.values().stream().sorted((o1, o2) -> o2.getCount() - o1.getCount()).limit(10).collect(Collectors.toList());
					final TopWordsMetric event = new TopWordsMetric(domain, timestamp, topWords);
					eventBusSupport.postAsync(event);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void cleanup() {
		synchronized (records) {
			for (String s : Config.WIKI_DOMAINS) {
				final Instant since = Instant.now().minus(1, ChronoUnit.HOURS);

				final HashSet<Instant> instantsToRemove = new HashSet<>(records.get(s).headMap(since).keySet());
				records.get(s).keySet().removeAll(instantsToRemove);
			}
		}
	}

}