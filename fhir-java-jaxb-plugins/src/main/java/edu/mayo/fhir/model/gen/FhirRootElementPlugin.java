package edu.mayo.fhir.model.gen;

import com.sun.tools.xjc.outline.ClassOutline;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.List;

public class FhirRootElementPlugin extends AbstractFhirResourcePlugin {

    public String getOptionName() {
        return "XfhirRootElement";
    }

    public List<String> getCustomizationURIs() {
        return Collections.singletonList("http://edu.mayo/fhir/RootElement");
    }

    public boolean isCustomizationTagName(String nsUri, String localName) {
        return false;
    }

    public String getUsage() {
        return "  -XfhirRootElement";
    }

    @Override
    protected void doWithResource(ClassOutline classOutline) {
        classOutline.implClass.annotate(XmlRootElement.class).param("name", classOutline.ref.name());
    }
}