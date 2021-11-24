/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.spring.boot.starter.smartservice.service.fhir;

import static nl.koppeltaal.spring.boot.starter.smartservice.constants.FhirConstant.CLASS_TO_PROFILE_MAP;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortOrderEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import com.auth0.jwk.JwkException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public abstract class BaseFhirClientCrudService<D extends BaseDto, R extends DomainResource> extends BaseFhirClientService {

	private static final Logger LOG = LoggerFactory.getLogger(AutomatedAuditEvents.class);

	final DtoConverter<D, R> dtoConverter;

	public BaseFhirClientCrudService(SmartServiceConfiguration smartServiceConfiguration, SmartClientCredentialService smartClientCredentialService, FhirContext fhirContext, DtoConverter<D, R> dtoConverter, AuditEventFhirClientService auditEventService) {
		super(smartServiceConfiguration, smartClientCredentialService, fhirContext, auditEventService);
		this.dtoConverter = dtoConverter;
	}

	public void deleteResource(String id) throws IOException, JwkException {
		final IBaseOperationOutcome execute = getFhirClient().delete()
				.resourceById(getResourceName(), id).execute();

		LOG.info("Deleted entity [{}]", execute.getIdElement());
		auditEventService.registerRestDelete(execute.getIdElement());

	}

	public void deleteResourceByReference(String id) throws IOException, JwkException {
		R resource = getResourceByReference(id);
		if (resource != null) {
			final IBaseOperationOutcome execute = getFhirClient().delete().resource(resource).execute();
			LOG.info("Deleted entity [{}]", execute.getIdElement());
			auditEventService.registerRestDelete(execute.getIdElement());
		}
	}

	public R getResourceByIdentifier(Identifier identifier) {
		String system = StringUtils.isNotEmpty(identifier.getSystem()) ? identifier.getSystem() : getDefaultSystem();
		return getResourceByIdentifier(system, identifier.getValue());
	}

	public R getResourceByIdentifier(String identifierValue) {
		return getResourceByIdentifier(identifierValue, getDefaultSystem());
	}

	public R getResourceByReference(String reference) {
		return (R) getFhirClient().read().resource(getResourceName()).withId(reference).execute();
	}

	public R getResourceByReference(Reference reference) {
		String ref = reference.getReference();
		if (StringUtils.isNotEmpty(ref)) {
			return getResourceByReference(ref);
		} else if (reference.getIdentifier() != null) {
			return getResourceByIdentifier(reference.getIdentifier());
		}
		return null;
	}

	public List<R> getResources(SortSpec sort) {
		return getResourcesInternal(sort, null);
	}

	public List<R> getResources() {
		return getResourcesInternal(null, null);
	}

	public List<R> getResources(ICriterion<?> criterion) {
		return getResourcesInternal(null, criterion);
	}

	public List<R> getResources(Map<String, List<IQueryParameterType>> criteria) {
		return getResourcesInternal(null, null, criteria);
	}

	public List<R> getResources(SortSpec sort, ICriterion<?> criterion) {
		return getResourcesInternal(sort, criterion);
	}

	private List<R> getResourcesInternal(SortSpec sort, ICriterion<?> criterion) {
		return  getResourcesInternal(sort, criterion, null);
	}

	private List<R> getResourcesInternal(SortSpec sort, ICriterion<?> criterion, Map<String, List<IQueryParameterType>> criteria) {
		List<R> rv = new ArrayList<>();

		final IQuery<Bundle> query = getFhirClient().search().forResource(getResourceName()).returnBundle(Bundle.class);

		query.sort(sort != null ? sort : new SortSpec("_id", SortOrderEnum.DESC));

		if(criterion != null) {
			query.where(criterion);
		}

		if(criteria != null) {
			if(criterion != null) throw new IllegalStateException("Cannot define both a criterion and criteria.");
			query.where(criteria);
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

		final R updatedEntity;
		try {
			if (res != null) {
				dtoConverter.applyDto(res, dtoConverter.convert(resource));
				updateMetaElement(res); //only needed to add profile to existing resources that don't have the "latest" profile
				MethodOutcome execute = getFhirClient().update().resource(res).execute();
				updatedEntity = (R) execute.getResource();

				LOG.info("Updated entity [{}]", updatedEntity.getIdElement());
				if(!FHIRAllTypes.AUDITEVENT.getDisplay().equals(getResourceName())) {
					auditEventService.registerRestUpdate(updatedEntity);
				}
				return updatedEntity;
			}

			updateMetaElement(resource);
			MethodOutcome execute = getFhirClient().create().resource(resource).execute();
			updatedEntity = (R) execute.getResource();
			LOG.info("Created entity [{}]", updatedEntity.getIdElement());

			if(!FHIRAllTypes.AUDITEVENT.getDisplay().equals(getResourceName())) {
				auditEventService.registerRestCreate(updatedEntity);
			}
		} catch (BaseServerResponseException e) {

			LOG.error("Failed to validate resource [{}] and id [{}] with the following message: {}",
					resource.getClass().getName(), id, e.getResponseBody());

			throw e;
		}

		return updatedEntity;
	}

	protected abstract String getDefaultSystem();

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

	protected R getResourceByIdentifier(String identifierValue, String identifierSystem) {
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

		final String profile = CLASS_TO_PROFILE_MAP.get(resource.getClass());
		if(StringUtils.isNotBlank(profile) && !meta.hasProfile(profile)) {
			meta.addProfile(profile);
		}

		meta.setSource(smartServiceConfiguration.getMetaSourceUuid());
		resource.setMeta(meta);
	}
}
