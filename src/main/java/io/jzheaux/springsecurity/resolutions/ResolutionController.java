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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class ResolutionController {
	private final ResolutionRepository resolutions;
	private final ProfileService profiles;

	public ResolutionController(ResolutionRepository resolutions, ProfileService profiles) {
		this.resolutions = resolutions;
		this.profiles = profiles;
	}

	@GetMapping("/resolutions")
	@PreAuthorize("hasAuthority('SCOPE_resolution:read')")
	public List<Map<String, Object>> read(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
		return this.resolutions.findByOwner(principal.getAttribute("user_id"))
				.stream().map(this::resolutionToMap)
				.collect(Collectors.toList());
	}

	private Map<String, Object> resolutionToMap(Resolution resolution) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("id", resolution.getId());
		map.put("owner", this.profiles.findById(resolution.getOwner()).getName());
		map.put("completed", resolution.getCompleted());
		map.put("text", resolution.getText());
		return map;
	}

	@GetMapping("/resolution/{id}")
	@CrossOrigin
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
