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
import nl.koppeltaal.spring.boot.starter.smartservice.dto.DeviceDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.DeviceDtoConverter;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.ResourceType;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class DeviceFhirClientService extends BaseFhirClientCrudService<DeviceDto, Device> {

	public DeviceFhirClientService(SmartServiceConfiguration smartServiceConfiguration, SmartClientCredentialService smartClientCredentialService, FhirContext fhirContext, DeviceDtoConverter deviceDtoConverter, AuditEventFhirClientService auditEventService) {
		super(smartServiceConfiguration, smartClientCredentialService, fhirContext, deviceDtoConverter, auditEventService);
	}

	@Override
	protected String getResourceName() {
		return ResourceType.Device.name();
	}

	protected String getDefaultSystem() {
		return "device-no-identifier";
	}

}
