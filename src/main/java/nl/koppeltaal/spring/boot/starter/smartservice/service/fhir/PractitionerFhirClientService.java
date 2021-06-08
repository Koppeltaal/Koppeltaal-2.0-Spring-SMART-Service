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
import nl.koppeltaal.spring.boot.starter.smartservice.dto.PractitionerDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.PractitionerDtoConverter;
import org.hl7.fhir.r4.model.Practitioner;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class PractitionerFhirClientService extends BaseFhirClientService<PractitionerDto, Practitioner> {

	public PractitionerFhirClientService(SmartServiceConfiguration smartServiceConfiguration, SmartClientCredentialService smartClientCredentialService, FhirContext fhirContext, PractitionerDtoConverter dtoConverter) {
		super(smartServiceConfiguration, smartClientCredentialService, fhirContext, dtoConverter);
	}

	@Override
	protected String getDefaultSystem() {
		return "IRMA";
	}

	@Override
	protected String getResourceName() {
		return "Practitioner";
	}


}
