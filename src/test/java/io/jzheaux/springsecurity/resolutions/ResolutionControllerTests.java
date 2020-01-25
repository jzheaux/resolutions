package io.jzheaux.springsecurity.resolutions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.opaqueToken;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResolutionController.class)
@AutoConfigureMockMvc
public class ResolutionControllerTests {
	UUID joshId = UUID.fromString("219168d2-1da4-4f8a-85d8-95b4377af3c1");
	UUID carolId = UUID.fromString("328167d1-2da3-5f7a-86d7-96b4376af2c0");

	@Autowired
	MockMvc mvc;

	@MockBean
	ResolutionRepository repository;

	@MockBean
	OpaqueTokenIntrospector introspector;

	@Test
	public void resolutionsWhenDefaultOpaqueTokenThenForbidden() throws Exception {
		this.mvc.perform(get("/resolutions")
				.with(opaqueToken()))
				.andExpect(status().isForbidden());
	}

	@Test
	public void resolutionsWhenHasUserIdAndScopeThenOk() throws Exception {
		this.mvc.perform(get("/resolutions")
				.with(opaqueToken()
						.attribute("user_id", this.carolId.toString())
						.scopes("resolution:read")
				))
				.andExpect(status().isOk());
	}
}
