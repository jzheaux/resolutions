package io.jzheaux.springsecurity.resolutions;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.springframework.security.oauth2.server.resource.BearerTokenErrorCodes.INVALID_TOKEN;

public class RequestHeaderAuthenticationManagerResolver
		implements AuthenticationManagerResolver<HttpServletRequest> {
	private static final OAuth2Error INVALID_TENANT =
			new OAuth2Error(INVALID_TOKEN, "invalid tenant", null);

	private final AuthenticationManagerResolver<String> tenantAuthenticationManagerResolver;

	public RequestHeaderAuthenticationManagerResolver
			(AuthenticationManagerResolver<String> tenantAuthenticationManagerResolver) {
		this.tenantAuthenticationManagerResolver = tenantAuthenticationManagerResolver;
	}

	@Override
	public AuthenticationManager resolve(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader("tenant-id"))
				.map(this.tenantAuthenticationManagerResolver::resolve)
				.orElseThrow(() -> new OAuth2AuthenticationException(INVALID_TENANT));
	}
}
