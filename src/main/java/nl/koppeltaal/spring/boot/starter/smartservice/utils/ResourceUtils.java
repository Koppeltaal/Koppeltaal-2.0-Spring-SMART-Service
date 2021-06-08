package nl.koppeltaal.spring.boot.starter.smartservice.utils;

import org.hl7.fhir.instance.model.api.IAnyResource;
import org.hl7.fhir.instance.model.api.IIdType;

/**
 *
 */
public class ResourceUtils {
	public static String getReference(IAnyResource resource) {
		IIdType idElement = resource.getIdElement();
		if (idElement != null) {
			return String.format("%s/%s", idElement.getResourceType(), idElement.getIdPart());
		}
		return null;
	}
}
