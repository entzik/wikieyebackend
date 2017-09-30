package com.thekirschners.wiki.analyzer.api;

import com.thekirschners.wiki.analyzer.Config;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(path = "/api/v1/config")
public class ConfigEndpoint {
	@RequestMapping(path = "domains", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<String[]> getLanguages() {
		return Mono.just(Config.WIKI_DOMAINS);
	}

	@RequestMapping(path = "actions", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<String[]> getActions() {
		return Mono.just(Config.ACTIONS);
	}

}
