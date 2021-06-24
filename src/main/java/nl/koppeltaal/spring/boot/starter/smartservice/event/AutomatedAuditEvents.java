package nl.koppeltaal.spring.boot.starter.smartservice.event;

import java.io.IOException;
import javax.annotation.PreDestroy;
import nl.koppeltaal.spring.boot.starter.smartservice.service.fhir.AuditEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * <p>This component will automatically write audit events to the FHIR store where possible.
 * For example, after the server started up successfully.</p>
 *
 * <p>Do not move the {@link ConditionalOnProperty} to the class level as this will break the
 * bean injecting via the <code>spring.factories</code></p>
 */
@Component
public class AutomatedAuditEvents {

  private final AuditEventService auditEventService;

  public AutomatedAuditEvents(AuditEventService auditEventService) {
    this.auditEventService = auditEventService;
  }

  @EventListener(ApplicationReadyEvent.class)
  @ConditionalOnProperty(value = "fhir.smart.service.auditEventsEnabled", havingValue = "true")
  public void registerServerStartup() throws IOException {
    auditEventService.registerServerStartup();
  }

  @PreDestroy
  @ConditionalOnProperty(value = "fhir.smart.service.auditEventsEnabled", havingValue = "true")
  public void registerServerShutdown() throws IOException {
    auditEventService.registerServerShutdown();
  }
}
