/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.spring.boot.starter.smartservice.service.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import com.auth0.jwk.JwkException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import nl.koppeltaal.spring.boot.starter.smartservice.configuration.SmartServiceConfiguration;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.BaseDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.DtoConverter;
import nl.koppeltaal.spring.boot.starter.smartservice.event.AutomatedAuditEvents;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Enumerations.FHIRAllTypes;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@SuppressWarnings("unchecked")
public abstract class BaseFhirClientService<D extends BaseDto, R extends DomainResource> {

	private static final Logger LOG = LoggerFactory.getLogger(AutomatedAuditEvents.class);

	final SmartServiceConfiguration smartServiceConfiguration;
	final SmartClientCredentialService smartClientCredentialService;
	final FhirContext fhirContext;
	final DtoConverter<D, R> dtoConverter;
	final AuditEventFhirClientService auditEventService;

	public BaseFhirClientService(SmartServiceConfiguration smartServiceConfiguration, SmartClientCredentialService smartClientCredentialService, FhirContext fhirContext, DtoConverter<D, R> dtoConverter, AuditEventFhirClientService auditEventService) {
		this.smartServiceConfiguration = smartServiceConfiguration;
		this.smartClientCredentialService = smartClientCredentialService;
		this.fhirContext = fhirContext;
		this.dtoConverter = dtoConverter;
		this.auditEventService = auditEventService;
	}

	public void deleteResource(String id) throws IOException, JwkException {
		final IBaseOperationOutcome execute = getFhirClient().delete()
				.resourceById(getResourceName(), id).execute();

		LOG.info("Deleted entity [{}]", execute.getIdElement());

	}

	public void deleteResourceByReference(String id) throws IOException, JwkException {
		R resource = getResourceByReference(id);
		if (resource != null) {
			final IBaseOperationOutcome execute = getFhirClient().delete().resource(resource).execute();
			LOG.info("Deleted entity [{}]", execute.getIdElement());
		}
	}

	public R getResourceByIdentifier(Identifier identifier) throws IOException, JwkException {
		String system = StringUtils.isNotEmpty(identifier.getSystem()) ? identifier.getSystem() : getDefaultSystem();
		return getResourceByIdentifier(system, identifier.getValue());
	}

	public R getResourceByIdentifier(String identifierValue) throws IOException{
		return getResourceByIdentifier(identifierValue, getDefaultSystem());
	}

	public R getResourceByReference(String reference) throws IOException {
		return (R) getFhirClient().read().resource(getResourceName()).withId(reference).execute();
	}

	public R getResourceByReference(Reference reference) throws IOException, JwkException {
		String ref = reference.getReference();
		if (StringUtils.isNotEmpty(ref)) {
			return getResourceByReference(ref);
		} else if (reference.getIdentifier() != null) {
			return getResourceByIdentifier(reference.getIdentifier());
		}
		return null;
	}

	public List<R> getResources(SortSpec sort) throws  IOException {
		return getResourcesInternal(sort, null);
	}

	public List<R> getResources() throws JwkException, IOException {
		return getResourcesInternal(null, null);
	}

	public List<R> getResources(ICriterion<?> criterion) throws IOException {
		return getResourcesInternal(null, criterion);
	}

	public List<R> getResources(SortSpec sort, ICriterion<?> criterion) throws IOException {
		return getResourcesInternal(sort, criterion);
	}

	private List<R> getResourcesInternal(SortSpec sort, ICriterion<?> criterion) throws IOException {
		List<R> rv = new ArrayList<>();

		final IQuery<Bundle> query = getFhirClient().search().forResource(getResourceName()).returnBundle(Bundle.class);

		if(sort != null) {
			query.sort(sort);
		}

		if(criterion != null) {
			query.where(criterion);
		}

		//FIXME: The server returns paginated results, this client doesn't support pagination,
		// it just transforms the amount returned (fhir store config)
		Bundle bundle = query.execute();

		for (Bundle.BundleEntryComponent component : bundle.getEntry()) {
			R resource = (R) component.getResource();
			rv.add(resource);
		}
		return rv;
	}

	public R storeResource(R resource) throws IOException {
		String identifier = getIdentifier(getDefaultSystem(), resource);
		String id = getId(resource);
		R res = null;
		if (StringUtils.isNotEmpty(id)) {
			res = getResourceByReference(id);
		} else if (StringUtils.isNotEmpty(identifier)) {
			res = getResourceByIdentifier(identifier, getDefaultSystem());
		}


		if (res != null) {
			dtoConverter.applyDto(res, dtoConverter.convert(resource));
			MethodOutcome execute = getFhirClient().update().resource(res).execute();
			final R updatedEntity = (R) execute.getResource();

			LOG.info("Updated entity [{}]", updatedEntity.getIdElement());
			if(!FHIRAllTypes.AUDITEVENT.getDisplay().equals(getResourceName())) {
				auditEventService.registerRestUpdate(updatedEntity);
			}
			return updatedEntity;
		}

		updateMetaElement(resource);
		MethodOutcome execute = getFhirClient().create().resource(resource).execute();
		final R updatedEntity = (R) execute.getResource();
		LOG.info("Created entity [{}]", updatedEntity.getIdElement());

		if(!FHIRAllTypes.AUDITEVENT.getDisplay().equals(getResourceName())) {
			auditEventService.registerRestCreate(updatedEntity);
		}

		return updatedEntity;
	}

	protected abstract String getDefaultSystem();

	protected IGenericClient getFhirClient() throws IOException {

		IGenericClient iGenericClient = fhirContext.newRestfulGenericClient(smartServiceConfiguration.getFhirServerUrl());

		iGenericClient.registerInterceptor(new BearerTokenAuthInterceptor(smartClientCredentialService.getAccessToken()));

		return iGenericClient;


	}

	protected String getId(DomainResource resource) {
		IdType idElement = resource.getIdElement();
		if (!idElement.isEmpty()) {
			return idElement.getIdPart();
		}
		return null;
	}

	protected final String getIdentifier(String system, R resource) {
		try {
			Method getIdentifier = resource.getClass().getDeclaredMethod("getIdentifier");
			List<Identifier> identifiers = (List<Identifier>) getIdentifier.invoke(resource);
			for (Identifier identifier : identifiers) {
				if (StringUtils.equals(identifier.getSystem(), system)) {
					return identifier.getValue();
				}
			}
			return null;
		} catch (NoSuchMethodException | IllegalAccessException e) {
			// Die silently
			return null;
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	protected R getResourceByIdentifier(String identifierValue, String identifierSystem) throws IOException {
		ICriterion<TokenClientParam> criterion = new TokenClientParam("identifier").exactly().systemAndIdentifier(identifierSystem, identifierValue);
		Bundle bundle = getFhirClient().search().forResource(getResourceName()).where(criterion).returnBundle(Bundle.class).execute();
		if (bundle.getTotal() > 0) {
			Bundle.BundleEntryComponent bundleEntryComponent = bundle.getEntry().get(0);
			return (R) bundleEntryComponent.getResource();
		}
		return null;
	}

	protected abstract String getResourceName();

	private void updateMetaElement(R resource) {
		Meta meta = resource.getMeta();
		if (meta == null) {
			meta = new Meta();
		}
		meta.setSource(smartServiceConfiguration.getMetaSourceUuid());
		resource.setMeta(meta);
	}
}
