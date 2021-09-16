package nl.koppeltaal.spring.boot.starter.smartservice.dto;

import java.util.ArrayList;
import java.util.List;

public class CodeableConceptDto {

  List<CodingDto> codings = new ArrayList<>();

  public void addCoding(CodingDto codingDto) {
    this.codings.add(codingDto);
  }

  public List<CodingDto> getCodings() {
    return codings;
  }

  public void setCodings(
      List<CodingDto> codings) {
    this.codings = codings;
  }
}
