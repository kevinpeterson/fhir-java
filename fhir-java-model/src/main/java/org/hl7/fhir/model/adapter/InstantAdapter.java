package org.hl7.fhir.model.adapter;

import java.util.GregorianCalendar;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.hl7.fhir.model.Instant;
import org.hl7.fhir.model.impl.InstantImpl;

/**
 */
public class InstantAdapter extends XmlAdapter<org.hl7.fhir.model.Instant,java.util.Date> {

    @Override
    public java.util.Date unmarshal(Instant v) throws Exception {
        if(v != null) {
            return v.getValue().toGregorianCalendar().getTime();
        } else {
            return null;
        }
    }

    @Override
    public Instant marshal(java.util.Date v) throws Exception {
        if(v != null) {
        	InstantImpl b = new InstantImpl();
        	
        	GregorianCalendar c = new GregorianCalendar();
        	c.setTime(v);
        	XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        	
            b.setValue(date);

            return b;
        } else {
            return null;
        }
    }
}
