package com.thekirschners.wiki.analyzer.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("update-event")
public class UpdateEvent {
	@JsonProperty("type")
	final String type;
	@JsonProperty("payload")
	final Object payload;

	public UpdateEvent(String type, Object payload) {
		this.type = type;
		this.payload = payload;
	}

	public String getType() {
		return type;
	}

	public Object getPayload() {
		return payload;
	}
}
