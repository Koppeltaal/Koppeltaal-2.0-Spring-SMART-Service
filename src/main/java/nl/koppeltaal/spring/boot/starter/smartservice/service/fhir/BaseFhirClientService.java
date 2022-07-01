package nl.koppeltaal.spring.boot.starter.smartservice.service.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.*;
import nl.koppeltaal.spring.boot.starter.smartservice.configuration.SmartServiceConfiguration;
import nl.koppeltaal.spring.boot.starter.smartservice.service.context.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;

public abstract class BaseFhirClientService {

	private static final Logger LOG = LoggerFactory.getLogger(BaseFhirClientService.class);
	static IGenericClient iGenericClient;
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

	protected IGenericClient getFhirClient() {

		if (iGenericClient != null) {
			return iGenericClient;
		}

		iGenericClient = fhirContext.newRestfulGenericClient(smartServiceConfiguration.getFhirServerUrl());

		if (smartServiceConfiguration.isBearerTokenEnabled()) {
			iGenericClient.registerInterceptor(new IClientInterceptor() {
				@Override
				public void interceptRequest(IHttpRequest theRequest) {
					try {
						theRequest.addHeader(Constants.HEADER_AUTHORIZATION, (Constants.HEADER_AUTHORIZATION_VALPREFIX_BEARER + smartClientCredentialService.getAccessToken()));
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}

				@Override
				public void interceptResponse(IHttpResponse theResponse) {

				}
			});
		} else {
			LOG.warn("Bearer token interceptor is disabled, only use this for development environments!");
		}

		return iGenericClient;
	}
}
