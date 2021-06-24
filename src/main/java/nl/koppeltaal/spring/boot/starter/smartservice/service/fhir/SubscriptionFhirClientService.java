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
import nl.koppeltaal.spring.boot.starter.smartservice.dto.SubscriptionDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.SubscriptionDtoConverter;
import org.hl7.fhir.r4.model.Subscription;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class SubscriptionFhirClientService extends BaseFhirClientService<SubscriptionDto, Subscription> {

	public SubscriptionFhirClientService(SmartServiceConfiguration smartServiceConfiguration, SmartClientCredentialService smartClientCredentialService, FhirContext fhirContext, SubscriptionDtoConverter subscriptionDtoConverter, AuditEventFhirClientService auditEventService) {
		super(smartServiceConfiguration, smartClientCredentialService, fhirContext, subscriptionDtoConverter, auditEventService);
	}

	@Override
	protected String getResourceName() {
		return "Subscription";
	}
	protected String getDefaultSystem() {
		return "subscription-no-identifier";
	}


}
