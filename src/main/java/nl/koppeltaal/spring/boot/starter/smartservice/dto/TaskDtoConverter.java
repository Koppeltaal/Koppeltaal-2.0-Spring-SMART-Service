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
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.Task;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class TaskDtoConverter implements DtoConverter<TaskDto, Task> {

	public static final String KT2_PROFILE_EXTENSION__CARE_TEAM__OBSERVER = "http://koppeltaal.nl/fhir/StructureDefinition/KT2ObservationTeam";
	public static final String KT2_PROFILE_TASK = "http://koppeltaal.nl/fhir/StructureDefinition/KT2Task";

	public void applyDto(Task task, TaskDto taskDto) {
		setId(task, taskDto);
		task.setRequester(new Reference(taskDto.getPractitioner()));
		task.setOwner(new Reference(taskDto.getPatient()));
		task.setInstantiatesCanonical(taskDto.getActivityDefinition());
		task.setStatus(Task.TaskStatus.fromCode(taskDto.getStatus()));

		// remove all "old" observer values
		final List<Extension> extensionsToKeep = task.getExtensionsByUrl(KT2_PROFILE_EXTENSION__CARE_TEAM__OBSERVER).stream()
				.filter(extension -> !StringUtils.equals(KT2_PROFILE_EXTENSION__CARE_TEAM__OBSERVER, extension.getUrl()))
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
		observerExtension.setUrl(KT2_PROFILE_EXTENSION__CARE_TEAM__OBSERVER);

		task.addExtension(observerExtension);
	}

	public void applyResource(TaskDto taskDto, Task task) {
		taskDto.setReference(getRelativeReference(task.getIdElement()));
		taskDto.setPatient(task.getOwner().getReference());
		taskDto.setPractitioner(task.getRequester().getReference());
		taskDto.setActivityDefinition(task.getInstantiatesCanonical());
		if (task.getStatus() != null)
			taskDto.setStatus(task.getStatus().toCode());

		task.getExtensionsByUrl(KT2_PROFILE_EXTENSION__CARE_TEAM__OBSERVER).forEach(extension ->
			taskDto.getObserverReferences().add(((Reference) extension.getValue()).getReference())
		);
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
