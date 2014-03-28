package org.hl7.fhir.model.adapter;

import org.hl7.fhir.model.impl.StringImpl;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 */
public class StringAdapter extends XmlAdapter<org.hl7.fhir.model.String,java.lang.String> {

    @Override
    public java.lang.String unmarshal(org.hl7.fhir.model.String v) throws Exception {
        if(v != null) {
            return v.getValue();
        } else {
            return null;
        }
    }

    @Override
    public org.hl7.fhir.model.String marshal(java.lang.String v) throws Exception {
        if(v != null) {
            StringImpl b = new StringImpl();
            b.setValue(v);

            return b;
        } else {
            return null;
        }
    }
}
