package org.hl7.fhir.model.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.hl7.fhir.model.impl.DateImpl;

/**
 */
public class DateAdapter extends XmlAdapter<org.hl7.fhir.model.Date,java.util.Date> {
	
	private DateFormat dateFormat = new SimpleDateFormat();

    @Override
    public java.util.Date unmarshal(org.hl7.fhir.model.Date v) throws Exception {
        if(v != null) {
            return dateFormat.parse(v.getValue());
        } else {
            return null;
        }
    }

    @Override
    public org.hl7.fhir.model.Date marshal(java.util.Date v) throws Exception {
        if(v != null) {
            DateImpl b = new DateImpl();
            b.setValue(dateFormat.format(v));

            return b;
        } else {
            return null;
        }
    }
}
