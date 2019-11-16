package io.jzheaux.springsecurity.resolutions;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static javax.persistence.CascadeType.ALL;

@Entity(name="users")
public class User {
	@Id
	UUID id;

	@Column
	String username;

	@Column
	String password;

	@Column
	Boolean enabled = true;

	@OneToMany(cascade=ALL)
	List<UserAuthority> authorities = new ArrayList<>();

	User() {}

	public User(UUID id, String username, String password) {
		this.id = id;
		this.username = username;
		this.password = password;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public List<UserAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<UserAuthority> authorities) {
		this.authorities = authorities;
	}

	public Iterable<UserAuthority> authorities() {
		return Collections.unmodifiableCollection(this.authorities);
	}

	public void addAuthority(String authority) {
		this.authorities.add(new UserAuthority(this, authority));
	}
}
