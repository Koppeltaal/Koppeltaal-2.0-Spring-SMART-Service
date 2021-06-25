package nl.koppeltaal.spring.boot.starter.smartservice.event;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import javax.annotation.PreDestroy;
import nl.koppeltaal.spring.boot.starter.smartservice.service.fhir.AuditEventFhirClientService;
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
  private static final Logger LOG = LoggerFactory.getLogger(AutomatedAuditEvents.class);
  private final AuditEventFhirClientService auditEventService;

  public AutomatedAuditEvents(AuditEventFhirClientService auditEventService) {
    this.auditEventService = auditEventService;
  }

  @EventListener(ApplicationReadyEvent.class)
  @ConditionalOnProperty(value = "fhir.smart.service.auditEventsEnabled", havingValue = "true")
  public void registerServerStartup() {

    /*
     * Start in a new thread as it can happen that the registration fails at the start
     * for example, when starting this in Kubernetes. The startup will send a request
     * to the FHIR store, but Kubernetes didn't get a successful liveness probe check.
     * If the FHIR store depends on a JWKS endpoint, this won't work.
     */
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        try {
          LOG.info("Running TimerTask to report server startup");
          auditEventService.registerServerStartup();
          this.cancel(); //succeeded, task can stop
        } catch (Exception e) {
          LOG.warn("Unable to send server startup audit event, message: [{}]", e.getMessage());
        }
      }
    };

    timer.scheduleAtFixedRate(task, 0, 2000);
  }

  @PreDestroy
  @ConditionalOnProperty(value = "fhir.smart.service.auditEventsEnabled", havingValue = "true")
  public void registerServerShutdown() throws IOException {
    auditEventService.registerServerShutdown();
  }
}
