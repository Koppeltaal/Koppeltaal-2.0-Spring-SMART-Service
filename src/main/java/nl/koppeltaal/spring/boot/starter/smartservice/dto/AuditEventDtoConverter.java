/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.spring.boot.starter.smartservice.dto;

import nl.koppeltaal.spring.boot.starter.smartservice.dto.AuditEventDto.AuditEventType;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.Coding;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class AuditEventDtoConverter implements DtoConverter<AuditEventDto, AuditEvent> {

	@Override
	public void applyDto(AuditEvent auditEvent, AuditEventDto dto) {
		setId(auditEvent, dto);
		auditEvent.setType(getType(dto));
		auditEvent.setRecorded(dto.getRecorded());
		auditEvent.setAgent(dto.getAgent());
		auditEvent.setSource(dto.getSource());
		auditEvent.setEntity(dto.getEntity());
	}

	@Override
	public AuditEventDto convert(AuditEvent auditEvent) {
		final AuditEventDto dto = new AuditEventDto();

		dto.setReference(getRelativeReference(auditEvent.getIdElement()));

		AuditEventType auditEventType = AuditEventType.byDisplay(auditEvent.getType().getDisplay());
		dto.setType(auditEventType);

		dto.setRecorded(auditEvent.getRecorded());
		dto.setAgent(dto.getAgent());
		dto.setSource(dto.getSource());
		dto.setEntity(dto.getEntity());

		return dto;
	}

	@Override
	public AuditEvent convert(AuditEventDto dto) {
		final AuditEvent auditEvent = new AuditEvent();

		applyDto(auditEvent, dto);

		return auditEvent;
	}

	private Coding getType(AuditEventDto dto) {
		final AuditEventType type = dto.getType();
		return new Coding(type.getSystem(), type.getCode(), type.getDisplay());
	}
}
