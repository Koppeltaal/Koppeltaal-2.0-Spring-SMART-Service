/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.spring.boot.starter.smartservice.service.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.TokenParam;
import nl.koppeltaal.spring.boot.starter.smartservice.configuration.SmartServiceConfiguration;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.EndpointDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.EndpointDtoConverter;
import org.hl7.fhir.r4.model.CareTeam;
import org.hl7.fhir.r4.model.Endpoint;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

	@Override
	protected Map<String, List<IQueryParameterType>> getEndOfLifeExclusion() {
		return Map.of(Endpoint.SP_STATUS + ":not", List.of(new TokenParam(Endpoint.EndpointStatus.OFF.toCode())));
	}

	protected String getDefaultSystem() {
		return "urn:ietf:rfc:3986";
	}

}
