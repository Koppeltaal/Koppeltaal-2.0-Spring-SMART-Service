package nl.koppeltaal.spring.boot.starter.smartservice.dto;

import org.hl7.fhir.r4.model.Coding;

public class CodingDto {
  private String system;
  private String code;
  private String display;
  private String version;

  public CodingDto(Coding coding) {

    this.system = coding.getSystem();
    this.code = coding.getCode();
    this.display = coding.getDisplay();
    this.version = coding.getVersion();
  }

  public String getSystem() {
    return system;
  }

  public void setSystem(String system) {
    this.system = system;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDisplay() {
    return display;
  }

  public void setDisplay(String display) {
    this.display = display;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public String toString() {
    return "CodingDto{" +
        "system='" + system + '\'' +
        ", code='" + code + '\'' +
        ", display='" + display + '\'' +
        ", version='" + version + '\'' +
        '}';
  }
}
