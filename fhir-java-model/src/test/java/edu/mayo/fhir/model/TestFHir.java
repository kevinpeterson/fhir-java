package edu.mayo.fhir.model;

import org.hl7.fhir.model.Organization;
import org.hl7.fhir.model.Patient;
import org.hl7.fhir.model.Procedure;
import org.hl7.fhir.model.impl.OrganizationImpl;
import org.hl7.fhir.model.impl.PatientImpl;
import org.hl7.fhir.model.impl.ProcedureImpl;
import org.hl7.fhir.model.trait.Proxied;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 */
public class TestFHir {

    @Test
    public void test() throws Exception {
        Patient p = new PatientImpl();

        SprintPatient mayoPatient = p.don(SprintPatient.class);

        mayoPatient.setUri("http://something");

        Procedure pr = new ProcedureImpl();
        pr.setSubjectResource(mayoPatient);

        mayoPatient.setMyGreeting("HI");
        mayoPatient.setSomething("HI");

        Organization o = new OrganizationImpl();
        o.setUri("http://mayo.edu/");

        mayoPatient.setManagingOrganizationResource(o);

        System.out.println(pr.getSubject());
        System.out.println(mayoPatient.getMyGreeting());

        JAXBContext jaxbContext = JAXBContext.newInstance(PatientImpl.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter w = new StringWriter();

        jaxbMarshaller.marshal(((Proxied)mayoPatient).getTarget(), w);

        System.out.println(w);

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Patient p2 = (Patient) jaxbUnmarshaller.unmarshal(new StringReader(w.toString()));

    }
}
