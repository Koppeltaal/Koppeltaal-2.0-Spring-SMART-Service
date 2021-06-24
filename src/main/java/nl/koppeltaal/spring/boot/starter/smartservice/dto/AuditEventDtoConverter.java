/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.spring.boot.starter.smartservice.dto;

import java.util.Collections;
import java.util.List;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.AuditEventDto.AuditEventSubType;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.AuditEventDto.AuditEventType;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.AuditEvent.AuditEventEntityComponent;
import org.hl7.fhir.r4.model.AuditEvent.AuditEventSourceComponent;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 *
 */
@Component
public class AuditEventDtoConverter implements DtoConverter<AuditEventDto, AuditEvent> {

	@Override
	public void applyDto(AuditEvent auditEvent, AuditEventDto dto) {
		setId(auditEvent, dto);
		auditEvent.setType(getType(dto));
		auditEvent.setSubtype(getSubType(dto));
		auditEvent.setRecorded(dto.getRecorded());

		final AuditEventEntityComponent entity = new AuditEventEntityComponent();
		entity.setWhat(new Reference(dto.getEntityWhat()));
		auditEvent.setEntity(Collections.singletonList(entity));
	}

	@Override
	public AuditEventDto convert(AuditEvent auditEvent) {
		final AuditEventDto dto = new AuditEventDto();

		dto.setReference(getRelativeReference(auditEvent.getIdElement()));

		AuditEventType auditEventType = AuditEventType.byDisplay(auditEvent.getType().getDisplay());
		dto.setType(auditEventType.getDisplay());

		final List<Coding> subtype = auditEvent.getSubtype();
		if(!CollectionUtils.isEmpty(subtype)) {
			AuditEventSubType auditEventSubType = AuditEventSubType.byDisplay(subtype.get(0).getDisplay());
			dto.setSubType(auditEventSubType.getDisplay());
		}

		final AuditEventSourceComponent source = auditEvent.getSource();
		dto.setSource(source.getObserver().getReference());

		final List<AuditEventEntityComponent> entity = auditEvent.getEntity();
		if(!CollectionUtils.isEmpty(entity)) {

			dto.setEntityWhat(entity.get(0).getWhat().getReference());
		}
		dto.setRecorded(auditEvent.getRecorded());

		return dto;
	}

	@Override
	public AuditEvent convert(AuditEventDto dto) {
		final AuditEvent auditEvent = new AuditEvent();

		applyDto(auditEvent, dto);

		return auditEvent;
	}

	private Coding getType(AuditEventDto dto) {
		AuditEventType type = AuditEventType.byDisplay(dto.getType());
		return new Coding(type.getSystem(), type.getCode(), type.getDisplay());
	}

	private List<Coding> getSubType(AuditEventDto dto) {
		AuditEventSubType subType = AuditEventSubType.byDisplay(dto.getType());

		return Collections.singletonList(
				new Coding(subType.getSystem(), subType.getCode(), subType.getDisplay())
		);
	}
}
