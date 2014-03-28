package edu.mayo.fhir.model.gen;

import com.sun.tools.xjc.outline.ClassOutline;

import java.util.Collections;
import java.util.List;

public class FhirTraitablePlugin extends AbstractFhirResourcePlugin {

    public String getOptionName() {
        return "XfhirTraitable";
    }

    public List getCustomizationURIs() {
        return Collections.singletonList("http://edu.mayo/fhir/Traitable");
    }

    public boolean isCustomizationTagName(String nsUri, String localName) {
        return false;
    }

    public String getUsage() {
        return "  -XfhirTraitable";
    }

    @Override
    protected void doWithResource(ClassOutline classOutline) {
        try {
            classOutline.ref._implements(
                    classOutline.parent().
                            getCodeModel().ref("org.hl7.fhir.model.trait.Traitable").
                            erasure().narrow(classOutline.ref));


            String name = classOutline.ref.binaryName();
            classOutline.implClass.direct(
                    "\r\n" +
                    "public <I extends " + name + "> I don(Class<I> extensionInterface) {\r\n" +
                    "   return org.hl7.fhir.model.trait.Trait.don( (" + name + ") this, extensionInterface);\r\n" +
                    "}\r\n"
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}