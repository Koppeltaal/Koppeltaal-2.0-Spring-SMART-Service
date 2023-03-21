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
import nl.koppeltaal.spring.boot.starter.smartservice.dto.PractitionerDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.PractitionerDtoConverter;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class PractitionerFhirClientService extends BaseFhirClientCrudService<PractitionerDto, Practitioner> {

	public PractitionerFhirClientService(SmartServiceConfiguration smartServiceConfiguration, SmartClientCredentialService smartClientCredentialService, FhirContext fhirContext, PractitionerDtoConverter dtoConverter, AuditEventFhirClientService auditEventService) {
		super(smartServiceConfiguration, smartClientCredentialService, fhirContext, dtoConverter, auditEventService);
	}

	@Override
	protected String getDefaultSystem() {
		return "https://irma.app/email";
	}

	@Override
	protected String getResourceName() {
		return "Practitioner";
	}

	@Override
	protected Map<String, List<IQueryParameterType>> getEndOfLifeExclusion() {
		return Map.of(Practitioner.ACTIVE.getParamName(), List.of(new TokenParam("true")));
	}


}
