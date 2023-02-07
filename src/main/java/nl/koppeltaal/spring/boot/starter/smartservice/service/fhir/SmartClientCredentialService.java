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
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nl.koppeltaal.spring.boot.starter.smartservice.configuration.SmartServiceConfiguration;
import nl.koppeltaal.spring.boot.starter.smartservice.response.Oauth2TokenResponse;
import nl.koppeltaal.spring.boot.starter.smartservice.service.jwt.JwtValidationService;
import nl.koppeltaal.springbootstarterjwks.config.JwksConfiguration;
import nl.koppeltaal.springbootstarterjwks.util.KeyUtils;
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
import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.jwk.Use;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class SmartClientCredentialService {

	private static final Logger LOG = LoggerFactory.getLogger(SmartClientCredentialService.class);

	public static final String CLIENT_ASSERTION_TYPE = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";
	private final SmartServiceConfiguration smartServiceConfiguration;
	private final JwksConfiguration jwksConfiguration;
	private final FhirCapabilitiesService fhirCapabilitiesService;
	private final JwtValidationService jwtValidationService;

	private Oauth2TokenResponse tokenResponse;

	public SmartClientCredentialService(SmartServiceConfiguration smartServiceConfiguration,
			JwksConfiguration jwksConfiguration,
			FhirCapabilitiesService fhirCapabilitiesService, JwtValidationService jwtValidationService) {
		this.smartServiceConfiguration = smartServiceConfiguration;
		this.jwksConfiguration = jwksConfiguration;
		this.fhirCapabilitiesService = fhirCapabilitiesService;
		this.jwtValidationService = jwtValidationService;
	}

	public void checkCredentials() throws IOException {
		try {
			if (tokenResponse != null) {
				jwtValidationService.validate(tokenResponse.getAccessToken(), null, 0);
			}
		} catch (JwkException | JWTVerificationException e) {
			try {
				fetchToken();
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
			params.add(new BasicNameValuePair("scope", smartServiceConfiguration.getScope()));
			params.add(new BasicNameValuePair("client_assertion_type", CLIENT_ASSERTION_TYPE));
			params.add(new BasicNameValuePair("client_assertion", getSmartServiceClientAssertion(tokenUrl)));

			postTokenRequest(tokenUrl, httpClient, params);
		}
	}

	public String getAccessToken() throws IOException {
		if (tokenResponse == null) {
			fetchToken();
		} else {
			checkCredentials();
		}
		final String accessToken = tokenResponse.getAccessToken();
		LOG.info("Using Access Token: \n\n{}", accessToken);
		return accessToken;
	}

	public String getSmartServiceClientAssertion(String oauthTokenEndpoint) {
		try {
			JwtClaims claims = new JwtClaims();
			claims.setAudience(oauthTokenEndpoint);
			claims.setIssuer(smartServiceConfiguration.getClientId());
			claims.setSubject(smartServiceConfiguration.getClientId());
			claims.setIssuedAt(NumericDate.now());
			claims.setExpirationTime(NumericDate.fromMilliseconds(System.currentTimeMillis()
					+ jwksConfiguration.getJwtTimeoutInSeconds() * 1000L));
			claims.setJwtId(UUID.randomUUID().toString());

			JsonWebSignature jws = new JsonWebSignature();

			// The payload of the JWS is JSON content of the JWT Claims
			jws.setPayload(claims.toJson());

			KeyPair rsaKeyPair = KeyUtils
					.getRsaKeyPair(jwksConfiguration.getSigningPublicKey(), jwksConfiguration.getSigningPrivateKey());

			PublicJsonWebKey jwk = PublicJsonWebKey.Factory.newPublicJwk(rsaKeyPair.getPublic());
			jwk.setPrivateKey(rsaKeyPair.getPrivate());
			jwk.setUse(Use.SIGNATURE);
			jwk.setAlgorithm(jwksConfiguration.getSigningAlgorithm());

			// The JWT is signed using the private key
			jws.setKey(jwk.getPrivateKey());

			// Set the Key ID (kid) header because it's just the polite thing to do.
			// We only have one key in this example but a using a Key ID helps
			// facilitate a smooth key rollover process
			jws.setKeyIdHeaderValue(KeyUtils.getFingerPrint(rsaKeyPair.getPublic()));

			// Set the signature algorithm on the JWT/JWS that will integrity protect the claims
			jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA512);

			return jws.getCompactSerialization();
		} catch (JoseException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException("Failed to sign token", e);
		}
	}

	private CloseableHttpClient createHttpClient() {
		return HttpClients.createDefault();
	}

	private void postTokenRequest(String tokenUrl, CloseableHttpClient httpClient, List<NameValuePair> params) throws IOException {
		final HttpPost httpPost = new HttpPost(tokenUrl);
		httpPost.setHeader("Accept", "application/json");
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
				LOG.error(String.format("Unexpected response: %s from URL: %s", content, tokenUrl));
			}
			throw new IOException("System error");
		}
	}
}
