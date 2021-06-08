package nl.koppeltaal.spring.boot.starter.smartservice.dto;

/**
 *
 */
public class OrgPersonDto extends PersonDto {
	String organization;

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}
}
