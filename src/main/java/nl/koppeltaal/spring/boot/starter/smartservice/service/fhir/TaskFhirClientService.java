/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.spring.boot.starter.smartservice.service.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;
import com.auth0.jwk.JwkException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import nl.koppeltaal.spring.boot.starter.smartservice.configuration.SmartServiceConfiguration;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.TaskDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.TaskDtoConverter;
import nl.koppeltaal.spring.boot.starter.smartservice.utils.ResourceUtils;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.ActivityDefinition;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.Task;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class TaskFhirClientService extends BaseFhirClientService<TaskDto, Task> {

	public TaskFhirClientService(SmartServiceConfiguration smartServiceConfiguration, SmartClientCredentialService smartClientCredentialService, FhirContext fhirContext, TaskDtoConverter taskDtoConverter, AuditEventService auditEventService) {
		super(smartServiceConfiguration, smartClientCredentialService, fhirContext, taskDtoConverter, auditEventService);
	}

	public Task getOrCreateTask(Patient patient, Practitioner practitioner, ActivityDefinition activityDefinition, boolean forceNew) throws IOException, JwkException {
		List<Task> tasks =  Collections.emptyList();
		if (!forceNew) {
			tasks = getTasksForOwnerAndDefinition(patient, activityDefinition);
		}
		Task task;
		if (tasks.isEmpty()) {
			task = new Task();
			task.setOwner(buildReference(patient));
			if (practitioner != null) {
				task.setRequester(buildReference(practitioner));
			}
			task.setStatus(Task.TaskStatus.READY);
			task.setIntent(Task.TaskIntent.ORDER);
			task.getRestriction().addRecipient(buildReference(practitioner));
			task.getExecutionPeriod().setStart(new Date());
			task.setInstantiatesCanonical(ResourceUtils.getReference(activityDefinition));
			task = storeResource(task);
		} else {
			task = tasks.get(0);
		}
		if (task.getStatus() == null) {
			task.setStatus(Task.TaskStatus.REQUESTED);
		}
		return task;
	}

	private Reference buildReference(Resource resource) {
		if (resource == null) return null;
		return new Reference(getRelativeReference(resource.getIdElement()));
	}


	private Reference buildReference(Patient patient) {
		if (patient == null) return null;
		return new Reference(getRelativeReference(patient.getIdElement()));
	}


	private String getRelativeReference(IIdType idElement) {
		return idElement.toUnqualifiedVersionless().getValue();
	}

	public List<Task> getResourcesByOwner(String ownerReference) throws IOException, JwkException {
		List<Task> rv = new ArrayList<>();
		ICriterion<ReferenceClientParam> criterion = Task.OWNER.hasId(getIdFromReference(ownerReference));
		Bundle bundle = getFhirClient().search().forResource(getResourceName()).where(criterion).returnBundle(Bundle.class).execute();
		for (Bundle.BundleEntryComponent component : bundle.getEntry()) {
			Task resource = (Task) component.getResource();
			rv.add(resource);
		}
		return rv;
	}

	private IdType getIdFromReference(String ownerReference) {
		if (StringUtils.contains(ownerReference, "/")) {
			// Strip of the Patient/ from Patient/1
			ownerReference = StringUtils.substringAfterLast(ownerReference, "/");
		}
		return new IdType(ownerReference);
	}

	protected String getDefaultSystem() {
		return "system";
	}

	@Override
	protected String getResourceName() {
		return "Task";
	}

	private List<Task> getTasksForOwnerAndDefinition(Patient fhirPatient, ActivityDefinition fhirDefinition) throws IOException, JwkException {
		List<Task> rv = new ArrayList<>();
		List<Task> resourcesByOwner = getResourcesByOwner(ResourceUtils.getReference(fhirPatient));
		for (Task task : resourcesByOwner) {
			if (StringUtils.equals(task.getInstantiatesCanonical(), ResourceUtils.getReference(fhirDefinition))) {
				rv.add(task);
			}
		}
		return rv;
	}


}
