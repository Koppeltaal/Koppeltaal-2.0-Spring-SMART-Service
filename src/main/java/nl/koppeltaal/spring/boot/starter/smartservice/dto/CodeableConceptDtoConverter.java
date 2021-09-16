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
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class CodeableConceptDtoConverter {

	private final CodingDtoConverter codingConverter;

	public CodeableConceptDtoConverter(CodingDtoConverter codingConverter) {
		this.codingConverter = codingConverter;
	}

	public void applyDto(CodeableConcept codeableConcept, CodeableConceptDto codeableConceptDto) {
		final List<Coding> codings = codeableConceptDto.getCodings().stream()
				.map(codingConverter::convert)
				.collect(Collectors.toList());

		codeableConcept.setCoding(codings);
	}

	public CodeableConceptDto convert(CodeableConcept codeableConcept) {

		final CodeableConceptDto codeableConceptDto = new CodeableConceptDto();

		for (Coding coding : codeableConcept.getCoding()) {
			codeableConceptDto.addCoding(codingConverter.convert(coding));
		}

		return codeableConceptDto;
	}


	public CodeableConcept convert(CodeableConceptDto codeableConceptDto) {
		CodeableConcept codeableConcept = new CodeableConcept();

		applyDto(codeableConcept, codeableConceptDto);

		return codeableConcept;
	}

	public List<CodeableConceptDto> convert(List<CodeableConcept> codeableConcepts) {
		return codeableConcepts.stream()
				.map(this::convert)
				.collect(Collectors.toList());
	}

	public List<CodeableConcept> convertDtos(List<CodeableConceptDto> codeableConceptDtos) {
		return codeableConceptDtos.stream()
				.map(this::convert)
				.collect(Collectors.toList());
	}
}
