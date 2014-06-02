package edu.mayo.fhir.model.gen;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

public class FhirFieldRenamingPlugin extends com.sun.tools.xjc.Plugin {

   
    public String getOptionName() {
        return "XfhirFieldRenaming";
    }

    public List<String> getCustomizationURIs() {
        return Collections.singletonList("http://edu.mayo/fhir/FhirFieldRenaming");
    }

    public boolean isCustomizationTagName(String nsUri, String localName) {
        return false;
    }

    public String getUsage() {
        return "  -XfhirFieldRenaming      :  Rename 'Impl' suffixed attributes.";
    }

    public boolean run(Outline outline, Options options, ErrorHandler errorHandler) throws SAXException {
       
        for (ClassOutline classOutline : outline.getClasses()) {
        	
            for (Entry<String, JFieldVar> field : classOutline.implClass.fields().entrySet()) {
            	String name = field.getValue().type().fullName();

            	if(field.getValue().type().fullName().startsWith("org.purl.atompub.tombstones") ||
            			field.getValue().type().fullName().startsWith(List.class.getName())) {
            		continue;
            	}
            	
            	JType newType = outline.getCodeModel().ref(name.replaceAll("Impl$", "").replace(".impl", ""));
            	field.getValue().type(newType);
            }
        }

        return true;
    }

}