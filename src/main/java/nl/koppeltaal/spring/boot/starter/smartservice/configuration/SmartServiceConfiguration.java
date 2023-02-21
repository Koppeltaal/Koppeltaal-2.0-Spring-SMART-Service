/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.spring.boot.starter.smartservice.configuration;

import ca.uhn.fhir.context.FhirContext;
import javax.annotation.PostConstruct;
import nl.koppeltaal.springbootstarterjwks.config.JwksConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

@Configuration
@ConfigurationProperties(prefix = "fhir.smart.service")
@ConditionalOnClass(JwksConfiguration.class)
public class SmartServiceConfiguration {
	String fhirServerUrl;
	String clientId;
	String deviceRef;
	String scope = "*/read";
	String metaSourceUuid;
	boolean auditEventsEnabled = true;
	boolean bearerTokenEnabled = true;

	@Bean
	public FhirContext fhirContext() {
		return FhirContext.forR4();
	}

	@PostConstruct
	public void validate() {
		Assert.notNull(fhirServerUrl, "The config property [fhir.smart.service.fhirServerUrl] is required to communicate  with the FHIR Store");
		Assert.notNull(clientId, "The config property [fhir.smart.service.clientId] is required to communicate with the FHIR Store");
		Assert.notNull(deviceRef, "The config property [fhir.smart.service.deviceRef] is required to create AuditEvents");
		Assert.notNull(metaSourceUuid, "The config property [fhir.smart.service.metaSourceUuid] is required to set a source on entities");
		Assert.isTrue(deviceRef.startsWith("Device/"), "The config property [fhir.smart.service.deviceRef] must start with \"Device/\"");
		Assert.isTrue(metaSourceUuid.startsWith("urn:uuid:"), "The config property [fhir.smart.service.metaSourceUuid] must start with \"urn:uuid:\"");
	}

	public String getFhirServerUrl() {
		return fhirServerUrl;
	}

	public void setFhirServerUrl(String fhirServerUrl) {
		this.fhirServerUrl = fhirServerUrl;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getDeviceRef() {
		return deviceRef;
	}

	public void setDeviceRef(String deviceRef) {
		this.deviceRef = deviceRef;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getMetaSourceUuid() {
		return metaSourceUuid;
	}

	public void setMetaSourceUuid(String metaSourceUuid) {
		this.metaSourceUuid = metaSourceUuid;
	}

	public boolean isAuditEventsEnabled() {
		return auditEventsEnabled;
	}

	public void setAuditEventsEnabled(boolean auditEventsEnabled) {
		this.auditEventsEnabled = auditEventsEnabled;
	}

	public boolean isBearerTokenEnabled() {
		return bearerTokenEnabled;
	}

	public void setBearerTokenEnabled(boolean bearerTokenEnabled) {
		this.bearerTokenEnabled = bearerTokenEnabled;
	}

	@Override
	public String toString() {
		return "SmartServiceConfiguration{" +
				"fhirServerUrl='" + fhirServerUrl + '\'' +
				", clientId='" + clientId + '\'' +
				", deviceRef='" + deviceRef + '\'' +
				", scope='" + scope + '\'' +
				", metaSourceUuid='" + metaSourceUuid + '\'' +
				", auditEventsEnabled=" + auditEventsEnabled +
				", bearerTokenEnabled=" + bearerTokenEnabled +
				'}';
	}

}
