package edu.mayo.fhir.model;

import org.hl7.fhir.model.Patient;
import org.hl7.fhir.model.trait.FhirExtension;

/**
 */
public interface MayoPatient extends Patient {

    @FhirExtension("http://mayo.edu/sprint/KevinExtension")
    public String getMyGreeting();

    @FhirExtension("http://mayo.edu/sprint/KevinExtension")
    public String setMyGreeting(String value);

}
