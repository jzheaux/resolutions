package io.jzheaux.springsecurity.resolutions;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@RestController
public class AuthenticationManagerResolverConfig {
	private static final String ONE = "http://localhost:9999/auth/realms/one";
	private static final String TWO = "http://localhost:9999/auth/realms/two";
	private final Map<String, AuthenticationManager>
			authenticationManagers = new LinkedHashMap<>();

	AuthenticationManagerResolverConfig() {
		this.authenticationManagers.put(ONE, jwt(ONE));
		this.authenticationManagers.put(TWO, opaqueToken(TWO));
	}

	@Bean
	AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver() {
		return new RequestHeaderAuthenticationManagerResolver
				(this.authenticationManagers::get);
	}

	@PutMapping("/tenant/{tenant}")
	String addTenant(@PathVariable("tenant") String tenant) {
		String issuer = "http://localhost:9999/auth/realms/" + tenant;
		this.authenticationManagers.put(issuer, jwt(issuer));
		return issuer;
	}

	@DeleteMapping("/tenant/{tenant}")
	void removeTenant(@PathVariable("tenant") String tenant) {
		String issuer = "http://localhost:9999/auth/realms/" + tenant;
		this.authenticationManagers.remove(issuer);
	}

	AuthenticationManager jwt(String issuer) {
		JwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuer);
		JwtAuthenticationProvider provider = new JwtAuthenticationProvider(jwtDecoder);
		JwtGrantedAuthoritiesConverter defaults = new JwtGrantedAuthoritiesConverter();
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(jwt -> {
			Collection<GrantedAuthority> authorities = defaults.convert(jwt);
			if (ONE.equals(issuer)) {
				authorities.add(new SimpleGrantedAuthority("tenants:write"));
			}
			return authorities;
		});
		provider.setJwtAuthenticationConverter(converter);
		return provider::authenticate;
	}

	AuthenticationManager opaqueToken(String issuer) {
		OpaqueTokenIntrospector introspector = new NimbusOpaqueTokenIntrospector(
				issuer + "/protocol/openid-connect/token/introspect",
				"app",
				"bfbd9f62-02ce-4638-a370-80d45514bd0a"
		);
		OpaqueTokenAuthenticationProvider provider = new OpaqueTokenAuthenticationProvider(introspector);
		return provider::authenticate;
	}
}
