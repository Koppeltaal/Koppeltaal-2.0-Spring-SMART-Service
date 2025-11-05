/*
 * Copyright (c) Stichting Koppeltaal 2021.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package nl.koppeltaal.spring.boot.starter.smartservice.service.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.gclient.IClientExecutable;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import com.auth0.jwk.JwkException;
import nl.koppeltaal.spring.boot.starter.smartservice.configuration.SmartServiceConfiguration;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.BaseDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.DtoConverter;
import nl.koppeltaal.spring.boot.starter.smartservice.event.AutomatedAuditEvents;
import nl.koppeltaal.spring.boot.starter.smartservice.service.context.TraceContext;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static nl.koppeltaal.spring.boot.starter.smartservice.constants.FhirConstant.CLASS_TO_PROFILE_MAP;

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
		deleteResource(id, null);
	}

	public void deleteResource(String id, @Nullable TraceContext traceContext) {
		final MethodOutcome execute = executeMethod(getFhirClient().delete()
				.resourceById(getResourceName(), id), traceContext, null);

		LOG.info("Deleted entity [{}]", execute.getId());
	}

	public void deleteResourceByReference(String id) throws IOException, JwkException {
		deleteResourceByReference(id, null);
	}

	public void deleteResourceByReference(String id, @Nullable TraceContext traceContext) {
		R resource = getResourceByReference(id);
		if (resource != null) {
			final MethodOutcome execute = executeMethod(getFhirClient().delete().resource(resource), traceContext, resource);
			LOG.info("Deleted entity [{}]", execute.getId());
		}
	}

	public R deleteByMarkingAsEndOfLife(String reference) throws IOException {
		return deleteByMarkingAsEndOfLife(reference, null);
	}

	public R deleteByMarkingAsEndOfLife(String reference, @Nullable TraceContext traceContext) throws IOException {
		R resource = getResourceByReference(reference);

		if(resource instanceof ActivityDefinition) {
			((ActivityDefinition) resource).setStatus(Enumerations.PublicationStatus.RETIRED);
		} else if(resource instanceof Endpoint) {
			((Endpoint) resource).setStatus(Endpoint.EndpointStatus.OFF);
		} else if(resource instanceof Device) {
			((Device) resource).setStatus(Device.FHIRDeviceStatus.INACTIVE);
		} else if(resource instanceof Task) {
			((Task) resource).setStatus(Task.TaskStatus.CANCELLED);
		} else if(resource instanceof Patient) {
			((Patient) resource).setActive(false);
		} else if(resource instanceof Practitioner) {
			((Practitioner) resource).setActive(false);
		} else if(resource instanceof CareTeam) {
			((CareTeam) resource).setStatus(CareTeam.CareTeamStatus.INACTIVE);
		} else if(resource instanceof Organization) {
			((Organization) resource).setActive(false);
		} else if(resource instanceof Subscription) {
			((Subscription) resource).setStatus(Subscription.SubscriptionStatus.OFF);
		}

		return storeResource(resource, traceContext);
	}

	public <E extends IClientExecutable<?, O>, O extends IBaseResource> O execute(E type) {
		return execute(type, null);
	}
	public <E extends IClientExecutable<?, O>, O extends IBaseResource> O execute(E type, @Nullable TraceContext traceContext) {
		setTracingHeaders(type, traceContext);
		return type.execute();
	}
	public <E extends IClientExecutable<?, O>, O extends MethodOutcome> O executeMethod(E type, @Nullable R previous) {
		return executeMethod(type, null, previous);
	}

	public <E extends IClientExecutable<?, O>, O extends MethodOutcome> O executeMethod(E type, @Nullable TraceContext traceContext, @Nullable R previous) {
		setTracingHeaders(type, traceContext);
		setIfMatchHeader(type, previous);
		return type.execute();
	}

	private void setIfMatchHeader(IClientExecutable type, @Nullable R previous) {
		if (previous != null) {
			String version = getVersion(previous);
			if (StringUtils.isNotEmpty(version)) {
				type.withAdditionalHeader("If-Match", "W/\"" + version + "\"");
			}
		}
	}

	private String getVersion(R previous) {
		String version = previous.getIdElement().getVersionIdPart();
		if (StringUtils.isEmpty(version)) {
			version = previous.getMeta().getVersionId();
		}
		return version;
	}

	private void setTracingHeaders(IClientExecutable type, @Nullable TraceContext traceContext) {
		traceContext = traceContext != null ? traceContext : new TraceContext();
		type.withAdditionalHeader("X-B3-TraceId", traceContext.getTraceId())
				.withAdditionalHeader("X-B3-ParentSpanId", traceContext.getParentSpanId())
				.withAdditionalHeader("X-B3-SpanId", traceContext.getSpanId())
				.withAdditionalHeader("X-B3-Sampled", "1");
	}

	public R getResourceByIdentifier(Identifier identifier) {
		return getResourceByIdentifier(identifier, null);
	}

	public R getResourceByIdentifier(Identifier identifier, @Nullable TraceContext traceContext) {
		String system = StringUtils.isNotEmpty(identifier.getSystem()) ? identifier.getSystem() : getDefaultSystem();
		return getResourceByIdentifier(identifier.getValue(), system, traceContext);
	}

	public R getResourceByIdentifier(String identifierValue) {
		return getResourceByIdentifier(identifierValue, (TraceContext) null);
	}

	public R getResourceByIdentifier(String identifierValue, @Nullable TraceContext traceContext) {
		return getResourceByIdentifier(identifierValue, getDefaultSystem(), traceContext);
	}

	/**
	 * @see #getResourceByUrl(String, TraceContext)
	 */
	public R getResourceByUrl(String url) {
		return getResourceByUrl(url, null);
	}

	/**
	 * Certain resources have a url property, and this can be used by Canonical References.
	 * Added a service function to retrieve resources based on this attribute. It has to be
	 * globally unique, so the function returns 1 instance. Logs a warning if it finds more
	 * and returns the first in the list.
	 *
	 * @param url The value of the URL it needs to find
	 * @param traceContext A {@link TraceContext}
	 * @return null if not found, otherwise the first resource it found
	 */
	public R getResourceByUrl(String url, @Nullable TraceContext traceContext) {
		final Map<String, List<IQueryParameterType>> criteria = new HashMap<>();
		criteria.put("url", Collections.singletonList(new UriParam(url)));

		List<R> resourcesInternal = getResourcesInternal(null, null, criteria, traceContext, false);

		if(resourcesInternal.isEmpty()) return null;

		if(resourcesInternal.size() > 1) {
			LOG.warn("Found multiple resources with url value {}, returning first in list @ getResourceByUrl(url).", url);
		}
		return resourcesInternal.get(0);
	}

	public R getResourceByReference(String reference) {
		return getResourceByReference(reference, null);
	}
	public R getResourceByReference(String reference, @Nullable TraceContext traceContext) {
		return (R) execute(getFhirClient().read().resource(getResourceName()).withId(reference), traceContext);
	}

	public R getResourceByReference(Reference reference) {
		return getResourceByReference(reference, null);
	}

	public R getResourceByReference(Reference reference, @Nullable TraceContext traceContext) {
		String ref = reference.getReference();
		if (StringUtils.isNotEmpty(ref)) {
			return getResourceByReference(ref);
		} else if (reference.getIdentifier() != null) {
			return getResourceByIdentifier(reference.getIdentifier(), traceContext);
		}
		return null;
	}

	public List<R> getResources(SortSpec sort) {
		return getResources(sort, (TraceContext)null);
	}

	public List<R> getResources(SortSpec sort, @Nullable TraceContext traceContext) {
		return getResourcesInternal(sort, null, traceContext);
	}

	public List<R> getResources() {
		return getResources((TraceContext) null);
	}

	public List<R> getResources(GetResourceBuilder<D, R> resourceBuilder) {

		return getResourcesInternal(
				resourceBuilder.getSort(),
				resourceBuilder.getCriterion(),
				resourceBuilder.getCriteria(),
				resourceBuilder.getTraceContext(),
				resourceBuilder.isIncludeEndOfLife()
		);
	}


	public List<R> getResources(@Nullable TraceContext traceContext) {
		return getResourcesInternal(null, null, traceContext);
	}

	public List<R> getResources(ICriterion<?> criterion) {
		return getResources(criterion, null);
	}

	public List<R> getResources(ICriterion<?> criterion, @Nullable TraceContext traceContext) {
		return getResourcesInternal(null, criterion, traceContext);
	}

	public List<R> getResources(Map<String, List<IQueryParameterType>> criteria) {
		return getResources(criteria, null);
	}
	public List<R> getResources(Map<String, List<IQueryParameterType>> criteria, @Nullable TraceContext traceContext) {
		return getResourcesInternal(null, null, criteria, traceContext, false);
	}

	public List<R> getResources(SortSpec sort, ICriterion<?> criterion) {
		return getResourcesInternal(sort, criterion, null);
	}

	public List<R> getResources(SortSpec sort, ICriterion<?> criterion, @Nullable TraceContext traceContext) {
		return getResourcesInternal(sort, criterion, traceContext);
	}

	public R storeResource(R resource) throws IOException {
		return storeResource(resource, null);
	}

	public R storeResource(R resource, @Nullable TraceContext traceContext) throws IOException {
		String identifierValue = getIdentifierValue(getDefaultSystem(), resource);
		String id = getId(resource);
		R res = null;
		if (StringUtils.isNotEmpty(id)) {
			res = getResourceByReference(id);
		} else if (StringUtils.isNotEmpty(identifierValue)) {
			res = getResourceByIdentifier(identifierValue, getDefaultSystem(), traceContext);
		}

		final R updatedEntity;
		try {
			if (res != null) {
				dtoConverter.applyDto(res, dtoConverter.convert(resource));
				updateMetaElement(res); //only needed to add profile to existing resources that don't have the "latest" profile
				MethodOutcome execute = executeMethod(getFhirClient().update().resource(res), traceContext, res);
				updatedEntity = (R) execute.getResource();

				LOG.info("Updated entity [{}]", updatedEntity.getIdElement());
				return updatedEntity;
			}

			updateMetaElement(resource);
			MethodOutcome execute = executeMethod(getFhirClient().create().resource(resource), traceContext,null);
			updatedEntity = (R) execute.getResource();
			LOG.info("Created entity [{}]", updatedEntity.getIdElement());

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

	protected final String getIdentifierValue(String system, R resource) {
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
		return getResourceByIdentifier(identifierValue, identifierSystem, null);
	}

	public R getResourceByIdentifier(String identifierValue, String identifierSystem, @Nullable TraceContext traceContext) {
		ICriterion<TokenClientParam> criterion = new TokenClientParam("identifier").exactly().systemAndIdentifier(identifierSystem, identifierValue);
		Bundle bundle = execute(getFhirClient().search().forResource(getResourceName()).where(criterion).returnBundle(Bundle.class), traceContext);
		if (bundle.getTotal() > 0) {
			Bundle.BundleEntryComponent bundleEntryComponent = bundle.getEntry().get(0);
			return (R) bundleEntryComponent.getResource();
		}
		return null;
	}

	protected abstract String getResourceName();
	/**
	 * As KT2 works with an end-of-life cycle instead of deletes, crud services can provide their way of excluding
	 * entities marked as end-of-life. By default, the crud services will exclude end-of-life entities
	 */
	protected abstract Map<String, List<IQueryParameterType>> getEndOfLifeExclusion();

	private List<R> getResourcesInternal(SortSpec sort, ICriterion<?> criterion, Map<String, List<IQueryParameterType>> criteria, @Nullable TraceContext traceContext, boolean includeEndOfLife) {
		List<R> rv = new ArrayList<>();

		final IQuery<Bundle> query = getFhirClient().search().forResource(getResourceName()).returnBundle(Bundle.class);

		if(sort != null) {
			query.sort(sort);
		}

		if (criterion != null) {
			query.where(criterion);
		}

		Map<String, List<IQueryParameterType>> endOfLifeExclusion = getEndOfLifeExclusion();
		if (criteria != null) {
			if (criterion != null) throw new IllegalStateException("Cannot define both a criterion and criteria.");

			if(!includeEndOfLife && endOfLifeExclusion != null) {
				criteria.putAll(endOfLifeExclusion);
			}

			query.where(criteria);
		} else if(!includeEndOfLife && endOfLifeExclusion != null) {
			query.where(endOfLifeExclusion);
		}

		//FIXME: The server returns paginated results, this client doesn't support pagination,
		// it just transforms the amount returned (fhir store config)
		Bundle bundle = execute(query, traceContext);

		for (Bundle.BundleEntryComponent component : bundle.getEntry()) {
			R resource = (R) component.getResource();
			rv.add(resource);
		}
		return rv;
	}

	private List<R> getResourcesInternal(SortSpec sort, ICriterion<?> criterion, @Nullable TraceContext traceContext) {
		return getResourcesInternal(sort, criterion, null, traceContext, false);
	}

	private void updateMetaElement(R resource) {
		Meta meta = resource.getMeta();
		if (meta == null) {
			meta = new Meta();
		}

		final String profile = CLASS_TO_PROFILE_MAP.get(resource.getClass());
		if (StringUtils.isNotBlank(profile) && !meta.hasProfile(profile)) {
			meta.addProfile(profile);
		}

		meta.setSource(smartServiceConfiguration.getMetaSourceUuid());
		resource.setMeta(meta);
	}
}
