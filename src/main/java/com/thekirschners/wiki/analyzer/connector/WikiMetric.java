package com.thekirschners.wiki.analyzer.connector;


import java.util.Formatter;

import static java.lang.StrictMath.round;

/**
 * Created by emilkirschner on 03/11/15.
 */
public class WikiMetric {
    private final long timestamp;
    private final String domain;
    private final String action;
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

    public String toJSon() {
        StringBuilder sb = new StringBuilder();
        Formatter fmt = new Formatter(sb);
        fmt.format("{\"@msg-type\": \"wiki-metric\", \"timestamp\": %d, \"domain\": \"%s\", \"action\": \"%s\", \"sec-rate\": %f}", timestamp, domain, action, (double)round(secRate));
        return sb.toString();
    }
}
