package edu.mayo.fhir.model.gen;

import com.sun.codemodel.*;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.*;
import java.util.Map.Entry;

public class FhirFieldRenamingPlugin extends com.sun.tools.xjc.Plugin {

   
    public String getOptionName() {
        return "XfhirFieldRenaming";
    }

    public List getCustomizationURIs() {
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

            	if(field.getValue().type().fullName().startsWith(List.class.getName())) {
            		continue;
            	}
            	
            	JType newType = outline.getCodeModel().ref(name.replaceAll("Impl$", "").replace(".impl", ""));
            	field.getValue().type(newType);
            }
        }

        return true;
    }

}