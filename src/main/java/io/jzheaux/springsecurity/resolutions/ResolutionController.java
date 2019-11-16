package io.jzheaux.springsecurity.resolutions;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
	public List<Resolution> read() {
		UUID owner = UUID.fromString("219168d2-1da4-4f8a-85d8-95b4377af3c1");
		return this.resolutions.findByOwner(owner);
	}

	@GetMapping("/resolution/{id}")
	public Optional<Resolution> read(UUID id) {
		return this.resolutions.findById(id);
	}

	@PostMapping("/resolution")
	public Resolution make(@RequestBody String text) {
		UUID owner = UUID.fromString("219168d2-1da4-4f8a-85d8-95b4377af3c1");
		Resolution resolution = new Resolution(text, owner);
		return this.resolutions.save(resolution);
	}

	@PutMapping(path="/resolution/{id}/revise")
	@Transactional
	public Optional<Resolution> revise(@PathVariable("id") UUID id, @RequestBody String text) {
		this.resolutions.revise(id, text);
		return read(id);
	}

	@PutMapping("/resolution/{id}/complete")
	@Transactional
	public Optional<Resolution> complete(@PathVariable("id") UUID id) {
		this.resolutions.complete(id);
		return read(id);
	}
}
