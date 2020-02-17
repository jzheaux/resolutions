package io.jzheaux.springsecurity.profiles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
public class ProfileController {
	@Autowired
	ProfileRepository users;

	@GetMapping("/user/{userId}")
	Optional<Profile> info(@PathVariable("userId") UUID userId) {
		return this.users.findById(userId);
	}
}
