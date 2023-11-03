package nl.koppeltaal.spring.boot.starter.smartservice.utils;

import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ExtensionUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ExtensionUtils.class);

    public static Optional<String> getReferenceValue(DomainResource resource, String extensionSystem) {
        Optional<Extension> instantiatesExtension = resource.getExtensionsByUrl(extensionSystem).stream().findFirst();

        if(instantiatesExtension.isPresent()) {
            return Optional.of(((Reference) instantiatesExtension.get().getValue()).getReference());
        }

        LOG.warn("No extension with system [{}] found on [{}]", extensionSystem, ResourceUtils.getReference(resource));
        return Optional.empty();
    }

}
