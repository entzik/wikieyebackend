package com.thekirschners.wiki.analyzer.api;

import com.thekirschners.wiki.analyzer.Config;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(path = "/api/v1/config", produces = MediaType.APPLICATION_JSON_VALUE)
public class WikiMonitorEndpoint {
	@RequestMapping(path = "domains")
	public List<String> getLanguages() {
		return Arrays.stream(Config.WIKI_DOMAINS).collect(toList());
	}

	@RequestMapping(path = "actions")
	public List<String> getActions() {
		return Arrays.stream(Config.ACTIONS).collect(toList());
	}

}
