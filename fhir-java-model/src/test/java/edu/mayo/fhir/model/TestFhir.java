package edu.mayo.fhir.model;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.hl7.fhir.model.Patient;
import org.hl7.fhir.model.Procedure;
import org.hl7.fhir.model.impl.OrganizationImpl;
import org.hl7.fhir.model.impl.PatientImpl;
import org.hl7.fhir.model.impl.ProcedureImpl;
import org.junit.Test;
import org.w3._2005.atom.FeedType;
import org.w3._2005.atom.impl.ContentTypeImpl;
import org.w3._2005.atom.impl.EntryTypeImpl;
import org.w3._2005.atom.impl.FeedTypeImpl;

/**
 */
public class TestFhir {

    @Test
    public void testDoned() throws Exception {
        Patient p = new PatientImpl();

        SprintPatient mayoPatient = p.don(SprintPatient.class);

        mayoPatient.setUri("http://something");

        Procedure pr = new ProcedureImpl();
        pr.setSubjectResource(mayoPatient);

        mayoPatient.setMyGreeting("HI");
        mayoPatient.setSomething("HI");

        OrganizationImpl o = new OrganizationImpl();
        o.setUri("http://mayo.edu/");

        mayoPatient.setManagingOrganizationResource(o);

        System.out.println(pr.getSubject());
        System.out.println(mayoPatient.getMyGreeting());
        
        FeedType feed = new FeedTypeImpl();
        
        ContentTypeImpl c = new ContentTypeImpl();
        c.getContent().add(o);
        
        EntryTypeImpl e = new EntryTypeImpl();
        e.getTitleOrLinkOrId().add(new JAXBElement<ContentTypeImpl>(new QName("test"), ContentTypeImpl.class, c));
 
        
        feed.getTitleOrUpdatedOrId().add(e);

        JAXBContext jaxbContext = JAXBContext.newInstance(
        		"com.a9.spec.opensearch.impl:com.a9.spec.opensearch.extensions.relevance.impl:" +
        		"org.hl7.fhir.model.impl:org.purl.atompub.tombstones._1.impl:" +
        		"org.w3._1999.xhtml.impl:org.w3._2000._09.xmldsig.impl:" +
        		"org.w3._2005.atom.impl"
        		);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter w = new StringWriter();
        
        JAXBElement f = new JAXBElement(
        		  new QName("http://www.w3.org/2005/Atom","feed"), FeedTypeImpl.class, feed );

        jaxbMarshaller.marshal(f, w);

        System.out.println(w);
    }
}
