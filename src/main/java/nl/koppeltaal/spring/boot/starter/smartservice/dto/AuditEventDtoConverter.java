/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.spring.boot.starter.smartservice.dto;

import nl.koppeltaal.spring.boot.starter.smartservice.dto.AuditEventDto.AuditEventSubType;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.AuditEventDto.AuditEventType;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.AuditEvent.AuditEventEntityComponent;
import org.hl7.fhir.r4.model.AuditEvent.AuditEventSourceComponent;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 *
 */
@Component
public class AuditEventDtoConverter implements DtoConverter<AuditEventDto, AuditEvent> {

	@Override
	public void applyDto(AuditEvent auditEvent, AuditEventDto dto) {
		throw new UnsupportedOperationException("applyDto not supported");
	}

	@Override
	public AuditEventDto convert(AuditEvent auditEvent) {
		final AuditEventDto dto = new AuditEventDto();
		dto.setReference(getRelativeReference(auditEvent.getIdElement()));
		Coding type = auditEvent.getType();
		AuditEventType auditEventType = AuditEventType.newAuditEventType(type.getCode(), type.getSystem(), type.getDisplay());
		dto.setType(auditEventType);

		final List<Coding> subtype = auditEvent.getSubtype();
		if (!CollectionUtils.isEmpty(subtype)) {
			Coding subType = auditEvent.getSubtypeFirstRep();
			AuditEventSubType auditEventSubType = AuditEventSubType.newAuditEventSubType(subType.getCode(), subType.getSystem(), subType.getDisplay());
			dto.setSubType(auditEventSubType);
		}

		final AuditEventSourceComponent source = auditEvent.getSource();
		dto.setSource(source.getObserver().getReference());
		List<AuditEvent.AuditEventAgentComponent> agent = auditEvent.getAgent();
		if (!agent.isEmpty()) {
			AuditEvent.AuditEventAgentComponent component = agent.get(0);
			dto.setAction(component.getWho().getReference());
		}

		final List<AuditEventEntityComponent> entity = auditEvent.getEntity();
		boolean first = true;
		for (AuditEventEntityComponent component : entity) {
			Reference what = component.getWhat();
			dto.addEntityWhat(what.getReference());
			if (first) {
				byte[] query = component.getQuery();
				if (query != null && query.length > 0) {
					dto.setQuery(new String(query, StandardCharsets.UTF_8));
				}
				dto.setEntityType(what.getType());
				first = false;
			}
		}
		dto.setRecorded(auditEvent.getRecorded());
		AuditEvent.AuditEventAction action = auditEvent.getAction();
		if (action != null) {
			dto.setAction(action.getDisplay());
		}

		AuditEvent.AuditEventOutcome outcome = auditEvent.getOutcome();
		if (outcome != null) {
			dto.setOutcome(outcome.getDisplay());
		}

		dto.setTraceId(getExtensionValue(auditEvent, "http://koppeltaal.nl/fhir/StructureDefinition/trace-id"));
		dto.setSpanId(getExtensionValue(auditEvent, "http://koppeltaal.nl/fhir/StructureDefinition/request-id"));
		dto.setParentSpanId(getExtensionValue(auditEvent, "http://koppeltaal.nl/fhir/StructureDefinition/correlation-id"));

		return dto;
	}

	@Override
	public AuditEvent convert(AuditEventDto dto) {
		final AuditEvent auditEvent = new AuditEvent();

		applyDto(auditEvent, dto);

		return auditEvent;
	}

	private String getExtensionValue(AuditEvent auditEvent, String name) {
		Extension extension = auditEvent.getExtensionByUrl(name);
		if (extension != null) {
			Type value = extension.getValue();
			if (value instanceof IdType) {
				IdType idType = (IdType) value;
				return idType.getValue();
			}
		}
		return null;
	}

	private List<Coding> getSubType(AuditEventDto dto) {
		AuditEventSubType subType = dto.getSubType();

		return Collections.singletonList(
				new Coding(subType.getSystem(), subType.getCode(), subType.getDisplay())
		);
	}

	private Coding getType(AuditEventDto dto) {
		AuditEventType type = dto.getType();
		return new Coding(type.getSystem(), type.getCode(), type.getDisplay());
	}
}
