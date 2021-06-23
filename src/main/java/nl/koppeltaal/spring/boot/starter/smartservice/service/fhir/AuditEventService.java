package nl.koppeltaal.spring.boot.starter.smartservice.service.fhir;

import ca.uhn.fhir.context.FhirContext;
import com.auth0.jwk.JwkException;
import java.io.IOException;
import nl.koppeltaal.spring.boot.starter.smartservice.configuration.SmartServiceConfiguration;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.AuditEventDto;
import nl.koppeltaal.spring.boot.starter.smartservice.dto.AuditEventDtoConverter;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.Enumerations.FHIRAllTypes;
import org.springframework.stereotype.Service;

/**
 * Service to persist {@link AuditEvent} objects. This service blocks update or delete calls
 */
@Service
public class AuditEventService extends BaseFhirClientService<AuditEventDto, AuditEvent> {

  public AuditEventService(SmartServiceConfiguration smartServiceConfiguration, SmartClientCredentialService smartClientCredentialService, FhirContext fhirContext, AuditEventDtoConverter auditEventDtoConverter) {
    super(smartServiceConfiguration, smartClientCredentialService, fhirContext, auditEventDtoConverter);
  }

  @Override
  protected String getDefaultSystem() {
    return FHIRAllTypes.AUDITEVENT.getSystem();
  }

  @Override
  protected String getResourceName() {
    return FHIRAllTypes.AUDITEVENT.getDisplay();
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
  public AuditEvent storeResource(String source, AuditEvent resource) throws IOException, JwkException {

    final String identifier = getIdentifier(source, resource);

    if(getResourceByIdentifier(identifier) != null) {
      throw new UnsupportedOperationException("Updating logs is not allowed");
    }

    return super.storeResource(source, resource);
  }
}
