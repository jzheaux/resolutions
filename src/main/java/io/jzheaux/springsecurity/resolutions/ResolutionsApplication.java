package io.jzheaux.springsecurity.resolutions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResolutionsApplication {

	@Bean
	BiFunction<Optional<Resolution>, UUID, Boolean> owner() {
		return (resolution, userId) -> resolution
				.filter(r -> r.getOwner().toString().equals(userId))
				.isPresent();
	}

	public static void main(String[] args) {
		SpringApplication.run(ResolutionsApplication.class, args);
	}

}