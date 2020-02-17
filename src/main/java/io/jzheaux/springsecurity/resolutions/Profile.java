package io.jzheaux.springsecurity.resolutions;

import java.util.UUID;

public class Profile {
	UUID id;

	String name;

	Profile() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
