/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.spring.boot.starter.smartservice.dto;

import nl.koppeltaal.spring.boot.starter.smartservice.utils.ExtensionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static nl.koppeltaal.spring.boot.starter.smartservice.constants.FhirConstant.KT2_EXTENSION__CARE_TEAM__OBSERVER;
import static nl.koppeltaal.spring.boot.starter.smartservice.constants.FhirConstant.KT2_EXTENSION__TASK__INSTANTIATES;

/**
 *
 */
@Component
public class TaskDtoConverter implements DtoConverter<TaskDto, Task> {

	public void applyDto(Task task, TaskDto taskDto) {
		setId(task, taskDto);
		task.setIdentifier(
				Collections.singletonList(createIdentifier(taskDto.getIdentifierSystem(), taskDto.getIdentifierValue())));
		task.setRequester(new Reference(taskDto.getPractitioner()));
		task.setOwner(new Reference(taskDto.getPatient()));
		addInstantiatesExtension(task, taskDto.getActivityDefinition());
		task.setStatus(Task.TaskStatus.fromCode(taskDto.getStatus()));

		// remove all "old" observer values
		final List<Extension> extensionsToKeep = task.getExtensionsByUrl(
						KT2_EXTENSION__CARE_TEAM__OBSERVER).stream()
				.filter(extension -> !StringUtils.equals(KT2_EXTENSION__CARE_TEAM__OBSERVER, extension.getUrl()))
				.collect(Collectors.toList());
		task.setExtension(extensionsToKeep);

		taskDto.getObserverReferences().forEach((observerReference) ->
				addObserverExtension(task, observerReference)
		);
	}

	public static void addObserverExtension(Task task, String observerReference) {
		final Reference careTeamReference = new Reference();
		careTeamReference.setReference(observerReference);
		careTeamReference.setType(ResourceType.CareTeam.name());

		final Extension observerExtension = new Extension();
		observerExtension.setValue(careTeamReference);
		observerExtension.setUrl(KT2_EXTENSION__CARE_TEAM__OBSERVER);

		task.addExtension(observerExtension);
	}

	public static void addInstantiatesExtension(Task task, String activityDefinitionReference) {
		final Reference instantiatesReference = new Reference();
		instantiatesReference.setReference(activityDefinitionReference);
		instantiatesReference.setType(ResourceType.ActivityDefinition.name());

		final Extension instantiatesExtension = new Extension();
		instantiatesExtension.setValue(instantiatesReference);
		instantiatesExtension.setUrl(KT2_EXTENSION__TASK__INSTANTIATES);

		task.addExtension(instantiatesExtension);
	}

	public void applyResource(TaskDto taskDto, Task task) {
		taskDto.setReference(getRelativeReference(task.getIdElement()));

		List<Identifier> identifiers = task.getIdentifier();
		for (Identifier identifier : identifiers) {
			taskDto.setIdentifierSystem(identifier.getSystem());
			taskDto.setIdentifierValue(identifier.getValue());
		}

		taskDto.setPatient(task.getOwner().getReference());
		taskDto.setPractitioner(task.getRequester().getReference());
		if (task.getStatus() != null)
			taskDto.setStatus(task.getStatus().toCode());

		task.getExtensionsByUrl(KT2_EXTENSION__CARE_TEAM__OBSERVER).forEach(extension ->
			taskDto.getObserverReferences().add(((Reference) extension.getValue()).getReference())
		);

		ExtensionUtils.getReferenceValue(task, KT2_EXTENSION__TASK__INSTANTIATES)
				.ifPresent(taskDto::setActivityDefinition);
	}

	public TaskDto convert(Task task) {
		TaskDto taskDto = new TaskDto();
		applyResource(taskDto, task);
		return taskDto;
	}

	public Task convert(TaskDto taskDto) {
		Task task = new Task();
		applyDto(task, taskDto);
		return task;
	}

}
