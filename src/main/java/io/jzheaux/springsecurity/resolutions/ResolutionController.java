package io.jzheaux.springsecurity.resolutions;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class ResolutionController {
	private final ResolutionRepository resolutions;

	public ResolutionController(ResolutionRepository resolutions) {
		this.resolutions = resolutions;
	}

	@GetMapping("/resolutions")
	public List<Resolution> read(@CurrentUserId UUID userId) {
		UUID owner = userId;
		return this.resolutions.findByOwner(owner);
	}

	@GetMapping("/resolution/{id}")
	public Optional<Resolution> readOne(@PathVariable("id") UUID id) {
		return this.resolutions.findById(id);
	}

	@PostMapping("/resolution")
	public Resolution make(@RequestBody String text, @CurrentUserId UUID userId) {
		UUID owner = userId;
		Resolution resolution = new Resolution(text, owner);
		return this.resolutions.save(resolution);
	}

	@PutMapping(path="/resolution/{id}/revise")
	@Transactional
	public Optional<Resolution> revise(@PathVariable("id") UUID id, @RequestBody String text) {
		this.resolutions.revise(id, text);
		return readOne(id);
	}

	@PutMapping("/resolution/{id}/complete")
	@Transactional
	public Optional<Resolution> complete(@PathVariable("id") UUID id) {
		this.resolutions.complete(id);
		return readOne(id);
	}

	@PostMapping("/resolution/{id}/share")
	public void share(@PathVariable("id") UUID id, @AuthenticationPrincipal User user) {
		readOne(id).ifPresent(resolution -> {
			for (UUID friend : user.getFriends()) {
				make(resolution.getText(), friend);
			}
		});
	}
}
