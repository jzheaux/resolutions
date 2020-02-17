package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RestController
public class ResolutionsController {
	private final WebClient resolutions;

	public ResolutionsController(WebClient resolutions) {
		this.resolutions = resolutions;
	}

	@GetMapping("/resolutions")
	public List<Resolution> resolutions() {
		return this.resolutions.get()
				.uri("/resolutions")
				.retrieve()
				.bodyToFlux(Resolution.class)
				.collectList()
				.block();
	}
}
