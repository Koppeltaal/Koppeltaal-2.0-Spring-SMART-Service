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
import nl.koppeltaal.spring.boot.starter.smartservice.dto.OrganizationDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.OrganizationDtoConverter;
import org.hl7.fhir.r4.model.Organization;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class OrganizationFhirClientService extends BaseFhirClientCrudService<OrganizationDto, Organization> {

	public OrganizationFhirClientService(SmartServiceConfiguration smartServiceConfiguration, SmartClientCredentialService smartClientCredentialService, FhirContext fhirContext, OrganizationDtoConverter organizationDtoConverter, AuditEventFhirClientService auditEventService) {
		super(smartServiceConfiguration, smartClientCredentialService, fhirContext, organizationDtoConverter, auditEventService);
	}

	@Override
	protected String getResourceName() {
		return "Organization";
	}

	protected String getDefaultSystem() {
		return "http://fhir.nl/fhir/NamingSystem/agb-z";
	}

}
