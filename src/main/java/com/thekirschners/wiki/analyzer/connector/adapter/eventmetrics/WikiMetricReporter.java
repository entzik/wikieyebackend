package com.thekirschners.wiki.analyzer.connector.adapter.eventmetrics;

import com.codahale.metrics.*;
import com.codahale.metrics.Timer;
import com.thekirschners.wiki.analyzer.domain.WikiMetric;
import org.zalando.stups.boot.eventbus.EventBusSupport;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by emilkirschner on 31/10/15.
 */
public class WikiMetricReporter extends ScheduledReporter {
	private final Clock clock;
	EventBusSupport eventBusSupport;

	public static Builder forRegistry(MetricRegistry registry, EventBusSupport eventBusSupport) {
		return new Builder(registry, eventBusSupport);
	}

	protected WikiMetricReporter(EventBusSupport eventBusSupport, MetricRegistry registry, Clock clock, String name, MetricFilter filter, TimeUnit rateUnit, TimeUnit durationUnit) {
		super(registry, name, filter, rateUnit, durationUnit);
		this.eventBusSupport = eventBusSupport;
		this.clock = clock;
	}


	/**
	 * A builder for {@link CsvReporter} instances. Defaults to using the default locale, converting
	 * rates to events/second, converting durations to milliseconds, and not filtering metrics.
	 */
	public static class Builder {
		private final MetricRegistry registry;
		private final EventBusSupport eventBusSupport;
		private Locale locale;
		private TimeUnit rateUnit;
		private TimeUnit durationUnit;
		private Clock clock;
		private MetricFilter filter;

		private Builder(MetricRegistry registry, EventBusSupport eventBusSupport) {
			this.registry = registry;
			this.eventBusSupport = eventBusSupport;
			this.locale = Locale.getDefault();
			this.rateUnit = TimeUnit.SECONDS;
			this.durationUnit = TimeUnit.MILLISECONDS;
			this.clock = Clock.defaultClock();
			this.filter = MetricFilter.ALL;
		}

		/**
		 * Format numbers for the given {@link Locale}.
		 *
		 * @param locale a {@link Locale}
		 * @return {@code this}
		 */
		public Builder formatFor(Locale locale) {
			this.locale = locale;
			return this;
		}

		/**
		 * Convert rates to the given time unit.
		 *
		 * @param rateUnit a unit of time
		 * @return {@code this}
		 */
		public Builder convertRatesTo(TimeUnit rateUnit) {
			this.rateUnit = rateUnit;
			return this;
		}

		/**
		 * Convert durations to the given time unit.
		 *
		 * @param durationUnit a unit of time
		 * @return {@code this}
		 */
		public Builder convertDurationsTo(TimeUnit durationUnit) {
			this.durationUnit = durationUnit;
			return this;
		}

		/**
		 * Use the given {@link Clock} instance for the time.
		 *
		 * @param clock a {@link Clock} instance
		 * @return {@code this}
		 */
		public Builder withClock(Clock clock) {
			this.clock = clock;
			return this;
		}

		/**
		 * Only report metrics which match the given accept.
		 *
		 * @param filter a {@link MetricFilter}
		 * @return {@code this}
		 */
		public Builder filter(MetricFilter filter) {
			this.filter = filter;
			return this;
		}

		public WikiMetricReporter build() {
			return new WikiMetricReporter(eventBusSupport, registry, clock, "eventBuss", filter, rateUnit, durationUnit);
		}
	}

	@Override
	public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters, SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {
		final long timestamp = TimeUnit.MILLISECONDS.toSeconds(clock.getTime());

		for (Map.Entry<String, Meter> entry : meters.entrySet()) {
			reportMeter(timestamp, entry.getKey(), entry.getValue());
		}
	}

	private void reportMeter(long timestamp, String name, Meter meter) {
		final String[] split = name.split("#");
		final WikiMetric wikiMetric = new WikiMetric(
				timestamp,
				split[0],
				split[1],
				convertRate(meter.getOneMinuteRate())
		);
		eventBusSupport.postAsync(wikiMetric);
	}

	protected String sanitize(String name) {
		return name;
	}

	@FunctionalInterface
	interface Converter {
		double convert(double x);
	}
}
