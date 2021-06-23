package nl.koppeltaal.spring.boot.starter.smartservice.dto;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.hl7.fhir.r4.model.AuditEvent.AuditEventAgentComponent;
import org.hl7.fhir.r4.model.AuditEvent.AuditEventEntityComponent;
import org.hl7.fhir.r4.model.AuditEvent.AuditEventSourceComponent;

public class AuditEventDto extends BaseDto {
  private final static String SYS__DCM = "http://dicom.nema.org/resources/ontology/DCM";
  private final static String SYS__AUDIT_EVENT_TYPE = "http://terminology.hl7.org/CodeSystem/audit-event-type";

  //TODO: Implement all fields

  private AuditEventType type;
  private Date recorded;

  //TODO: Make these DTOs as well
  private List<AuditEventAgentComponent> agent;
  private AuditEventSourceComponent source;
  private List<AuditEventEntityComponent> entity;

  public AuditEventType getType() {
    return type;
  }

  public void setType(AuditEventType type) {
    this.type = type;
  }

  public Date getRecorded() {
    return recorded;
  }

  public void setRecorded(Date recorded) {
    this.recorded = recorded;
  }

  public List<AuditEventAgentComponent> getAgent() {
    return agent;
  }

  public void setAgent(List<AuditEventAgentComponent> agent) {
    this.agent = agent;
  }

  public AuditEventSourceComponent getSource() {
    return source;
  }

  public void setSource(AuditEventSourceComponent source) {
    this.source = source;
  }

  public List<AuditEventEntityComponent> getEntity() {
    return entity;
  }

  public void setEntity(List<AuditEventEntityComponent> entity) {
    this.entity = entity;
  }

  public enum AuditEventType {

    APPLICATION_ACTIVITY("110100", SYS__DCM, "Application Activity"),
    PATIENT_RECORD("110110", SYS__DCM, "Patient Record"),
    RESTFUL_OPERATION("rest", SYS__AUDIT_EVENT_TYPE, "Restful Operation")
    ;

    private final String code;
    private final String system;
    private final String display;

    AuditEventType(String code, String system, String display) {
      this.code = code;
      this.system = system;
      this.display = display;
    }

    public String getCode() {
      return code;
    }

    public String getSystem() {
      return system;
    }

    public String getDisplay() {
      return display;
    }

    public static AuditEventType byDisplay(String display) {
      for (AuditEventType value : AuditEventType.values()) {
        if (value.getDisplay().equals(display)) {
          return value;
        }
      }

      throw new IllegalArgumentException(display + " is (currently) not known display value in AuditEventType");
    }
  }

}
