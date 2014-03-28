package org.hl7.fhir.model.adapter;

import org.hl7.fhir.model.impl.IntegerImpl;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 */
public class IntegerAdapter extends XmlAdapter<org.hl7.fhir.model.Integer,java.lang.Integer> {

    @Override
    public java.lang.Integer unmarshal(org.hl7.fhir.model.Integer v) throws Exception {
        if(v != null) {
            return v.getValue();
        } else {
            return null;
        }
    }

    @Override
    public org.hl7.fhir.model.Integer marshal(java.lang.Integer v) throws Exception {
        if(v != null) {
            IntegerImpl b = new IntegerImpl();
            b.setValue(v);

            return b;
        } else {
            return null;
        }
    }
}
