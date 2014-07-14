package edu.mayo.fhir.model.xml;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;

import org.apache.commons.io.IOUtils;
import org.hl7.fhir.model.xml.FhirXmlMarshaller;
import org.junit.Test;

import edu.mayo.fhir.model.MayoPatientImpl;

public class TestFhirXmlMarshaller {

	@Test
	public void testMarshalImpl() throws Exception {
		FhirXmlMarshaller m = new FhirXmlMarshaller();

		MayoPatientImpl impl = new MayoPatientImpl();
		impl.setMyGreeting("hi");
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		m.marshall(impl, out);
		
		System.out.println(out.toString("UTF-16"));
		
		assertTrue(out.toString("UTF-16").contains("<valueString value=\"hi\"/>"));		
	}
	
	@Test
	public void testMarshalAndUnmarshalImpl() throws Exception {
		FhirXmlMarshaller m = new FhirXmlMarshaller();

		MayoPatientImpl impl = new MayoPatientImpl();
		impl.setMyGreeting("hi");
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		m.marshall(impl, out);

		MayoPatientImpl returnedImpl = 
				m.unmarshall(IOUtils.toInputStream(out.toString("UTF-16"), "UTF-16"), MayoPatientImpl.class);
	
		assertEquals("hi", returnedImpl.getMyGreeting());
	}
}
