package com.thekirschners.wiki.analyzer.connector.adapter.keywords;

/**
 * Created by emilkirschner on 17/06/17.
 */
public class StringCount implements Comparable<StringCount> {
	final String word;
	final int count;

	public StringCount(String word, int count) {
		this.word = word;
		this.count = count;
	}

	public String getWord() {
		return word;
	}

	public int getCount() {
		return count;
	}

	@Override
	public int compareTo(StringCount o) {
		return this.count - o.count;
	}

	@Override
	public String toString() {
		return "{" +
				"word='" + word + '\'' +
				", count=" + count +
				'}';
	}

	public String toJson() {
		return "{\"word\": \"" + word + "\", \"count\":" + count + "}";
	}
}
