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
import nl.koppeltaal.spring.boot.starter.smartservice.dto.SubscriptionDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.SubscriptionDtoConverter;
import org.hl7.fhir.r4.model.Subscription;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *
 */
@Service
public class SubscriptionFhirClientService extends BaseFhirClientCrudService<SubscriptionDto, Subscription> {

	public SubscriptionFhirClientService(SmartServiceConfiguration smartServiceConfiguration, SmartClientCredentialService smartClientCredentialService, FhirContext fhirContext, SubscriptionDtoConverter subscriptionDtoConverter, AuditEventFhirClientService auditEventService) {
		super(smartServiceConfiguration, smartClientCredentialService, fhirContext, subscriptionDtoConverter, auditEventService);
	}

	@Override
	protected String getResourceName() {
		return "Subscription";
	}

	@Override
	protected Map<String, List<IQueryParameterType>> getEndOfLifeExclusion() {
		return Map.of(Subscription.SP_STATUS + ":not", List.of(new TokenParam(Subscription.SubscriptionStatus.OFF.toCode())));
	}

	protected String getDefaultSystem() {
		return "subscription-no-identifier";
	}


}
