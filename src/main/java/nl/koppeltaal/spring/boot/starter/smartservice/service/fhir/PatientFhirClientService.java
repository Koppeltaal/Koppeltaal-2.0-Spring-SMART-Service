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
import nl.koppeltaal.spring.boot.starter.smartservice.dto.PatientDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.PatientDtoConverter;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class PatientFhirClientService extends BaseFhirClientCrudService<PatientDto, Patient> {

	public PatientFhirClientService(SmartServiceConfiguration smartServiceConfiguration, SmartClientCredentialService smartClientCredentialService, FhirContext fhirContext, PatientDtoConverter patientDtoConverter, AuditEventFhirClientService auditEventService) {
		super(smartServiceConfiguration, smartClientCredentialService, fhirContext, patientDtoConverter, auditEventService);
	}

	@Override
	protected String getResourceName() {
		return "Patient";
	}

	@Override
	protected Map<String, List<IQueryParameterType>> getEndOfLifeExclusion() {
		return Map.of(Patient.ACTIVE.getParamName(), List.of(new TokenParam("true")));
	}

	protected String getDefaultSystem() {
		return "https://irma.app/email";
	}


}
