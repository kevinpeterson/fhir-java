package org.hl7.fhir.model.xml;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.hl7.fhir.model.Resource;
import org.hl7.fhir.model.trait.Proxied;
import org.w3._2005.atom.FeedType;
import org.w3._2005.atom.impl.ContentTypeImpl;
import org.w3._2005.atom.impl.EntryTypeImpl;
import org.w3._2005.atom.impl.FeedTypeImpl;

public class FhirXmlMarshaller {
	
	private Marshaller marshaller;
	
	public FhirXmlMarshaller() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(
        		"com.a9.spec.opensearch.impl:" +
        		"com.a9.spec.opensearch.extensions.relevance.impl:" +
        		"org.hl7.fhir.model.impl:" +
        		"org.purl.atompub.tombstones._1.impl:" +
        		"org.w3._1999.xhtml.impl:" +
        		"org.w3._2000._09.xmldsig.impl:" +
        		"org.w3._2005.atom.impl"
        		);
        Marshaller marshaller = jaxbContext.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        
        this.marshaller = marshaller;
	}
	
	public void marshall(Object resource, OutputStream out) throws JAXBException {
		this.marshaller.marshal(this.unwrap(resource), out);
	}

	public void marshallAtom(List<? extends Resource> resources, OutputStream out) throws JAXBException {
		FeedType feed = new FeedTypeImpl();
        
		for(Resource resource : resources) {
			resource = this.unwrap(resource);
			
	        ContentTypeImpl c = new ContentTypeImpl();
	        c.getContent().add((Serializable) resource);
	        
	        EntryTypeImpl e = new EntryTypeImpl();
	        e.getTitleOrLinkOrId().add(new JAXBElement<ContentTypeImpl>(
	        		new QName("http://www.w3.org/2005/Atom", "content"), ContentTypeImpl.class, c));
	        feed.getTitleOrUpdatedOrId().add(e);
		}
		
		JAXBElement feedElement = new JAXBElement(
      		  new QName("http://www.w3.org/2005/Atom","feed"), FeedTypeImpl.class, feed );
		
		this.marshall(feedElement, out);
	}
	
	private <T> T unwrap(T resource) {
		if(resource instanceof Proxied) {
			return (T) ((Proxied)resource).getTarget();
		} else {
			return resource;
		}
	}
	
}
