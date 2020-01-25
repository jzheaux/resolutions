package io.jzheaux.springsecurity.resolutions;

import com.nimbusds.oauth2.sdk.ResourceOwnerPasswordCredentialsGrant;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ResolutionsApplicationTests {
	UUID joshId = UUID.fromString("219168d2-1da4-4f8a-85d8-95b4377af3c1");
	UUID carolId = UUID.fromString("328167d1-2da3-5f7a-86d7-96b4376af2c0");

	@Autowired
	MockMvc mvc;

	@Autowired
	OAuth2ResourceServerProperties properties;

	@Test
	public void resolutionWhenOpaqueTokenThenSucceeds() throws Exception {
		String token = token("carol", "carol");
		this.mvc.perform(get("/resolutions")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}

	@Test
	public void shareWhenNoShareAuthorityThenForbidden() throws Exception {
		String token = token("josh", "josh");
		this.mvc.perform(post("/resolution/219168d2-1da4-4f8a-85d8-95b4377af3c1/share")
				.header("Authorization", "Bearer " + token)
				.with(csrf()))
				.andExpect(status().isForbidden());
	}

	@Test
	public void shareWhenHasAuthorityThenPasses() throws Exception {
		String token = token("carol", "carol");
		this.mvc.perform(post("/resolution/219168d2-1da4-4f8a-85d8-95b4377af3c1/share")
				.header("Authorization", "Bearer " + token)
				.with(csrf()))
				.andExpect(status().isOk());
	}

	private String token(String user, String password) throws Exception {
		String tokenUri = "http://localhost:9999/auth/realms/one/protocol/openid-connect/token";
		ClientID client = new ClientID(this.properties.getOpaquetoken().getClientId());
		Secret secret = new Secret(this.properties.getOpaquetoken().getClientSecret());
		TokenRequest request = new TokenRequest(
				URI.create(tokenUri),
				new ClientSecretBasic(client, secret),
				new ResourceOwnerPasswordCredentialsGrant(user, new Secret(password)),
				new Scope("resolution:read", "resolution:write"));
		TokenResponse response = TokenResponse.parse(request.toHTTPRequest().send());
		AccessToken token = response.toSuccessResponse().getTokens().getAccessToken();
		return token.getValue();
	}
}
