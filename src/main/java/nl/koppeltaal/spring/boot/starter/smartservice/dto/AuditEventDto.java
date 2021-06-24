package nl.koppeltaal.spring.boot.starter.smartservice.dto;

import java.util.Date;
import org.hl7.fhir.r4.model.Coding;

public class AuditEventDto extends BaseDto {
  private final static String SYS__DCM = "http://dicom.nema.org/resources/ontology/DCM";
  private final static String SYS__AUDIT_EVENT_TYPE = "http://terminology.hl7.org/CodeSystem/audit-event-type";
  private final static String SYS__RESTFUL_INTERACTION = "http://hl7.org/fhir/restful-interaction";

  //TODO: Implement all fields

  private String type;
  private String subType;
  private Date recorded;

  //TODO: Make proper DTOs objects for  agent, source and entity
  private String sourceReference;
  private String entityWhat;
  private String source;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getSubType() {
    return subType;
  }

  public void setSubType(String subType) {
    this.subType = subType;
  }

  public Date getRecorded() {
    return recorded;
  }

  public void setRecorded(Date recorded) {
    this.recorded = recorded;
  }

  public String getSourceReference() {
    return sourceReference;
  }

  public void setSourceReference(String sourceReference) {
    this.sourceReference = sourceReference;
  }

  public String getEntityWhat() {
    return entityWhat;
  }

  public void setEntityWhat(String entityWhat) {
    this.entityWhat = entityWhat;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
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

    public Coding getCoding() {
      return new Coding(this.system, this.code, this.display);
    }
  }

  public enum AuditEventSubType {
    APPLICATION_START("110120", SYS__DCM, "Application Start"),
    APPLICATION_STOP("110121", SYS__DCM, "Application Stop"),
    RESTFUL_INTERACTION__CREATE("create", SYS__RESTFUL_INTERACTION, "create"),
    RESTFUL_INTERACTION__UPDATE("update", SYS__RESTFUL_INTERACTION, "update"),
    RESTFUL_INTERACTION__DELETE("delete", SYS__RESTFUL_INTERACTION, "delete"),
    RESTFUL_INTERACTION__READ("read", SYS__RESTFUL_INTERACTION, "read"),
    ;
    private final String code;
    private final String system;
    private final String display;

    AuditEventSubType(String code, String system, String display) {
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

    public static AuditEventSubType byDisplay(String display) {
      for (AuditEventSubType value : AuditEventSubType.values()) {
        if (value.getDisplay().equals(display)) {
          return value;
        }
      }

      throw new IllegalArgumentException(display + " is (currently) not known display value in AuditEventType");
    }

    public Coding getCoding() {
      return new Coding(this.system, this.code, this.display);
    }

  }
}
