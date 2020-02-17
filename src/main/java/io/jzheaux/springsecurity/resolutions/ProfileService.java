package io.jzheaux.springsecurity.resolutions;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Service
public class ProfileService {
	private final WebClient users;

	public ProfileService(WebClient users) {
		this.users = users;
	}

	public Profile findById(UUID userId) {
		return this.users.get()
				.uri("/user/" + userId)
				.retrieve()
				.bodyToMono(Profile.class)
				.block();
	}
}
