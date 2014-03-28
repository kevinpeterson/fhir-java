package edu.mayo.fhir.model.gen;

import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public abstract class AbstractFhirResourcePlugin extends com.sun.tools.xjc.Plugin {

    private static final String RESOURCE = "org.hl7.fhir.model.Resource";

    public boolean run(Outline outline, Options options, ErrorHandler errorHandler) throws SAXException {

        for (ClassOutline classOutline : outline.getClasses()) {
            if(this.isResource(classOutline) && !classOutline.ref.binaryName().equals(RESOURCE)) {
                this.doWithResource(classOutline);
            }
        }

        return true;
    }

    private boolean isResource(ClassOutline classOutline) {
        if(classOutline == null) {
            return false;
        } else if(classOutline.ref.binaryName().equals(RESOURCE)) {
            return true;
        } else {
            ClassOutline superClass = classOutline.getSuperClass();

            return isResource(superClass);
        }
    }

    protected abstract void doWithResource(ClassOutline classOutline);

}