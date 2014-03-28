package org.hl7.fhir.model.adapter;

import org.hl7.fhir.model.impl.BooleanImpl;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 */
public class BooleanAdapter extends XmlAdapter<org.hl7.fhir.model.Boolean,java.lang.Boolean> {

    @Override
    public java.lang.Boolean unmarshal(org.hl7.fhir.model.Boolean v) throws Exception {
        if(v != null) {
            return v.isValue();
        } else {
            return null;
        }
    }

    @Override
    public org.hl7.fhir.model.Boolean marshal(java.lang.Boolean v) throws Exception {
        if(v != null) {
            BooleanImpl b = new BooleanImpl();
            b.setValue(v);

            return b;
        } else {
            return null;
        }
    }
}
