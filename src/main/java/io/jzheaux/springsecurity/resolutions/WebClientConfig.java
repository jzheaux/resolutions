package io.jzheaux.springsecurity.resolutions;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServletBearerExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	@Bean
	WebClient profiles() {
		return WebClient.builder()
				.baseUrl("http://localhost:8081")
				.filter(new ServletBearerExchangeFilterFunction())
				.build();
	}
}
