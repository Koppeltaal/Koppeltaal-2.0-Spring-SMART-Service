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
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import ca.uhn.fhir.rest.param.TokenParam;
import com.auth0.jwk.JwkException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import nl.koppeltaal.spring.boot.starter.smartservice.configuration.SmartServiceConfiguration;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.ActivityDefinitionDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.ActivityDefinitionDtoConverter;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.ActivityDefinition;
import org.hl7.fhir.r4.model.codesystems.PublicationStatus;
import org.springframework.stereotype.Service;

import static org.hl7.fhir.r4.model.codesystems.PublicationStatus.RETIRED;

/**
 *
 */
@Service
public class ActivityDefinitionFhirClientService extends BaseFhirClientCrudService<ActivityDefinitionDto, ActivityDefinition> {

	public ActivityDefinitionFhirClientService(SmartServiceConfiguration smartServiceConfiguration, SmartClientCredentialService smartClientCredentialService, FhirContext fhirContext, ActivityDefinitionDtoConverter activityDefinitionDtoConverter, AuditEventFhirClientService auditEventService) {
		super(smartServiceConfiguration, smartClientCredentialService, fhirContext, activityDefinitionDtoConverter, auditEventService);
	}

	public List<ActivityDefinition> getResourcesForPatient(String patientReference) throws IOException, JwkException {
		ICriterion<TokenClientParam> criterion = ActivityDefinition.STATUS.exactly().code(StringUtils.lowerCase(PublicationStatus.ACTIVE.name()));
		return getResources(criterion);
	}



	@Override
	protected String getDefaultSystem() {
		return "http:/vzvz.nl/artifacts";
	}

	@Override
	protected String getResourceName() {
		return "ActivityDefinition";
	}

	@Override
	protected Map<String, List<IQueryParameterType>> getEndOfLifeExclusion() {
		return Map.of(ActivityDefinition.SP_STATUS + ":not", List.of(new TokenParam("retired"))); //ActivityDefinition.ActivityDefinitionStatus does not exist
	}

}
