package com.thekirschners.wiki.analyzer.connector.adapter.eventmetrics;

import com.codahale.metrics.MetricRegistry;
import com.google.common.eventbus.Subscribe;
import com.thekirschners.wiki.analyzer.connector.WikiEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.stups.boot.eventbus.EventBusSupport;

import java.util.concurrent.TimeUnit;

/**
 * Created by emilkirschner on 13/06/17.
 */
@Component
public class EventRateMetricsAdapter {
	MetricRegistry metrics;
	WikiMetricReporter metricReporter;

	@Autowired
	public EventRateMetricsAdapter(EventBusSupport eventBusSupport) {
		metrics = new MetricRegistry();

		metricReporter = WikiMetricReporter.forRegistry(metrics, eventBusSupport)
				.convertRatesTo(TimeUnit.MINUTES)
				.convertDurationsTo(TimeUnit.MILLISECONDS)
				.build();
		metricReporter.start(5, TimeUnit.SECONDS);
	}

	@Subscribe
	public void processEvent(WikiEvent wikiEvent) throws Exception {
		final String domain = wikiEvent.getDomain();
		metrics.meter("all#all").mark();
		metrics.meter(domain + "#all").mark();
		final String action = wikiEvent.getAction();
		metrics.meter(domain + "#" + action).mark();
		metrics.meter("all#" + action).mark();
	}

	public WikiMetricReporter getMetricReporter() {
		return metricReporter;
	}
}
