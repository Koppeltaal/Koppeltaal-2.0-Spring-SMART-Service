package nl.koppeltaal.spring.boot.starter.smartservice.service.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import java.io.IOException;
import nl.koppeltaal.spring.boot.starter.smartservice.configuration.SmartServiceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseFhirClientService {

  private static final Logger LOG = LoggerFactory.getLogger(BaseFhirClientService.class);

  final SmartServiceConfiguration smartServiceConfiguration;
  final SmartClientCredentialService smartClientCredentialService;
  final FhirContext fhirContext;
  final AuditEventFhirClientService auditEventService;

  protected BaseFhirClientService(SmartServiceConfiguration smartServiceConfiguration, SmartClientCredentialService smartClientCredentialService, FhirContext fhirContext, AuditEventFhirClientService auditEventService) {
    this.smartServiceConfiguration = smartServiceConfiguration;
    this.smartClientCredentialService = smartClientCredentialService;
    this.fhirContext = fhirContext;
    this.auditEventService = auditEventService;
  }

  protected IGenericClient getFhirClient() throws IOException {

    IGenericClient iGenericClient = fhirContext.newRestfulGenericClient(smartServiceConfiguration.getFhirServerUrl());

    iGenericClient.registerInterceptor(new BearerTokenAuthInterceptor(smartClientCredentialService.getAccessToken()));

    return iGenericClient;
  }
}
