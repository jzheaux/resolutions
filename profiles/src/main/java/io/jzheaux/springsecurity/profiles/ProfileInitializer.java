package io.jzheaux.springsecurity.profiles;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProfileInitializer implements SmartInitializingSingleton {
	private final ProfileRepository profiles;

	public ProfileInitializer(ProfileRepository profiles) {
		this.profiles = profiles;
	}

	@Override
	public void afterSingletonsInstantiated() {
		UUID joshId = UUID.fromString("219168d2-1da4-4f8a-85d8-95b4377af3c1");
		UUID carolId = UUID.fromString("328167d1-2da3-5f7a-86d7-96b4376af2c0");

		this.profiles.save(new Profile(joshId, "Josh Cummings"));
		this.profiles.save(new Profile(carolId, "Carol Carolson"));
	}
}
