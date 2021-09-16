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
import org.hl7.fhir.r4.model.Coding;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class CodingDtoConverter {

	public void applyDto(Coding coding, CodingDto codingDto) {
		coding.setSystem(codingDto.getSystem());
		coding.setCode(codingDto.getCode());
		coding.setDisplay(codingDto.getDisplay());
		coding.setVersion(codingDto.getVersion());
	}

	public CodingDto convert(Coding coding) {
		return new CodingDto(coding);
	}


	public Coding convert(CodingDto codingDto) {
		Coding coding = new Coding();

		applyDto(coding, codingDto);

		return coding;
	}

	public List<CodingDto> convert(List<Coding> codings) {
		return codings.stream()
				.map(this::convert)
				.collect(Collectors.toList());
	}

	public List<Coding> convertDtos(List<CodingDto> codingDtos) {
		return codingDtos.stream()
				.map(this::convert)
				.collect(Collectors.toList());
	}
}
