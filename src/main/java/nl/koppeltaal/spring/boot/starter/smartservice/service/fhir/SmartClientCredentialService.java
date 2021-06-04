/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.spring.boot.starter.smartservice.service.fhir;

import com.auth0.jwk.JwkException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import nl.koppeltaal.spring.boot.starter.smartservice.configuration.SmartServiceConfiguration;
import nl.koppeltaal.spring.boot.starter.smartservice.response.Oauth2TokenResponse;
import nl.koppeltaal.spring.boot.starter.smartservice.service.jwt.JwtValidationService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class SmartClientCredentialService {
	public static final String DEFAULT_SCOPE = "*/write";
	private final SmartServiceConfiguration smartServiceConfiguration;
	private final FhirCapabilitiesService fhirCapabilitiesService;
	private final JwtValidationService jwtValidationService;

	private Oauth2TokenResponse tokenResponse;

	private final Log LOG = LogFactory.getLog(SmartClientCredentialService.class);


	public SmartClientCredentialService(SmartServiceConfiguration smartServiceConfiguration, FhirCapabilitiesService fhirCapabilitiesService, JwtValidationService jwtValidationService) {
		this.smartServiceConfiguration = smartServiceConfiguration;
		this.fhirCapabilitiesService = fhirCapabilitiesService;
		this.jwtValidationService = jwtValidationService;
	}

	public void checkCredentials() throws IOException {
		try {
			if (tokenResponse != null) {
				jwtValidationService.validate(tokenResponse.getAccessToken(), null, 60);
			}
		} catch (JwkException | JWTVerificationException e) {
			try {
				refreshToken();
			} catch (IOException ex) {
				LOG.warn("Got error during refresh, restart and fetch a new token.");
				fetchToken();
			}
		}
	}

	public void fetchToken() throws IOException {
		String tokenUrl = fhirCapabilitiesService.getTokenUrl();
		try (CloseableHttpClient httpClient = createHttpClient()) {

			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("grant_type", "client_credentials"));
			params.add(new BasicNameValuePair("scope", DEFAULT_SCOPE));

			postTokenRequest(tokenUrl, httpClient, params);
		}

	}

	public String getAccessToken() throws IOException {
		if (tokenResponse == null) {
			fetchToken();
		} else {
			checkCredentials();
		}
		return tokenResponse.getAccessToken();
	}

	public void refreshToken() throws IOException {
		String tokenUrl = fhirCapabilitiesService.getTokenUrl();
		try (CloseableHttpClient httpClient = createHttpClient()) {

			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair("grant_type", "refresh_token"));
			params.add(new BasicNameValuePair("refresh_token", tokenResponse.getRefreshToken()));
			params.add(new BasicNameValuePair("scope", DEFAULT_SCOPE));

			postTokenRequest(tokenUrl, httpClient, params);
		}

	}

	private CloseableHttpClient createHttpClient() {
		return HttpClients.createDefault();
	}

	private void postTokenRequest(String tokenUrl, CloseableHttpClient httpClient, List<NameValuePair> params) throws IOException {
		final HttpPost httpPost = new HttpPost(tokenUrl);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(String.format("%s:%s", smartServiceConfiguration.getClientId(), smartServiceConfiguration.getClientSecret()).getBytes(StandardCharsets.US_ASCII)));
		httpPost.setEntity(new UrlEncodedFormEntity(params));
		CloseableHttpResponse response = httpClient.execute(httpPost);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == 401) {
			throw new IOException("Access denied");
		} else if (statusCode >= 200 && statusCode < 300) {
			try (InputStream in = response.getEntity().getContent()) {
				String content = IOUtils.toString(new InputStreamReader(in, Charset.defaultCharset()));
				ObjectMapper objectMapper = new ObjectMapper();
				try {
					tokenResponse = objectMapper.readValue(content, Oauth2TokenResponse.class);
				} catch (JsonParseException e) {
					LOG.error(String.format("Failed to parse content: %s", content));
					throw e;
				}
			}
		} else {
			try (InputStream in = response.getEntity().getContent()) {
				String content = IOUtils.toString(new InputStreamReader(in, Charset.defaultCharset()));
				LOG.error(String.format("Unexpected response: %s", content));
			}
			throw new IOException("System error");
		}
	}
}
