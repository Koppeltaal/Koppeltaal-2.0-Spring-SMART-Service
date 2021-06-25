package nl.koppeltaal.spring.boot.starter.smartservice.service.fhir;

import ca.uhn.fhir.context.FhirContext;
import com.auth0.jwk.JwkException;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import nl.koppeltaal.spring.boot.starter.smartservice.configuration.SmartServiceConfiguration;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.AuditEventDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.AuditEventDto.AuditEventSubType;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.AuditEventDto.AuditEventType;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.DtoConverter;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.AuditEvent.AuditEventAction;
import org.hl7.fhir.r4.model.AuditEvent.AuditEventEntityComponent;
import org.hl7.fhir.r4.model.AuditEvent.AuditEventSourceComponent;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Enumerations.FHIRAllTypes;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service to persist {@link AuditEvent} objects. This service blocks update or delete calls
 */
@Service
public class AuditEventFhirClientService extends BaseFhirClientService<AuditEventDto, AuditEvent> {

  private static final Logger LOG = LoggerFactory.getLogger(AuditEventFhirClientService.class);

  public AuditEventFhirClientService(SmartServiceConfiguration smartServiceConfiguration, SmartClientCredentialService smartClientCredentialService, FhirContext fhirContext, DtoConverter<AuditEventDto, AuditEvent> dtoConverter) {
    super(smartServiceConfiguration, smartClientCredentialService, fhirContext, dtoConverter, null);
  }

  @Override
  protected String getDefaultSystem() {
    return FHIRAllTypes.AUDITEVENT.getSystem();
  }

  @Override
  protected String getResourceName() {
    return FHIRAllTypes.AUDITEVENT.getDisplay();
  }

  public AuditEvent registerServerStartup() throws IOException {

    LOG.info("Attempting to write server startup AuditEvent to the FHIR store");

    final Coding type = AuditEventType.APPLICATION_ACTIVITY.getCoding();
    final Coding subType = AuditEventSubType.APPLICATION_START.getCoding();

    final AuditEvent auditEvent = getAuditEventBase(type, subType);

    return storeResource(auditEvent);
  }

  public AuditEvent registerServerShutdown() throws IOException {

    LOG.info("Attempting to write server shutdown AuditEvent to the FHIR store");

    final Coding type = AuditEventType.APPLICATION_ACTIVITY.getCoding();
    final Coding subType = AuditEventSubType.APPLICATION_STOP.getCoding();

    final AuditEvent auditEvent = getAuditEventBase(type, subType);

    return storeResource(auditEvent);
  }

  /**
   * <p>Registers an audit event for the creation of a resource.</p>
   * <br/>
   * <p>
   *   IMPORTANT! The resource needs to be persisted before calling this function.
   *   Otherwise, the AuditEvent.entity.reference cannot be generated.
   * </p>
   * @param resource The newly created and persisted resource
   * @return The created AuditEvent
   */
  public AuditEvent registerRestCreate(DomainResource resource) throws IOException {

    final Coding subType = AuditEventSubType.RESTFUL_INTERACTION__CREATE.getCoding();
    return registerRestOperation(resource, subType, AuditEventAction.C);
  }
  /**
   * <p>Registers an audit event for the updating of a resource.</p>
   * @param resource The newly created and persisted resource
   * @return The created AuditEvent
   */
  public AuditEvent registerRestUpdate(DomainResource resource) throws IOException {

    final Coding subType = AuditEventSubType.RESTFUL_INTERACTION__UPDATE.getCoding();
    return registerRestOperation(resource, subType, AuditEventAction.U);
  }

  public AuditEvent registerRestDelete(IIdType idType) throws IOException {
    final Coding subType = AuditEventSubType.RESTFUL_INTERACTION__DELETE.getCoding();
    return registerRestOperation(idType, subType, AuditEventAction.D);
  }

  private AuditEvent registerRestOperation(DomainResource resource, Coding subType, AuditEventAction auditEventAction) throws IOException {

    // Do not recursively create audit event logs for creating an audit event
    if(resource instanceof AuditEvent) return (AuditEvent) resource;

    return registerRestOperation(resource.getIdElement(), subType, auditEventAction);
  }

  private AuditEvent registerRestOperation(IIdType idType, Coding subType, AuditEventAction auditEventAction) throws IOException {
    final Coding type = AuditEventType.RESTFUL_OPERATION.getCoding();
    LOG.info("Creating AuditLog with type [{}] and subType [{}] for resource [{}]", type.getDisplay(), subType.getDisplay(), idType);

    final AuditEvent auditEvent = getAuditEventBase(type, subType);
    auditEvent.setAction(auditEventAction);

    final AuditEventEntityComponent entity = new AuditEventEntityComponent();

    entity.setWhat(new Reference(idType));

    auditEvent.setEntity(Collections.singletonList(entity));

    return storeResource(auditEvent);
  }

  private AuditEvent getAuditEventBase(Coding type, Coding subType) {
    final InstantType date = new InstantType(new Date());

    final Reference reference = new Reference(smartServiceConfiguration.getMetaSourceUuid());
    final AuditEventSourceComponent source = new AuditEventSourceComponent(reference);

    final AuditEvent auditEvent = new AuditEvent(type, date, source);
    auditEvent.setSubtype(Collections.singletonList(subType));
    return auditEvent;
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

  @Override
  public void deleteResource(String id) throws IOException, JwkException {
    throw new UnsupportedOperationException("Deleting logs is not allowed");
  }

  @Override
  public AuditEvent storeResource(AuditEvent resource) throws IOException {

    final String id = getId(resource);
    
    if(StringUtils.isNotBlank(id)) {
      LOG.error("storeResource is called with the `id` field populated. This is against the FHIR "
          + "rules. The user is either updating an AuditEvent or creating a new one with `id`");

      throw new UnsupportedOperationException("Updating logs is not allowed");
    }

    return super.storeResource(resource);
  }
}
