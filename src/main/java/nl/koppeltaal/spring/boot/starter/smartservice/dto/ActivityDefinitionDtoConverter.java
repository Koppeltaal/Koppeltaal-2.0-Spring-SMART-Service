/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.spring.boot.starter.smartservice.dto;

import static nl.koppeltaal.spring.boot.starter.smartservice.dto.ActivityDefinitionDto.EXTENSION__PUBLISHER_IDENTIFIER;

import java.util.Collections;
import java.util.List;
import org.hl7.fhir.r4.model.ActivityDefinition;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class ActivityDefinitionDtoConverter implements DtoConverter<ActivityDefinitionDto, ActivityDefinition> {

	public void applyDto(ActivityDefinition activityDefinition, ActivityDefinitionDto activityDefinitionDto) {
		setId(activityDefinition, activityDefinitionDto);
		activityDefinition.setIdentifier(Collections.singletonList(createIdentifier(activityDefinitionDto.getIdentifierSystem(), activityDefinitionDto.getIdentifierValue())));
		activityDefinition.setName(activityDefinitionDto.getName());
		activityDefinition.setTitle(activityDefinitionDto.getTitle());
		activityDefinition.setUrl(activityDefinitionDto.getUrl());
		activityDefinition.setStatus(Enumerations.PublicationStatus.fromCode(activityDefinitionDto.getStatus()));
		activityDefinition.setDescription(activityDefinitionDto.getDescription());
		activityDefinition.setExtension(getExtensions(activityDefinitionDto));

		activityDefinition.setKind(ActivityDefinition.ActivityDefinitionKind.fromCode(activityDefinitionDto.getKind()));

		Reference value = new Reference(activityDefinitionDto.getLocation());
		value.setType("ActivityDefinition");
		activityDefinition.setLocation(value);
	}

	private List<Extension> getExtensions(ActivityDefinitionDto activityDefinitionDto) {
		return Collections.singletonList(new Extension(EXTENSION__PUBLISHER_IDENTIFIER, new StringType(activityDefinitionDto.getPublisherIdentifier())));
	}


	public void applyResource(ActivityDefinitionDto activityDefinitionDto, ActivityDefinition activityDefinition) {

		activityDefinitionDto.setReference(getRelativeReference(activityDefinition.getIdElement()));

		List<Identifier> identifiers = activityDefinition.getIdentifier();
		for (Identifier identifier : identifiers) {
			activityDefinitionDto.setIdentifierSystem(identifier.getSystem());
			activityDefinitionDto.setIdentifierValue(identifier.getValue());
		}

		activityDefinitionDto.setName(activityDefinition.getName());
		activityDefinitionDto.setTitle(activityDefinition.getTitle());
		activityDefinitionDto.setUrl(activityDefinition.getUrl());
		Enumerations.PublicationStatus status = activityDefinition.getStatus();
		activityDefinitionDto.setStatus(status != null ? status.toCode() : null);

		activityDefinitionDto.setDescription(activityDefinition.getDescription());
		ActivityDefinition.ActivityDefinitionKind kind = activityDefinition.getKind();
		activityDefinitionDto.setKind(kind != null ? kind.toCode() : null);
		Reference location = activityDefinition.getLocation();
		activityDefinitionDto.setLocation(location != null ? location.getReference() : null);

		convertExtensions(activityDefinition.getExtension(), activityDefinitionDto);

	}

	private void convertExtensions(List<Extension> extensions, ActivityDefinitionDto activityDefinitionDto) {
		extensions.forEach(extension -> {
			switch (extension.getUrl()) {
				case EXTENSION__PUBLISHER_IDENTIFIER:
					activityDefinitionDto.setPublisherIdentifier(extension.getValue().toString());
			}
		});
	}

	public ActivityDefinitionDto convert(ActivityDefinition activityDefinition) {
		ActivityDefinitionDto activityDefinitionDto = new ActivityDefinitionDto();

		applyResource(activityDefinitionDto, activityDefinition);


		return activityDefinitionDto;
	}

	public ActivityDefinition convert(ActivityDefinitionDto activityDefinitionDto) {
		ActivityDefinition activityDefinition = new ActivityDefinition();

		applyDto(activityDefinition, activityDefinitionDto);
		return activityDefinition;
	}

}
