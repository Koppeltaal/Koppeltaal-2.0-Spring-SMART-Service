package nl.koppeltaal.spring.boot.starter.smartservice.service.fhir;

import ca.uhn.fhir.context.FhirContext;
import com.auth0.jwk.JwkException;
import nl.koppeltaal.spring.boot.starter.smartservice.configuration.SmartServiceConfiguration;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.AuditEventDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.AuditEventDto.AuditEventSubType;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.AuditEventDto.AuditEventType;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.DtoConverter;
import nl.koppeltaal.spring.boot.starter.smartservice.service.context.TraceContext;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.AuditEvent.*;
import org.hl7.fhir.r4.model.Enumerations.FHIRAllTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;

/**
 * Service to persist {@link AuditEvent} objects. This service blocks update or delete calls
 */
@Service
public class AuditEventFhirClientService extends BaseFhirClientCrudService<AuditEventDto, AuditEvent> {

	public final static String META_PROFILE_URL = "http://koppeltaal.nl/fhir/StructureDefinition/KT2AuditEvent";

	private static final Logger LOG = LoggerFactory.getLogger(AuditEventFhirClientService.class);

	public AuditEventFhirClientService(SmartServiceConfiguration smartServiceConfiguration, SmartClientCredentialService smartClientCredentialService, FhirContext fhirContext, DtoConverter<AuditEventDto, AuditEvent> dtoConverter) {
		super(smartServiceConfiguration, smartClientCredentialService, fhirContext, dtoConverter, null);
	}

	@Override
	public void deleteResource(String id) throws IOException, JwkException {
		throw new UnsupportedOperationException("Deleting logs is not allowed");
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////
	// Servers that provide support for AuditEvent resources would not generally accept update      //
	// or delete operations on the resources, as this would compromise the integrity of the audit   //
	// record. - section below disabled delete and update operations                                //
	//////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void deleteResourceByReference(String id) throws IOException, JwkException {
		throw new UnsupportedOperationException("Deleting logs is not allowed");
	}

	public AuditEvent registerServerShutdown() throws IOException {

		if (!smartServiceConfiguration.isAuditEventsEnabled()) return null;

		LOG.info("Attempting to write server shutdown AuditEvent to the FHIR store");

		final Coding type = getCoding(AuditEventType.APPLICATION_ACTIVITY);
		final Coding subType = getCoding(AuditEventSubType.APPLICATION_STOP);

		final AuditEvent auditEvent = getAuditEventBase(type, subType);

		AuditEventEntityComponent entity = new AuditEventEntityComponent();

		entity.setDescription("Server shutdown application with client_id " + smartServiceConfiguration.getClientId());
		auditEvent.setEntity(Collections.singletonList(entity));

		return storeResource(auditEvent);
	}

	public AuditEvent registerServerStartup() throws IOException {

		if (!smartServiceConfiguration.isAuditEventsEnabled()) return null;

		LOG.info("Attempting to write server startup AuditEvent to the FHIR store");

		final Coding type = getCoding(AuditEventType.APPLICATION_ACTIVITY);
		final Coding subType = getCoding(AuditEventSubType.APPLICATION_START);

		final AuditEvent auditEvent = getAuditEventBase(type, subType);

		AuditEventEntityComponent entity = new AuditEventEntityComponent();

		entity.setDescription("Server startup application with client_id " + smartServiceConfiguration.getClientId());
		auditEvent.setEntity(Collections.singletonList(entity));

		return storeResource(auditEvent);
	}

	@Override
	public AuditEvent storeResource(AuditEvent resource, @Nullable TraceContext traceContext) throws IOException {

		final String id = getId(resource);

		if (StringUtils.isNotBlank(id)) {
			LOG.error("storeResource is called with the `id` field populated. This is against the FHIR "
					+ "rules. The user is either updating an AuditEvent or creating a new one with `id`");

			throw new UnsupportedOperationException("Updating logs is not allowed");
		}

		return super.storeResource(resource, traceContext);
	}

	private AuditEvent getAuditEventBase(Coding type, Coding subType) {
		final InstantType date = new InstantType(new Date());

		Meta meta = new Meta();
		meta.setProfile(Collections.singletonList(new CanonicalType(META_PROFILE_URL)));

		final Reference reference = new Reference(smartServiceConfiguration.getMetaSourceUuid());
		final AuditEventSourceComponent source = new AuditEventSourceComponent(reference);

		final AuditEvent auditEvent = new AuditEvent(type, date, source);
		auditEvent.setMeta(meta);
		auditEvent.setSubtype(Collections.singletonList(subType));

		final AuditEventAgentComponent agent = new AuditEventAgentComponent();
		//TODO: Add a Config object to populate the agent with more useful data.
		agent.setRequestor(true);
		auditEvent.setAgent(Collections.singletonList(agent));

		return auditEvent;
	}

	private Coding getCoding(AuditEventSubType type) {
		return new Coding(type.getSystem(), type.getCode(), type.getDisplay());
	}

	private Coding getCoding(AuditEventType type) {
		return new Coding(type.getSystem(), type.getCode(), type.getDisplay());
	}

	@Override
	protected String getDefaultSystem() {
		return FHIRAllTypes.AUDITEVENT.getSystem();
	}

	@Override
	protected String getResourceName() {
		return FHIRAllTypes.AUDITEVENT.getDisplay();
	}

	private AuditEvent registerRestOperation(IIdType idType, Coding subType, AuditEventAction auditEventAction, TraceContext traceContext) throws IOException {
		final Coding type = getCoding(AuditEventType.RESTFUL_OPERATION);
		LOG.info("Creating AuditLog with type [{}] and subType [{}] for resource [{}]", type.getDisplay(), subType.getDisplay(), idType);

		final AuditEvent auditEvent = getAuditEventBase(type, subType);
		auditEvent.setAction(auditEventAction);

		final AuditEventEntityComponent entity = new AuditEventEntityComponent();

		entity.setWhat(new Reference(idType));

		auditEvent.setEntity(Collections.singletonList(entity));

		auditEvent.addExtension("http://koppeltaal.nl/fhir/StructureDefinition/trace-id", new IdType(traceContext.getTraceId()));
		auditEvent.addExtension("http://koppeltaal.nl/fhir/StructureDefinition/request-id", new IdType(traceContext.getSpanId()));
		auditEvent.addExtension("http://koppeltaal.nl/fhir/StructureDefinition/correlation-id", new IdType(traceContext.getParentSpanId()));

		return storeResource(auditEvent);
	}

	private AuditEvent registerRestOperation(DomainResource resource, Coding subType, AuditEventAction auditEventAction, TraceContext traceContext) throws IOException {

		// Do not recursively create audit event logs for creating an audit event
		if (resource instanceof AuditEvent) return (AuditEvent) resource;

		return registerRestOperation(resource.getIdElement(), subType, auditEventAction, traceContext);
	}
}
