package io.jzheaux.springsecurity.resolutions;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ResolutionOpaqueTokenIntrospector implements OpaqueTokenIntrospector {
	private final OpaqueTokenIntrospector delegate;

	public ResolutionOpaqueTokenIntrospector(
			OpaqueTokenIntrospector delegate) {
		this.delegate = delegate;
	}

	@Override
	public OAuth2AuthenticatedPrincipal introspect(String token) {
		OAuth2AuthenticatedPrincipal principal = this.delegate.introspect(token);

		String name = principal.getName();
		Map<String, Object> attributes =
				new HashMap<>(principal.getAttributes());
		Collection<GrantedAuthority> authorities =
				new ArrayList<>(principal.getAuthorities());

		UUID userId = UUID.fromString(principal.getAttribute("user_id"));
		attributes.put("user_id", userId);

		return new DefaultOAuth2AuthenticatedPrincipal(name, attributes, authorities);
	}
}
