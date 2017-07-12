package com.thekirschners.wiki.analyzer.connector;

/**
* Created by emilkirschner on 15/10/14.
*/
public class WikiEvent {
    final private long time;
    final private String domain;
    final private String topic;
    final private String action;
    final private String user;
    final private String scndUser;

    public WikiEvent(long time, String domain, String topic, String action, String user, String scndUser) {
        this.time = time;
        this.domain = domain;
        this.topic = topic;
        this.action = action;
        this.user = user;
        this.scndUser = scndUser;
    }

    public long getTime() {
        return time;
    }

    public String getDomain() {
        return domain;
    }

    public String getTopic() {
        return topic;
    }

    public String getAction() {
        return action;
    }

    public String getUser() {
        return user;
    }

    public String getScndUser() {
        return scndUser;
    }

    @Override
    public String toString() {
        return "WikiEvent{" +
                "time=" + time +
                ", domain='" + domain + '\'' +
                ", topic='" + topic + '\'' +
                ", action='" + action + '\'' +
                ", user='" + user + '\'' +
                ", scndUser='" + scndUser + '\'' +
                '}';
    }
}
