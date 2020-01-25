package io.jzheaux.springsecurity.resolutions;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
	@PreAuthorize("hasAuthority('SCOPE_resolution:read')")
	public List<Resolution> read(@AuthenticationPrincipal Jwt jwt) {
		UUID owner = UUID.fromString(jwt.getClaim("user_id"));
		return this.resolutions.findByOwner(owner);
	}

	@GetMapping("/resolution/{id}")
	@PreAuthorize("hasAuthority('SCOPE_resolution:read')")
	@PostAuthorize("@owner.apply(returnObject, principal.claims['user_id'])")
	public Optional<Resolution> read(@PathVariable("id") UUID id) {
		return this.resolutions.findById(id);
	}

	@PostMapping("/resolution")
	@PreAuthorize("hasAuthority('SCOPE_resolution:write')")
	public Resolution make(@RequestBody String text, @AuthenticationPrincipal Jwt jwt) {
		UUID owner = UUID.fromString(jwt.getClaim("user_id"));
		Resolution resolution = new Resolution(text, owner);
		return this.resolutions.save(resolution);
	}

	@PutMapping(path="/resolution/{id}/revise")
	@PreAuthorize("hasAuthority('SCOPE_resolution:write')")
	@PostAuthorize("@owner.apply(returnObject, principal.claims['user_id'])")
	@Transactional
	public Optional<Resolution> revise(@PathVariable("id") UUID id, @RequestBody String text) {
		this.resolutions.revise(id, text);
		return read(id);
	}

	@PutMapping("/resolution/{id}/complete")
	@PreAuthorize("hasAuthority('SCOPE_resolution:write')")
	@PostAuthorize("@owner.apply(returnObject, principal.claims['user_id'])")
	@Transactional
	public Optional<Resolution> complete(@PathVariable("id") UUID id) {
		this.resolutions.complete(id);
		return read(id);
	}
}
