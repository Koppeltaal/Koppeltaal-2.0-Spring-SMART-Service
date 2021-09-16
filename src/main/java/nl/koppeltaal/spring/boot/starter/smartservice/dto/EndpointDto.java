package nl.koppeltaal.spring.boot.starter.smartservice.dto;

import java.util.List;

/**
 *
 */
@SuppressWarnings("unused")
public class EndpointDto extends BaseDto {
	String name;
	String address;
	String status;
	CodingDto connectionType;
	List<CodeableConceptDto> payloadType;
	String managingOrganization;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public CodingDto getConnectionType() {
		return connectionType;
	}

	public void setConnectionType(CodingDto connectionType) {
		this.connectionType = connectionType;
	}

	public String getManagingOrganization() {
		return managingOrganization;
	}

	public void setManagingOrganization(String managingOrganization) {
		this.managingOrganization = managingOrganization;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<CodeableConceptDto> getPayloadType() {
		return payloadType;
	}

	public void setPayloadType(List<CodeableConceptDto> payloadType) {
		this.payloadType = payloadType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
