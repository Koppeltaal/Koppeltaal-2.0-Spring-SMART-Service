/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.spring.boot.starter.smartservice.service.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IFetchConformanceTyped;
import ca.uhn.fhir.rest.gclient.IFetchConformanceUntyped;
import java.util.List;
import nl.koppeltaal.spring.boot.starter.smartservice.configuration.SmartServiceConfiguration;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.Extension;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class FhirCapabilitiesService {
	private final FhirContext fhirContext;
	private final SmartServiceConfiguration smartServiceConfiguration;

	public FhirCapabilitiesService(FhirContext fhirContext, SmartServiceConfiguration smartServiceConfiguration) {
		this.fhirContext = fhirContext;
		this.smartServiceConfiguration = smartServiceConfiguration;
	}

	public String getTokenUrl() {
		IGenericClient client = fhirContext.newRestfulGenericClient(smartServiceConfiguration.getFhirServerUrl());
		IFetchConformanceUntyped capabilities = client.capabilities();
		IFetchConformanceTyped<CapabilityStatement> conformanceTyped = capabilities.ofType(CapabilityStatement.class);
		CapabilityStatement capabilityStatement = conformanceTyped.execute();
		List<CapabilityStatement.CapabilityStatementRestComponent> rest = capabilityStatement.getRest();
		for (CapabilityStatement.CapabilityStatementRestComponent capabilityStatementRestComponent : rest) {
			List<Extension> extensionsByUrl = capabilityStatementRestComponent.getSecurity().getExtensionsByUrl("http://fhir-registry.smarthealthit.org/StructureDefinition/oauth-uris");
			for (Extension extension : extensionsByUrl) {
				Extension token = extension.getExtensionByUrl("token");
				if (token != null) {
					return token.getValue().primitiveValue();
				}

			}
		}
		return null;
	}
	public String getAuthorizeUrl() {
		IGenericClient client = fhirContext.newRestfulGenericClient(smartServiceConfiguration.getFhirServerUrl());
		IFetchConformanceUntyped capabilities = client.capabilities();
		IFetchConformanceTyped<CapabilityStatement> conformanceTyped = capabilities.ofType(CapabilityStatement.class);
		CapabilityStatement capabilityStatement = conformanceTyped.execute();
		List<CapabilityStatement.CapabilityStatementRestComponent> rest = capabilityStatement.getRest();
		for (CapabilityStatement.CapabilityStatementRestComponent capabilityStatementRestComponent : rest) {
			List<Extension> extensionsByUrl = capabilityStatementRestComponent.getSecurity().getExtensionsByUrl("http://fhir-registry.smarthealthit.org/StructureDefinition/oauth-uris");
			for (Extension extension : extensionsByUrl) {
				Extension token = extension.getExtensionByUrl("authorize");
				if (token != null) {
					return token.getValue().primitiveValue();
				}

			}
		}
		return null;
	}
}
