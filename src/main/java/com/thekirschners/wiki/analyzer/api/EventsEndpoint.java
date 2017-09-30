package com.thekirschners.wiki.analyzer.api;

import com.google.common.eventbus.Subscribe;
import com.thekirschners.wiki.analyzer.domain.TopWordsMetric;
import com.thekirschners.wiki.analyzer.domain.UpdateEvent;
import com.thekirschners.wiki.analyzer.domain.WikiMetric;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;


@RestController
@RequestMapping(path = "/api/v1/events")
public class EventsEndpoint {
	DirectProcessor<UpdateEvent> processor;
	Flux<UpdateEvent> flux;

	public EventsEndpoint() {
		processor = DirectProcessor.create();
		flux = Flux.from(processor);
	}

	@Subscribe
	public void handleWikiMetric(WikiMetric wikiMetric) {
		processor.onNext(new UpdateEvent(WikiMetric.class.getSimpleName(), wikiMetric));
	}

	@Subscribe
	public void handleTopWordMetric(TopWordsMetric topWordsMetric) {
		processor.onNext(new UpdateEvent(TopWordsMetric.class.getSimpleName(), topWordsMetric));
	}

	@RequestMapping(path = "updates", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<UpdateEvent> getUpdates() {
		return flux.share();
	}
}
