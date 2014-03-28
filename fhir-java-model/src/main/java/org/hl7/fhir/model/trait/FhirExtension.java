package org.hl7.fhir.model.trait;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface FhirExtension {
    String value();
}