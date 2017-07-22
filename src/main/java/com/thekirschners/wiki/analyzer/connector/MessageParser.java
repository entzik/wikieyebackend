package com.thekirschners.wiki.analyzer.connector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by emilkirschner on 14/10/14.
 */
class MessageParser {

    static final Pattern DEFAULT_PATTERN = Pattern.compile("\\[\\[([^:]+)\\]\\]\\s.?\\s(\\S+)\\s\\*\\s(.+)\\s\\*\\s.*");

    static final Pattern ABUSED_TOPIC_PATTERN = Pattern.compile(".*Log/abusefilter\\]\\] hit.*edit.*\\[\\[(.*)\\]\\]\\..*");
    static final Pattern SPANISH_ABUSED_TOPIC_PATTERN = Pattern.compile("\\[\\[Especial\\:Log/abusefilter\\]\\] hit.*edit.*\\s\\[\\[(.*)\\]\\]\\..*");
    static final Pattern COMPLEX_BLOCKED_USER_PATTERN = Pattern.compile(".*Log/block\\]\\]\\s.*\\s\\*\\s.*\\s\\[\\[.*:(.*)\\]\\]\\s.*");
    static final Pattern SIMPLE_BLOCKED_USER_PATTERN = Pattern.compile(".*Log/block\\]\\]\\s.*User:(.*)\\s\\(.*");
    static final Pattern LOG_PATTERN = Pattern.compile("\\[\\[\\S+:Log/(\\S+)\\]\\]\\s+\\S+\\s+\\*\\s+(\\S+)\\s+\\*\\s+(.*)");

    public static WikiEvent parseMessage(long time, String domain, String msg) {
        final Matcher logMatcher = LOG_PATTERN.matcher(msg);
        if (logMatcher.matches()) {
            final String action = logMatcher.group(1);
            String scndUser = null;
            String topic = null;
            if ("abusefilter".equals(action)) {
                final Matcher spanishAbuseMatcher = SPANISH_ABUSED_TOPIC_PATTERN.matcher(msg);
                if (spanishAbuseMatcher.matches()) {
                    topic = spanishAbuseMatcher.group(1);
                } else
                    try {
                        topic = ABUSED_TOPIC_PATTERN.matcher(msg).group(1);
                    } catch (Exception e) {
                        topic = "unknown";
                    }
            }
            else if ("block".equals(action)) {
                final Matcher matcher = COMPLEX_BLOCKED_USER_PATTERN.matcher(msg);
                if (matcher.matches())
                    scndUser = matcher.group(1);
                else {
                    Matcher matcher2 = SIMPLE_BLOCKED_USER_PATTERN.matcher(msg);
                    if (matcher2.matches())
                        scndUser = matcher2.group(1);
                }
            }
            final String user = logMatcher.group(2);
            final WikiEvent wikiEvent = new WikiEvent(time, domain, topic, action, user, scndUser);
            return wikiEvent;
        } else {
            final Matcher defaultMatcher = DEFAULT_PATTERN.matcher(msg);
            if (defaultMatcher.matches()) {
                final String topic = defaultMatcher.group(1);
                final String user = defaultMatcher.group(3);
                return new WikiEvent(time, domain, topic, "update", user, null);
            } else {
                return null;
            }
        }
    }
}
