package edu.mayo.fhir.model;

import org.hl7.fhir.model.impl.PatientImpl;
import org.hl7.fhir.model.trait.FhirExtension;

public class MayoPatientImpl extends PatientImpl implements MayoPatient {

	private String myGreeting;
	
	@Override
	@FhirExtension("http://mayo.edu/sprint/KevinExtension")
	public String getMyGreeting() {
		return this.myGreeting;
	}

	@Override
	@FhirExtension("http://mayo.edu/sprint/KevinExtension")
	public void setMyGreeting(String value) {
		this.myGreeting = value;
	}

}
