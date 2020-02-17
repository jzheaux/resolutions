package io.jzheaux.springsecurity.resolutions;

import com.google.common.base.Suppliers;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Supplier;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests(a -> a
				.anyRequest().authenticated())
			.oauth2ResourceServer(o -> o
				.authenticationManagerResolver(authenticationManagerResolver()));
	}

	AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver() {
		Supplier<AuthenticationManager> jwt = Suppliers.memoize(this::jwt);
		return request -> jwt.get();
	}

	AuthenticationManager jwt() {
		JwtDecoder decoder = JwtDecoders.fromIssuerLocation("http://localhost:9999/auth/realms/one");
		JwtAuthenticationProvider provider = new JwtAuthenticationProvider(decoder);
		return provider::authenticate;
	}
}
