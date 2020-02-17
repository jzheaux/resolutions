package io.jzheaux.springsecurity.resolutions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import javax.servlet.http.HttpServletRequest;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests(a -> a
				.mvcMatchers("/tenant/**").hasAuthority("tenants:write")
				.anyRequest().authenticated())
			.oauth2ResourceServer(o -> o
				.authenticationManagerResolver(this.authenticationManagerResolver));
	}
}
