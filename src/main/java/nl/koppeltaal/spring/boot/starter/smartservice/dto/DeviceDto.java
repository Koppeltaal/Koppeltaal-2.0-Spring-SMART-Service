/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.spring.boot.starter.smartservice.dto;

import org.hl7.fhir.r4.model.Device.FHIRDeviceStatus;

/**
 *
 */
@SuppressWarnings("unused")
public class DeviceDto extends BaseIdentifierDto {
	String name;
	FHIRDeviceStatus status;

	public FHIRDeviceStatus getStatus() {
		return status;
	}

	public void setStatus(FHIRDeviceStatus status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
