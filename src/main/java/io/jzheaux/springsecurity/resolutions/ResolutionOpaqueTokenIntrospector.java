package io.jzheaux.springsecurity.resolutions;

import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
	private final UserRepository users;

	public ResolutionOpaqueTokenIntrospector(
			OpaqueTokenIntrospector delegate, UserRepository users) {
		this.delegate = delegate;
		this.users = users;
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

		User user = users.findById(userId)
				.orElseThrow(() -> new UsernameNotFoundException("no user"));
		if ("premium".equals(user.getSubscription())) {
			if (authorities.stream().map(GrantedAuthority::getAuthority)
					.anyMatch(authority -> "SCOPE_resolution:write".equals(authority))) {
				authorities.add(new SimpleGrantedAuthority("resolution:share"));
			}
		}

		OAuth2AuthenticatedPrincipal delegate =
				new DefaultOAuth2AuthenticatedPrincipal(name, attributes, authorities);

		return new BridgeUser(user, delegate);
	}

	private static class BridgeUser extends User implements OAuth2AuthenticatedPrincipal {
		private final OAuth2AuthenticatedPrincipal delegate;

		public BridgeUser(User user, OAuth2AuthenticatedPrincipal delegate) {
			super(user);
			this.delegate = delegate;
		}

		@Override
		@Nullable
		public <A> A getAttribute(String name) {
			return delegate.getAttribute(name);
		}

		@Override
		public Map<String, Object> getAttributes() {
			return delegate.getAttributes();
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return delegate.getAuthorities();
		}

		@Override
		public String getName() {
			return delegate.getName();
		}
	}
}
