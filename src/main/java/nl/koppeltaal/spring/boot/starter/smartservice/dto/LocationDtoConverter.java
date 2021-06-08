/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.spring.boot.starter.smartservice.dto;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class LocationDtoConverter implements DtoConverter<LocationDto, Location> {

	public void applyDto(Location location, LocationDto locationDto) {
		setId(location, locationDto);
		location.getIdentifier().clear();
		location.getEndpoint().clear();
		Reference reference = new Reference(locationDto.getEndpoint());
		reference.setType("Endpoint");
		location.addEndpoint(reference);
	}

	public void applyResource(LocationDto locationDto, Location location) {
		locationDto.setReference(getRelativeReference(location.getIdElement()));
		List<Reference> endpoint = location.getEndpoint();
		for (Reference reference : endpoint) {
			if (StringUtils.equals("Endpoint", reference.getType())) {
				locationDto.setEndpoint(reference.getReference());
				break;
			}
		}
	}

	public LocationDto convert(Location location) {
		LocationDto locationDto = new LocationDto();
		applyResource(locationDto, location);
		return locationDto;
	}

	public Location convert(LocationDto locationDto) {
		Location location = new Location();
		applyDto(location, locationDto);
		return location;
	}

}
