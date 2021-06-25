/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.spring.boot.starter.smartservice.service.fhir;

import ca.uhn.fhir.context.FhirContext;
import nl.koppeltaal.spring.boot.starter.smartservice.configuration.SmartServiceConfiguration;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.EndpointDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.EndpointDtoConverter;
import org.hl7.fhir.r4.model.Endpoint;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class EndpointFhirClientService extends BaseFhirClientCrudService<EndpointDto, Endpoint> {

	public EndpointFhirClientService(SmartServiceConfiguration smartServiceConfiguration, SmartClientCredentialService smartClientCredentialService, FhirContext fhirContext, EndpointDtoConverter locationDtoConverter, AuditEventFhirClientService auditEventService) {
		super(smartServiceConfiguration, smartClientCredentialService, fhirContext, locationDtoConverter, auditEventService);
	}

	@Override
	protected String getResourceName() {
		return "Endpoint";
	}

	protected String getDefaultSystem() {
		return "urn:ietf:rfc:3986";
	}

}
