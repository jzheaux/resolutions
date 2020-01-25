package io.jzheaux.springsecurity.resolutions;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

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
}
