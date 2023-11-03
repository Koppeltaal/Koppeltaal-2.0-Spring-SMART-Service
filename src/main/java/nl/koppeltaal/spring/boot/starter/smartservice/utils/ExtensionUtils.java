package nl.koppeltaal.spring.boot.starter.smartservice.utils;

import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static nl.koppeltaal.spring.boot.starter.smartservice.constants.FhirConstant.KT2_EXTENSION__TASK__INSTANTIATES;

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

    public static Extension getInstantiatesExtension(String activityDefinitionReference) {
        final Reference instantiatesReference = new Reference();
        instantiatesReference.setReference(activityDefinitionReference);
        instantiatesReference.setType(ResourceType.ActivityDefinition.name());

        final Extension instantiatesExtension = new Extension();
        instantiatesExtension.setValue(instantiatesReference);
        instantiatesExtension.setUrl(KT2_EXTENSION__TASK__INSTANTIATES);

        return instantiatesExtension;
    }
}
