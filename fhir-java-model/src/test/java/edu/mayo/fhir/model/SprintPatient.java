package edu.mayo.fhir.model;

import org.hl7.fhir.model.trait.FhirExtension;

/**
 */
public interface SprintPatient extends MayoPatient {

    @FhirExtension("http://mayo.edu/sprint/SprintExtension")
    public String setSomething(String value);


}
