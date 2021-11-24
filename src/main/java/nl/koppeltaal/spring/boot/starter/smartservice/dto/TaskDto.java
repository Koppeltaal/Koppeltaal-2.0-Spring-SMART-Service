package nl.koppeltaal.spring.boot.starter.smartservice.dto;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@SuppressWarnings("unused")
public class TaskDto extends BaseIdentifierDto {
	String activityDefinition;
	String patient;
	String practitioner;
	String status;
	List<String> observerReferences = new ArrayList<>();

	public String getActivityDefinition() {
		return activityDefinition;
	}

	public void setActivityDefinition(String activityDefinition) {
		this.activityDefinition = activityDefinition;
	}

	public String getPatient() {
		return patient;
	}

	public void setPatient(String patient) {
		this.patient = patient;
	}

	public String getPractitioner() {
		return practitioner;
	}

	public void setPractitioner(String practitioner) {
		this.practitioner = practitioner;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<String> getObserverReferences() {
		return observerReferences;
	}

	public void setObserverReferences(
			List<String> observerReferences) {
		this.observerReferences = observerReferences;
	}
}
