package nl.koppeltaal.spring.boot.starter.smartservice.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

/**
 *
 */
public class Oauth2TokenResponse implements Serializable {
	@JsonProperty(value = "access_token")
	String accessToken;
	@JsonProperty("refresh_token")
	String refreshToken;
	@JsonProperty("token_type")
	String tokenType;
	@JsonProperty("expires_in")
	String expiresIn;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
}
