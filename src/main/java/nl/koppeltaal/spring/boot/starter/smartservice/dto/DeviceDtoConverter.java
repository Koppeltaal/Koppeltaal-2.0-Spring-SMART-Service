/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.spring.boot.starter.smartservice.dto;

import java.util.Collections;
import java.util.List;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.Device.DeviceDeviceNameComponent;
import org.hl7.fhir.r4.model.Identifier;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class DeviceDtoConverter implements DtoConverter<DeviceDto, Device> {


	public void applyDto(Device device, DeviceDto deviceDto) {
		setId(device, deviceDto);
		device.setIdentifier(Collections.singletonList(createIdentifier(deviceDto.getIdentifierSystem(), deviceDto.getIdentifierValue())));

		final DeviceDeviceNameComponent deviceName = new DeviceDeviceNameComponent();
		deviceName.setName(deviceDto.getName());
		device.setDeviceName(Collections.singletonList(deviceName));
		device.setStatus(deviceDto.getStatus());
	}

	public void applyResource(DeviceDto deviceDto, Device device) {
		deviceDto.setReference(getRelativeReference(device.getIdElement()));

		List<Identifier> identifiers = device.getIdentifier();
		for (Identifier identifier : identifiers) {
			deviceDto.setIdentifierSystem(identifier.getSystem());
			deviceDto.setIdentifierValue(identifier.getValue());
		}

		deviceDto.setName(device.getDeviceNameFirstRep().getName());
		deviceDto.setStatus(deviceDto.getStatus());
	}

	public DeviceDto convert(Device organization) {
		DeviceDto organizationDto = new DeviceDto();

		applyResource(organizationDto, organization);


		return organizationDto;
	}

	public Device convert(DeviceDto organizationDto) {
		Device organization = new Device();

		applyDto(organization, organizationDto);
		return organization;
	}


}
