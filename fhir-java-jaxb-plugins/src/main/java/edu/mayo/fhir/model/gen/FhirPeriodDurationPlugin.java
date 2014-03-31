package edu.mayo.fhir.model.gen;

import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

import java.util.Collections;
import java.util.List;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public class FhirPeriodDurationPlugin extends AbstractMustachePlugin {
	
	private static final String PERIOD = "org.hl7.fhir.model.Period";

    public String getOptionName() {
        return "XfhirPeriodDuration";
    }

    public List getCustomizationURIs() {
        return Collections.singletonList("http://edu.mayo/fhir/PeriodDuration");
    }

    public boolean isCustomizationTagName(String nsUri, String localName) {
        return false;
    }

    public String getUsage() {
        return "  -XfhirPeriodDuration";
    }

    public boolean run(Outline outline, Options options, ErrorHandler errorHandler) throws SAXException {

        for (ClassOutline classOutline : outline.getClasses()) {
            if(classOutline.ref.binaryName().equals(PERIOD)) {
                this.doWrite(classOutline.ref, "periodDurationInterface.mustache", null);
                this.doWrite(classOutline.implClass, "periodDurationImpl.mustache", null);
            }
        }

        return true;
    }
}