package com.thekirschners.wiki.analyzer.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.thekirschners.wiki.analyzer.connector.adapter.keywords.StringCount;
import com.thekirschners.wiki.analyzer.domain.JsonSerialized;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@JsonTypeName("top-words-metric")
public class TopWordsMetric implements JsonSerialized{
	@JsonProperty("domain")
	private final String domain;
	@JsonProperty("timestamp")
	private final Instant timestamp;
	@JsonProperty("words")
	private final List<StringCount> list;

	public TopWordsMetric(String domain, Instant timestamp, List<StringCount> list) {
		this.domain = domain;
		this.timestamp = timestamp;
		this.list = list;
	}

	public List<StringCount> getList() {
		return list;
	}

	public String toJson() {
		StringBuilder sb = new StringBuilder();

		sb.append("{\"@msg-type\": \"top-words-metric\", \"timestamp\":" + timestamp.toEpochMilli() + ", \"domain\": \"" + domain + "\", \"words\":[");
		sb.append(list.stream().map(StringCount::toJson).collect(Collectors.joining(",")));
		sb.append("]}");
		return sb.toString();
	}
}
