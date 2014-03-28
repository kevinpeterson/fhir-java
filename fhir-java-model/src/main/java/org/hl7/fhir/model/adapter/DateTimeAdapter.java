package org.hl7.fhir.model.adapter;

import org.hl7.fhir.model.impl.DateTimeImpl;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 */
public class DateTimeAdapter extends XmlAdapter<org.hl7.fhir.model.DateTime,java.util.Date> {

    @Override
    public java.util.Date unmarshal(org.hl7.fhir.model.DateTime v) throws Exception {
        if(v != null) {
            return v.getValue();
        } else {
            return null;
        }
    }

    @Override
    public org.hl7.fhir.model.DateTime marshal(java.util.Date v) throws Exception {
        if(v != null) {
            DateTimeImpl b = new DateTimeImpl();
            b.setValue(v);

            return b;
        } else {
            return null;
        }
    }
}
