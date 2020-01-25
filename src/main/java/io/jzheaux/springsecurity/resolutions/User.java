package io.jzheaux.springsecurity.resolutions;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class User {
	@Id
	UUID id;

	String subscription;

	@ElementCollection(fetch = FetchType.EAGER)
	List<UUID> friends = new ArrayList<>();

	User() {}

	public User(UUID id, String subscription, List<UUID> friends) {
		this.id = id;
		this.subscription = subscription;
		this.friends = friends;
	}

	public User(User user) {
		this.id = user.id;
		this.subscription = user.subscription;
		this.friends = user.friends;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getSubscription() {
		return subscription;
	}

	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}

	public List<UUID> getFriends() {
		return friends;
	}

	public void setFriends(List<UUID> friends) {
		this.friends = friends;
	}
}
