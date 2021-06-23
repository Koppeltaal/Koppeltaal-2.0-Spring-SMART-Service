/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.spring.boot.starter.smartservice.configuration;

import ca.uhn.fhir.context.FhirContext;
import nl.koppeltaal.springbootstarterjwks.config.JwksConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "fhir.smart.service")
@ConditionalOnClass(JwksConfiguration.class)
public class SmartServiceConfiguration {
	String fhirServerUrl;
	String clientId;
	String scope = "*/read";
	boolean auditEventsEnabled = true;

	@Bean
	public FhirContext fhirContext() {
		return FhirContext.forR4();
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

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public boolean isAuditEventsEnabled() {
		return auditEventsEnabled;
	}

	public void setAuditEventsEnabled(boolean auditEventsEnabled) {
		this.auditEventsEnabled = auditEventsEnabled;
	}

	@Override
	public String toString() {
		return "SmartServiceConfiguration{" +
				"fhirServerUrl='" + fhirServerUrl + '\'' +
				", clientId='" + clientId + '\'' +
				", scope='" + scope + '\'' +
				", auditEventsEnabled=" + auditEventsEnabled +
				'}';
	}

}
