/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.spring.boot.starter.smartservice.dto;

import java.util.ArrayList;
import java.util.List;
import org.hl7.fhir.r4.model.CareTeam.CareTeamStatus;

/**
 *
 */
@SuppressWarnings("unused")
public class CareTeamDto extends BaseIdentifierDto {
	String name;
	CareTeamStatus status;
	List<String> participants = new ArrayList<String>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CareTeamStatus getStatus() {
		return status;
	}

	public void setStatus(CareTeamStatus status) {
		this.status = status;
	}

	public List<String> getParticipants() {
		return participants;
	}

	public void setParticipants(
			List<String> participants) {
		this.participants = participants;
	}

	@Override
	public String toString() {
		return "CareTeamDto{" +
				"name='" + name + '\'' +
				", status=" + status +
				", participants=" + participants +
				"} " + super.toString();
	}
}
