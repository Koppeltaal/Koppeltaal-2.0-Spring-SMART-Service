package nl.koppeltaal.spring.boot.starter.smartservice.dto;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.CodeType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.StringType;

public class PersonDtoConverter {

    void fillHumanName(HumanName humanName, PersonDto personDto) {

        humanName.setUse(HumanName.NameUse.OFFICIAL);
        humanName.setFamily(personDto.getNameFamily());
        if (StringUtils.isNotEmpty(personDto.getNameGiven())) {
            humanName.getGiven().clear();
            for (String givenName : StringUtils.split(personDto.getNameGiven())) {
                StringType givenElement = humanName.addGivenElement();
                givenElement.setValue(givenName);
                givenElement.addExtension(new Extension("http://hl7.org/fhir/StructureDefinition/iso21090-EN-qualifier", new CodeType("IN"))); //FIXME: Just for POC purposes, hard coded value
            }
        }
    }

}
