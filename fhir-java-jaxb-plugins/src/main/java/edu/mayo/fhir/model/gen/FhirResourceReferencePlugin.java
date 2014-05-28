package edu.mayo.fhir.model.gen;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sun.codemodel.*;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import javax.xml.bind.annotation.XmlTransient;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

public class FhirResourceReferencePlugin extends com.sun.tools.xjc.Plugin {

    private MustacheFactory mf = new DefaultMustacheFactory("template");

    public String getOptionName() {
        return "XfhirResourceReference";
    }

    public List getCustomizationURIs() {
        return Collections.singletonList("http://edu.mayo/fhir/ResourceReference");
    }

    public boolean isCustomizationTagName(String nsUri, String localName) {
        return false;
    }

    public String getUsage() {
        return "  -XfhirResourceReference ";
    }

    public boolean run(Outline outline, Options options, ErrorHandler errorHandler) throws SAXException {
        JType resourceJType = outline.getCodeModel().ref("org.hl7.fhir.model.Resource");

        for (ClassOutline classOutline : outline.getClasses()) {
            if(classOutline.ref.binaryName().equals("org.hl7.fhir.model.ResourceReference")) {

                for (JDefinedClass clazz : Arrays.asList(classOutline.implClass, classOutline.ref)) {
                    JMethod getter = clazz.method(JMod.PUBLIC, resourceJType, "getResource");
                    JMethod setter = clazz.method(JMod.PUBLIC, classOutline.parent().getCodeModel().VOID, "setResource");
                    JVar var = setter.param(resourceJType, "value");
                    
                    if (!clazz.isInterface()) {
                        JFieldVar resource = clazz.field(JMod.PROTECTED, resourceJType, "resource");
                        resource.annotate(XmlTransient.class);
                        getter.body()._return(JExpr.refthis(resource.name()));
                        setter.body().assign(JExpr.refthis(resource.name()), var);
                    } 
                }
            }
        }
        for (ClassOutline classOutline : outline.getClasses()) {
            for (FieldOutline fieldOutline : classOutline.getDeclaredFields()) {
                String fieldName = fieldOutline.getRawType().binaryName();

                if(fieldName.equals("org.hl7.fhir.model.ResourceReference")) {
                    JFieldVar jf =
                            classOutline.implClass.fields().get(
                                    fieldOutline.getPropertyInfo().getName(false));

                    String name = jf.name().startsWith("_") ? jf.name().substring(1) : jf.name();
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                    JType referenceJType = classOutline.parent().getCodeModel().ref("org.hl7.fhir.model.Resource");

                    for (JDefinedClass clazz : Arrays.asList(classOutline.implClass, classOutline.ref)) {
                        JMethod setter = clazz.method(JMod.PUBLIC, classOutline.parent().getCodeModel().VOID, "set" + name + "Resource");
                        setter.param(referenceJType, "value");

                        if (!clazz.isInterface()) {

                            StringWriter sw = new StringWriter();
                            Mustache mustache = this.mf.compile("resourceReference.mustache");
                            try {
                                HashMap<String, String> params = new HashMap<String, String>();
                                params.put("field", jf.name());
                                mustache.execute(sw, params).flush();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            setter.body().directStatement(sw.toString());
                        }
                    }
                }
            }
        }

        return true;
    }

}