package nl.koppeltaal.spring.boot.starter.smartservice.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AuditEventDto extends BaseDto {
	private final static String SYS__DCM = "http://dicom.nema.org/resources/ontology/DCM";
	private final static String SYS__AUDIT_EVENT_TYPE = "http://terminology.hl7.org/CodeSystem/audit-event-type";
	private final static String SYS__RESTFUL_INTERACTION = "http://hl7.org/fhir/restful-interaction";

	//TODO: Implement all fields

	private AuditEventType type;
	private AuditEventSubType subType;
	private Date recorded;

	//TODO: Make proper DTOs objects for  agent, source and entity
	private String sourceReference;
	private String entityType;
	private List<String> entityWhat = new ArrayList<>();
	private String action;
	private String source;
	private String agent;
	private String query;
	private String outcome;
	private String traceId;
	private String spanId;
	private String parentSpanId;

	public void addEntityWhat(String entityWhat) {
		this.entityWhat.add(entityWhat);
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public List<String> getEntityWhat() {
		return entityWhat;
	}

	public void setEntityWhat(List<String> entityWhat) {
		this.entityWhat = entityWhat;
	}

	public String getOutcome() {
		return outcome;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}

	public String getParentSpanId() {
		return parentSpanId;
	}

	public void setParentSpanId(String parentSpanId) {
		this.parentSpanId = parentSpanId;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Date getRecorded() {
		return recorded;
	}

	public void setRecorded(Date recorded) {
		this.recorded = recorded;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSourceReference() {
		return sourceReference;
	}

	public void setSourceReference(String sourceReference) {
		this.sourceReference = sourceReference;
	}

	public String getSpanId() {
		return spanId;
	}

	public void setSpanId(String spanId) {
		this.spanId = spanId;
	}

	public AuditEventSubType getSubType() {
		return subType;
	}

	public void setSubType(AuditEventSubType subType) {
		this.subType = subType;
	}

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}

	public AuditEventType getType() {
		return type;
	}

	public void setType(AuditEventType type) {
		this.type = type;
	}

	public static class AuditEventSubType {
		public static final AuditEventSubType APPLICATION_START = new AuditEventSubType("110120", SYS__DCM, "Application Start");
		public static final AuditEventSubType APPLICATION_STOP = new AuditEventSubType("110121", SYS__DCM, "Application Stop");
		public static final AuditEventSubType RESTFUL_INTERACTION__CREATE = new AuditEventSubType("create", SYS__RESTFUL_INTERACTION, "create");
		public static final AuditEventSubType RESTFUL_INTERACTION__UPDATE = new AuditEventSubType("update", SYS__RESTFUL_INTERACTION, "update");
		public static final AuditEventSubType RESTFUL_INTERACTION__DELETE = new AuditEventSubType("delete", SYS__RESTFUL_INTERACTION, "delete");
		public static final AuditEventSubType RESTFUL_INTERACTION__READ = new AuditEventSubType("read", SYS__RESTFUL_INTERACTION, "read");
		private final String code;
		private final String system;
		private final String display;

		AuditEventSubType(String code, String system, String display) {
			this.code = code;
			this.system = system;
			this.display = display;
		}

		public static AuditEventSubType newAuditEventSubType(String code, String system, String display) {
			return new AuditEventSubType(code, system, display);
		}

		public String getCode() {
			return code;
		}

		public String getDisplay() {
			return display;
		}

		public String getSystem() {
			return system;
		}

	}

	public static class AuditEventType {

		public static final AuditEventType APPLICATION_ACTIVITY = new AuditEventType("110100", SYS__DCM, "Application Activity");
		public static final AuditEventType PATIENT_RECORD = new AuditEventType("110110", SYS__DCM, "Patient Record");
		public static final AuditEventType RESTFUL_OPERATION = new AuditEventType("rest", SYS__AUDIT_EVENT_TYPE, "RESTful Operation");
		private final String code;
		private final String system;
		private final String display;

		AuditEventType(String code, String system, String display) {
			this.code = code;
			this.system = system;
			this.display = display;
		}

		public static AuditEventType newAuditEventType(String code, String system, String display) {
			return new AuditEventType(code, system, display);
		}

		public String getCode() {
			return code;
		}


		public String getDisplay() {
			return display;
		}

		public String getSystem() {
			return system;
		}
	}
}
