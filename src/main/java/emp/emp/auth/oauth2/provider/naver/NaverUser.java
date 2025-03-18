package emp.emp.auth.oauth2.provider.naver;

import java.util.Map;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import emp.emp.auth.oauth2.provider.OAuth2ProviderUser;

public class NaverUser extends OAuth2ProviderUser {

	public NaverUser(OAuth2User oAuth2User, ClientRegistration clientRegistration) {
		super((Map<String, Object>)oAuth2User.getAttributes().get("response"), oAuth2User, clientRegistration);
	}

	@Override
	public String getVerifyId() {
		return "" + getAttributes().get("id");
	}

	@Override
	public String getEmail() {
		return (String)getAttributes().get("email");
	}
}
