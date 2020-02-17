package io.jzheaux.springsecurity.resolutions;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class AuthenticationManagerResolverConfig {
	private static final String ONE = "http://localhost:9999/auth/realms/one";
	private static final String TWO = "http://localhost:9999/auth/realms/two";

	@Bean
	AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver() {
		return new JwtIssuerAuthenticationManagerResolver
				(ONE, TWO);
	}
}
