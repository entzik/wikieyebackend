package com.thekirschners.wiki.analyzer.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.thekirschners.wiki.analyzer.domain.JsonSerialized;

import java.util.Formatter;

import static java.lang.StrictMath.round;

/**
 * Created by emilkirschner on 03/11/15.
 */
@JsonTypeName("update-event")
public class WikiMetric implements JsonSerialized {
    @JsonProperty("timestamp")
    private final long timestamp;
    @JsonProperty("domain")
    private final String domain;
    @JsonProperty("action")
    private final String action;
    @JsonProperty("sec-rate")
    private final double secRate;

    public WikiMetric(long timestamp, String domain, String action, double secRate) {
        this.timestamp = timestamp;
        this.domain = domain;
        this.action = action;
        this.secRate = secRate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getDomain() {
        return domain;
    }

    public String getAction() {
        return action;
    }

    public double getSecRate() {
        return secRate;
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        Formatter fmt = new Formatter(sb);
        fmt.format("{\"@msg-type\": \"wiki-metric\", \"timestamp\": %d, \"domain\": \"%s\", \"action\": \"%s\", \"sec-rate\": %f}", timestamp, domain, action, (double)round(secRate));
        return sb.toString();
    }
}
