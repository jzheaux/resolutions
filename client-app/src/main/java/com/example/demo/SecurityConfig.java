package com.example.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests(a -> a
				.anyRequest().authenticated())
			.csrf(c -> c.csrfTokenRepository
					(CookieCsrfTokenRepository.withHttpOnlyFalse()))
			.oauth2Login();
	}
}
