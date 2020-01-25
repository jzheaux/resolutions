package io.jzheaux.springsecurity.resolutions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.MappedJwtClaimSetConverter;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResolutionsApplication {

	@Bean
	BiFunction<Optional<Resolution>, UUID, Boolean> owner() {
		return (resolution, userId) -> resolution
				.filter(r -> r.getOwner().equals(userId))
				.isPresent();
	}

	private MappedJwtClaimSetConverter claimSetConverter() {
		Converter<Object, UUID> converter = value ->
				UUID.fromString(value.toString());
		return MappedJwtClaimSetConverter.withDefaults
				(Collections.singletonMap("user_id", converter));
	}

	@Autowired
	void jwtDecoder(JwtDecoder jwtDecoder) {
		((NimbusJwtDecoder) jwtDecoder).setClaimSetConverter(claimSetConverter());
	}

	public static void main(String[] args) {
		SpringApplication.run(ResolutionsApplication.class, args);
	}

}