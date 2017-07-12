package com.thekirschners.wiki.analyzer.connector.adapter.keywords;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;


public class TopWordsMetric {
	private final String domain;
	private final Instant timestamp;
	final List<StringCount> list;

	public TopWordsMetric(String domain, Instant timestamp, List<StringCount> list) {
		this.domain = domain;
		this.timestamp = timestamp;
		this.list = list;
	}

	public List<StringCount> getList() {
		return list;
	}

	public String toJSon() {
		StringBuilder sb = new StringBuilder();

		sb.append("{\"@msg-type\": \"top-words-metric\", \"timestamp\":" + timestamp.toEpochMilli() + ", \"domain\": \"" + domain + "\", \"words\":[");
		sb.append(list.stream().map(StringCount::toJson).collect(Collectors.joining(",")));
		sb.append("]}");
		return sb.toString();
	}
}
