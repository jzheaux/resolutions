package io.jzheaux.springsecurity.resolutions;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.text.ParseException;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ResolutionController.class)
@Import({ ResolutionsApplication.class, ResolutionsApplicationTests.JwtDecoderConfig.class })
@AutoConfigureMockMvc
class ResolutionsApplicationTests {
	UUID joshId = UUID.fromString("219168d2-1da4-4f8a-85d8-95b4377af3c1");
	UUID carolId = UUID.fromString("328167d1-2da3-5f7a-86d7-96b4376af2c0");

	@Autowired
	MockMvc mvc;

	@MockBean
	ResolutionRepository resolutions;

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

	@Test
	public void resolutionByIdWhenAdminThenAuthorized() throws Exception {
		Resolution josh = new Resolution("Mow the lawn", this.joshId);
		Resolution carol = new Resolution("Sing Christmas Carols", this.carolId);
		when(this.resolutions.findById(josh.getId())).thenReturn(Optional.of(josh));
		when(this.resolutions.findById(carol.getId())).thenReturn(Optional.of(carol));

		String joshToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMjE5MTY4ZDItMWRhNC00ZjhhLTg1ZDgtOTViNDM3N2FmM2MxIiwic2NvcGUiOiJyZXNvbHV0aW9uOnJlYWQiLCJhdWQiOiJyZXNvbHV0aW9uIn0.";
		String carolToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMzI4MTY3ZDEtMmRhMy01ZjdhLTg2ZDctOTZiNDM3NmFmMmMwIiwic2NvcGUiOiJyZXNvbHV0aW9uOnJlYWQiLCJhdWQiOiJyZXNvbHV0aW9uIn0.";

		this.mvc.perform(get("/resolution/" + josh.getId())
				.header("Authorization", "Bearer " + carolToken))
				.andExpect(status().isOk());
		this.mvc.perform(get("/resolution/" + carol.getId())
				.header("Authorization", "Bearer " + joshToken))
				.andExpect(status().isForbidden());
	}

	@Test
	public void resolutionsWhenNoAudienceThenUnauthorized() throws Exception {
		String audience = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMjE5MTY4ZDItMWRhNC00ZjhhLTg1ZDgtOTViNDM3N2FmM2MxIiwic2NvcGUiOiJyZXNvbHV0aW9uOnJlYWQiLCJhdWQiOiJyZXNvbHV0aW9uIn0.";
		String noAudience = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMjE5MTY4ZDItMWRhNC00ZjhhLTg1ZDgtOTViNDM3N2FmM2MxIiwic2NvcGUiOiJyZXNvbHV0aW9uOnJlYWQifQ.";

		this.mvc.perform(get("/resolutions")
				.header("Authorization", "Bearer " + audience))
				.andExpect(status().isOk());
		this.mvc.perform(get("/resolutions")
				.header("Authorization", "Bearer " + noAudience))
				.andExpect(status().isUnauthorized());
	}

	@Configuration
	static class JwtDecoderConfig {
		@Bean
		JwtDecoder jwtDecoder() {
			return new NimbusJwtDecoder(new MockJWTProcessor());
		}

		private static class MockJWTProcessor extends DefaultJWTProcessor<SecurityContext> {
			@Override
			public JWTClaimsSet process(SignedJWT signedJwt, SecurityContext context) {
				try {
					return signedJwt.getJWTClaimsSet();
				} catch (ParseException e) {
					throw new IllegalArgumentException(e);
				}
			}
		}
	}
}
