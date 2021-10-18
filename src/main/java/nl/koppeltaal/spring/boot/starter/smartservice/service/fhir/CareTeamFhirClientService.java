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
import nl.koppeltaal.spring.boot.starter.smartservice.dto.CareTeamDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.CareTeamDtoConverter;
import org.hl7.fhir.r4.model.CareTeam;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class CareTeamFhirClientService extends BaseFhirClientCrudService<CareTeamDto, CareTeam> {

	public CareTeamFhirClientService(SmartServiceConfiguration smartServiceConfiguration, SmartClientCredentialService smartClientCredentialService, FhirContext fhirContext, CareTeamDtoConverter dtoConverter, AuditEventFhirClientService auditEventService) {
		super(smartServiceConfiguration, smartClientCredentialService, fhirContext, dtoConverter, auditEventService);
	}

	@Override
	protected String getDefaultSystem() {
		return "system";
	}

	@Override
	protected String getResourceName() {
		return "CareTeam";
	}


}
