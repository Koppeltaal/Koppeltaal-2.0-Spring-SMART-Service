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
import nl.koppeltaal.spring.boot.starter.smartservice.dto.CareTeamDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.CareTeamDtoConverter;
import org.hl7.fhir.r4.model.CareTeam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

	@Override
	protected Map<String, List<IQueryParameterType>> getEndOfLifeExclusion() {
		return Map.of(CareTeam.SP_STATUS + ":not", List.of(new TokenParam(CareTeam.CareTeamStatus.INACTIVE.toCode())));
	}


}
