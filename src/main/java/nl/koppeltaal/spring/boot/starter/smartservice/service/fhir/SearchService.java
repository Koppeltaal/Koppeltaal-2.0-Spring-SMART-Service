package nl.koppeltaal.spring.boot.starter.smartservice.service.fhir;

import ca.uhn.fhir.context.FhirContext;
import java.io.IOException;
import java.util.Collections;
import nl.koppeltaal.spring.boot.starter.smartservice.configuration.SmartServiceConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SearchService extends BaseFhirClientService {
  private final RestTemplate restTemplate;

  protected SearchService(SmartServiceConfiguration smartServiceConfiguration, SmartClientCredentialService smartClientCredentialService, FhirContext fhirContext, AuditEventFhirClientService auditEventService) {
    super(smartServiceConfiguration, smartClientCredentialService, fhirContext, auditEventService);
    restTemplate = new RestTemplate();
  }

  public String rawSearch(String resourceType, String query) throws IOException {

    HttpHeaders headers = new HttpHeaders();
    if(smartServiceConfiguration.isBearerTokenEnabled()) {
      headers.setBearerAuth(smartClientCredentialService.getAccessToken());
    }
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(smartServiceConfiguration.getFhirServerUrl() + "/" + resourceType)
        .query(query);

    final HttpEntity<String> request = new HttpEntity<>(headers);


    final ResponseEntity<String> response = restTemplate.exchange(
        builder.toUriString(),
        HttpMethod.GET,
        request,
        String.class
    );

    return response.getBody();
  }
}
