package nl.koppeltaal.spring.boot.starter.smartservice.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

/**
 * Can be serialized with the OAuth2 client_credentials response
 */
public class Oauth2TokenResponse implements Serializable {

	String scope;
	@JsonProperty(value = "access_token")
	String accessToken;
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

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	@Override
	public String toString() {
		return "Oauth2TokenResponse{" +
				"accessToken='" + accessToken + '\'' +
				", scope='" + scope + '\'' +
				", tokenType='" + tokenType + '\'' +
				", expiresIn='" + expiresIn + '\'' +
				'}';
	}
}
