package io.jzheaux.springsecurity.resolutions;

import com.google.common.base.Suppliers;
import com.nimbusds.jwt.JWTParser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

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
		Supplier<AuthenticationManager> tokenType = Suppliers.memoize(this::tokenType);
		return request -> tokenType.get();
	}

	AuthenticationManager tokenType() {
		AuthenticationManager jwt = jwt();
		AuthenticationManager opaqueToken = opaqueToken();
		return authentication -> isAJwt((BearerTokenAuthenticationToken) authentication) ?
				jwt.authenticate(authentication) :
				opaqueToken.authenticate(authentication);
	}

	AuthenticationManager jwt() {
		JwtDecoder decoder = JwtDecoders.fromIssuerLocation("http://localhost:9999/auth/realms/one");
		JwtAuthenticationProvider provider = new JwtAuthenticationProvider(decoder);
		return provider::authenticate;
	}

	AuthenticationManager opaqueToken() {
		OpaqueTokenIntrospector introspector = new NimbusOpaqueTokenIntrospector(
				"http://localhost:9999/auth/realms/two/protocol/openid-connect/token/introspect",
				"app",
				"bfbd9f62-02ce-4638-a370-80d45514bd0a"
		);
		OpaqueTokenAuthenticationProvider provider = new OpaqueTokenAuthenticationProvider(introspector);
		return provider::authenticate;
	}

	boolean isAJwt(BearerTokenAuthenticationToken token) {
		try {
			JWTParser.parse(token.getToken());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
