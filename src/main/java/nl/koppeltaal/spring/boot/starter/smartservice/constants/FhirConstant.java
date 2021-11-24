package nl.koppeltaal.spring.boot.starter.smartservice.constants;

import java.util.HashMap;
import java.util.Map;
import org.hl7.fhir.r4.model.ActivityDefinition;
import org.hl7.fhir.r4.model.CareTeam;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Endpoint;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.RelatedPerson;
import org.hl7.fhir.r4.model.Subscription;
import org.hl7.fhir.r4.model.Task;

public class FhirConstant {

  public static final String KT2_PROFILE__ACTIVITY_DEFINITION = "http://koppeltaal.nl/fhir/StructureDefinition/KT2ActivityDefinition";
  public static final String KT2_PROFILE__CARE_TEAM = "http://koppeltaal.nl/fhir/StructureDefinition/KT2CareTeam";
  public static final String KT2_PROFILE__DEVICE = "http://koppeltaal.nl/fhir/StructureDefinition/KT2Device";
  public static final String KT2_PROFILE__ENDPOINT = "http://koppeltaal.nl/fhir/StructureDefinition/KT2Endpoint";
  public static final String KT2_PROFILE__PATIENT = "http://koppeltaal.nl/fhir/StructureDefinition/KT2Patient";
  public static final String KT2_PROFILE__PRACTITIONER = "http://koppeltaal.nl/fhir/StructureDefinition/KT2Practitioner";
  public static final String KT2_PROFILE__RELATED_PERSON = "http://koppeltaal.nl/fhir/StructureDefinition/KT2RelatedPerson";
  public static final String KT2_PROFILE__SUBSCRIPTION = "http://koppeltaal.nl/fhir/StructureDefinition/KT2Subscription";
  public static final String KT2_PROFILE__TASK = "http://koppeltaal.nl/fhir/StructureDefinition/KT2Task";

  public static final Map<Class<? extends DomainResource>, String> CLASS_TO_PROFILE_MAP = new HashMap<>() {{
    put(ActivityDefinition.class, KT2_PROFILE__ACTIVITY_DEFINITION);
    put(CareTeam.class, KT2_PROFILE__CARE_TEAM);
    put(Device.class, KT2_PROFILE__DEVICE);
    put(Endpoint.class, KT2_PROFILE__ENDPOINT);
    put(Patient.class, KT2_PROFILE__PATIENT);
    put(Practitioner.class, KT2_PROFILE__PRACTITIONER);
    put(RelatedPerson.class, KT2_PROFILE__RELATED_PERSON);
    put(Subscription.class, KT2_PROFILE__SUBSCRIPTION);
    put(Task.class, KT2_PROFILE__TASK);
  }};

  public static final String KT2_EXTENSION__CARE_TEAM__OBSERVER = "http://koppeltaal.nl/fhir/StructureDefinition/KT2ObservationTeam";
  public final static String KT2_EXTENSION__ENDPOINT = "http://koppeltaal.nl/fhir/StructureDefinition/KT2EndpointExtension";
  public final static String KT2_EXTENSION__PUBLISHER_IDENTIFIER = "https://koppeltaal.nl/publisher-identifier";


  public final static String CODING_SYSTEM__SNOMED = "http://snomed.info/sct";
  public final static Coding CODING__SNOMED__PERSON = new Coding(CODING_SYSTEM__SNOMED, "125676002", "Person");
  public final static Coding CODING_SNOMED__HEALTHCARE_PROFESSIONAL = new Coding(CODING_SYSTEM__SNOMED, "223366009", "Healthcare professional");
}
