package io.jzheaux.springsecurity.resolutions;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
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
	public List<Resolution> read(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
		return this.resolutions.findByOwner(principal.getAttribute("user_id"));
	}

	@GetMapping("/resolution/{id}")
	@CrossOrigin(maxAge = 0)
	@PreAuthorize("hasAuthority('SCOPE_resolution:read')")
	@PostAuthorize("hasRole('ADMIN') || @owner.apply(returnObject, principal.attributes['user_id'])")
	public Optional<Resolution> read(@PathVariable("id") UUID id) {
		return this.resolutions.findById(id);
	}

	@PostMapping("/resolution")
	@PreAuthorize("hasAuthority('SCOPE_resolution:write')")
	public Resolution make(@RequestBody String text,
			@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
		UUID owner = principal.getAttribute("user_id");
		Resolution resolution = new Resolution(text, owner);
		return this.resolutions.save(resolution);
	}

	@PutMapping(path="/resolution/{id}/revise")
	@PreAuthorize("hasAuthority('SCOPE_resolution:write')")
	@PostAuthorize("@owner.apply(returnObject, principal.attributes['user_id'])")
	@Transactional
	public Optional<Resolution> revise(@PathVariable("id") UUID id, @RequestBody String text) {
		this.resolutions.revise(id, text);
		return read(id);
	}

	@PutMapping("/resolution/{id}/complete")
	@PreAuthorize("hasAuthority('SCOPE_resolution:write')")
	@PostAuthorize("@owner.apply(returnObject, principal.attributes['user_id'])")
	@Transactional
	public Optional<Resolution> complete(@PathVariable("id") UUID id) {
		this.resolutions.complete(id);
		return read(id);
	}
}
