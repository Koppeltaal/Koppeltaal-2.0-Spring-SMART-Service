/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.spring.boot.starter.smartservice.dto;

import java.util.List;
import java.util.stream.Collectors;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Endpoint;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class EndpointDtoConverter implements DtoConverter<EndpointDto, Endpoint> {

	private final CodingDtoConverter codingConverter;
	private final CodeableConceptDtoConverter codeableConceptConverter;

	public EndpointDtoConverter(CodingDtoConverter codingConverter,
			CodeableConceptDtoConverter codeableConceptConverter) {
		this.codingConverter = codingConverter;
		this.codeableConceptConverter = codeableConceptConverter;
	}

	public void applyDto(Endpoint endpoint, EndpointDto endpointDto) {
		setId(endpoint, endpointDto);
		// TODO: implement the rest
		endpoint.setAddress(endpointDto.getAddress());
		endpoint.setName(endpointDto.getName());
		endpoint.setStatus(Endpoint.EndpointStatus.fromCode(endpointDto.getStatus()));

		endpoint.setConnectionType(codingConverter.convert(endpointDto.getConnectionType()));
		endpoint.setPayloadType(codeableConceptConverter.convertDtos(endpointDto.getPayloadType()));
	}



	public void applyResource(EndpointDto endpointDto, Endpoint endpoint) {
		endpointDto.setReference(getRelativeReference(endpoint.getIdElement()));
		endpointDto.setAddress(endpoint.getAddress());
		endpointDto.setName(endpoint.getName());
		endpointDto.setStatus(endpoint.getStatus().toCode());
		endpointDto.setConnectionType(codingConverter.convert(endpoint.getConnectionType()));
		endpointDto.setPayloadType(codeableConceptConverter.convert(endpoint.getPayloadType()));
	}

	public EndpointDto convert(Endpoint endpoint) {
		EndpointDto endpointDto = new EndpointDto();

		applyResource(endpointDto, endpoint);


		return endpointDto;
	}

	public Endpoint convert(EndpointDto endpointDto) {
		Endpoint endpoint = new Endpoint();

		applyDto(endpoint, endpointDto);
		return endpoint;
	}


}
