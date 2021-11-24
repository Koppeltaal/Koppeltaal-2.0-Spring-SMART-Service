/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.spring.boot.starter.smartservice.dto;

import static nl.koppeltaal.spring.boot.starter.smartservice.constants.FhirConstant.CODING_SNOMED__HEALTHCARE_PROFESSIONAL;
import static nl.koppeltaal.spring.boot.starter.smartservice.constants.FhirConstant.CODING__SNOMED__PERSON;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.hl7.fhir.r4.model.CareTeam;
import org.hl7.fhir.r4.model.CareTeam.CareTeamParticipantComponent;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class CareTeamDtoConverter implements DtoConverter<CareTeamDto, CareTeam> {

	public void applyDto(CareTeam careTeam, CareTeamDto careTeamDto) {
		setId(careTeam, careTeamDto);
		careTeam.setIdentifier(Collections.singletonList(createIdentifier(careTeamDto.getIdentifierSystem(), careTeamDto.getIdentifierValue())));

		careTeam.setName(careTeamDto.getName());
		careTeam.setStatus(careTeamDto.getStatus());

		careTeam.setParticipant(new ArrayList<>());
		for (String participantReference : careTeamDto.getParticipants()) {

			final CareTeamParticipantComponent participantComponent = new CareTeamParticipantComponent();

			participantComponent.setMember(new Reference(participantReference));

			final CodeableConcept role = new CodeableConcept();
			role.addCoding(participantReference.startsWith("Practitioner") ? CODING_SNOMED__HEALTHCARE_PROFESSIONAL : CODING__SNOMED__PERSON);

			participantComponent.setRole(Collections.singletonList(role));

			careTeam.addParticipant(participantComponent);
		}
	}

	public void applyResource(CareTeamDto careTeamDto, CareTeam careTeam) {
		careTeamDto.setReference(getRelativeReference(careTeam.getIdElement()));

		List<Identifier> identifiers = careTeam.getIdentifier();
		for (Identifier identifier : identifiers) {
			careTeamDto.setIdentifierSystem(identifier.getSystem());
			careTeamDto.setIdentifierValue(identifier.getValue());
		}

		careTeamDto.setName(careTeam.getName());
		careTeamDto.setStatus(careTeam.getStatus());

		final List<String> participantReferences = careTeam.getParticipant().stream()
				.map(participantComponent -> participantComponent.getMember().getReference())
				.collect(Collectors.toList());
		careTeamDto.setParticipants(participantReferences);
	}

	public CareTeamDto convert(CareTeam careTeam) {
		CareTeamDto careTeamDto = new CareTeamDto();

		applyResource(careTeamDto, careTeam);

		return careTeamDto;
	}

	public CareTeam convert(CareTeamDto careTeamDto) {
		CareTeam careTeam = new CareTeam();

		applyDto(careTeam, careTeamDto);
		return careTeam;
	}


}
