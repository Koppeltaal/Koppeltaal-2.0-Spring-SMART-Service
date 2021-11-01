/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.spring.boot.starter.smartservice.dto;

/**
 *
 */
@SuppressWarnings("unused")
public class ActivityDefinitionDto extends BaseIdentifierDto {

	public final static String EXTENSION__PUBLISHER_IDENTIFIER = "https://koppeltaal.nl/publisher-identifier";
	public final static String EXTENSION__ENDPOINT = "http://koppeltaal.nl/fhir/StructureDefinition/KT2EndpointExtension";

	String name;
	String title;
	String url;
	String status;
	String description;
	String kind;
	String endpoint;
	String code;
	String topic;
	String publisherIdentifier;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPublisherIdentifier() {
		return publisherIdentifier;
	}

	public void setPublisherIdentifier(String publisherIdentifier) {
		this.publisherIdentifier = publisherIdentifier;
	}
}
