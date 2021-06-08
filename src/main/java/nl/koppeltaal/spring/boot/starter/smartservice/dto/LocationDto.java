package nl.koppeltaal.spring.boot.starter.smartservice.dto;

/**
 *
 */
@SuppressWarnings("unused")
public class LocationDto extends BaseDto {
	String endpoint;

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
}
