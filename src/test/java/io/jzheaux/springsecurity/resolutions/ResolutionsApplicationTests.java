package io.jzheaux.springsecurity.resolutions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ResolutionsApplicationTests {
	UUID joshId = UUID.fromString("219168d2-1da4-4f8a-85d8-95b4377af3c1");
	UUID carolId = UUID.fromString("328167d1-2da3-5f7a-86d7-96b4376af2c0");

	@Autowired
	MockMvc mvc;

	@Test
	public void resolutionsWhenNoTokenThenUnauthorized() throws Exception {
		this.mvc.perform(get("/resolutions"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	public void resolutionsWhenNoScopesThenForbidden() throws Exception {
		this.mvc.perform(get("/resolutions")
				.with(jwt()))
				.andExpect(status().isForbidden());
	}

	@Test
	public void resolutionsWhenWrongScopeThenForbidden() throws Exception {
		this.mvc.perform(get("/resolutions")
				.with(jwt().authorities(new SimpleGrantedAuthority("SCOPE_resolution:write"))))
				.andExpect(status().isForbidden());
	}

	@Test
	public void resolutionsWhenReadScopeThenAuthorized() throws Exception {
		this.mvc.perform(get("/resolutions")
				.with(jwt().jwt(j -> j
						.claim("user_id", this.carolId)
						.claim("scope", "resolution:read")
				)))
				.andExpect(status().isOk());
	}
}
