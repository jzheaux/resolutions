package io.jzheaux.springsecurity.resolutions;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests(a -> a
				.mvcMatchers("/resolutions").hasAuthority("SCOPE_resolution:read")
				.anyRequest().authenticated())
			.oauth2ResourceServer(o -> o.opaqueToken());
	}

	@Bean
	public OpaqueTokenIntrospector tokenIntrospector(OAuth2ResourceServerProperties properties) {
		String url = properties.getOpaquetoken().getIntrospectionUri();
		String clientId = properties.getOpaquetoken().getClientId();
		String clientSecret = properties.getOpaquetoken().getClientSecret();
		OpaqueTokenIntrospector delegate = new NimbusOpaqueTokenIntrospector
				(url, clientId, clientSecret);
		return new ResolutionOpaqueTokenIntrospector(delegate);
	}
}
