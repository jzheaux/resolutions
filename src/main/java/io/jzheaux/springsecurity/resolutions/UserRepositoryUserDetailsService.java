package io.jzheaux.springsecurity.resolutions;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRepositoryUserDetailsService implements UserDetailsService {
	private final UserRepository users;

	public UserRepositoryUserDetailsService(UserRepository users) {
		this.users = users;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return this.users.findByUsername(username)
				.map(ResolutionsUserSpringSecurityUserDetails::new)
				.orElseThrow(() -> new UsernameNotFoundException("username not found"));
	}

	private static class ResolutionsUserSpringSecurityUserDetails
			extends User implements UserDetails, CredentialsContainer {
		private final List<GrantedAuthority> authorities;

		ResolutionsUserSpringSecurityUserDetails(User user) {
			super(user);
			this.authorities = user.getUserAuthorities().stream()
					.map(authority -> new SimpleGrantedAuthority(authority.authority))
					.collect(Collectors.toList());
		}

		@Override
		public List<GrantedAuthority> getAuthorities() {
			return this.authorities;
		}

		@Override
		public boolean isAccountNonExpired() {
			return true;
		}

		@Override
		public boolean isAccountNonLocked() {
			return true;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			return true;
		}

		@Override
		public boolean isEnabled() {
			return this.getEnabled();
		}

		@Override
		public void eraseCredentials() {
			this.setPassword(null);
		}
	}
}
