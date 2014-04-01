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

public class FhirPrimitiveTypesUnwrapperPlugin extends com.sun.tools.xjc.Plugin {

    private static final String ADAPTER_PACKAGE = "org.hl7.fhir.model.adapter";

    private static final Hl7Primitive HL7_STRING =
            new Hl7Primitive("org.hl7.fhir.model.String", String.class, "org.hl7.fhir.model.adapter.StringAdapter");

    private static final Hl7Primitive HL7_BOOLEAN =
            new Hl7Primitive("org.hl7.fhir.model.Boolean", Boolean.class, "org.hl7.fhir.model.adapter.BooleanAdapter");

    private static final Hl7Primitive HL7_INTEGER =
            new Hl7Primitive("org.hl7.fhir.model.Integer", Integer.class, "org.hl7.fhir.model.adapter.IntegerAdapter");

    private static final Hl7Primitive HL7_DATETIME =
            new Hl7Primitive("org.hl7.fhir.model.DateTime", Date.class, "org.hl7.fhir.model.adapter.DateTimeAdapter");
    
    private static final Hl7Primitive HL7_DATE =
            new Hl7Primitive("org.hl7.fhir.model.Date", Date.class, "org.hl7.fhir.model.adapter.DateAdapter");

    public String getOptionName() {
        return "XfhirPrimitives";
    }

    public List getCustomizationURIs() {
        return Collections.singletonList("http://edu.mayo/fhir/PrimitiveTypesUnwrapper");
    }

    public boolean isCustomizationTagName(String nsUri, String localName) {
        return false;
    }

    public String getUsage() {
        return "  -XfhirPrimitives      :  Unwrap HL7 FHIR primitive types.";
    }

    public boolean run(Outline outline, Options options, ErrorHandler errorHandler) throws SAXException {
        Map<String,Hl7Primitive> primitiveMap = new HashMap<String,Hl7Primitive>();
        for(Hl7Primitive primitive :
                Arrays.asList(HL7_STRING, HL7_BOOLEAN, HL7_INTEGER, HL7_DATETIME, HL7_DATE)) {
            primitiveMap.put(primitive.hl7Type, primitive);
        }

        for (ClassOutline classOutline : outline.getClasses()) {
            for (FieldOutline fieldOutline : classOutline.getDeclaredFields()) {
                String fieldName = fieldOutline.getRawType().binaryName();
                if (primitiveMap.containsKey(fieldName)) {
                    Hl7Primitive primitive = primitiveMap.get(fieldName);

                    JFieldVar jf =
                            classOutline.implClass.fields().get(
                                    fieldOutline.getPropertyInfo().getName(false));

                    JAnnotationUse use = jf.annotate(XmlJavaTypeAdapter.class);

                    use.param("value", classOutline.parent().getCodeModel().ref(primitive.adapter));

                    JType setType = classOutline.parent().getCodeModel().ref(primitive.primitive);
                    jf.type(setType);

                    this.replaceGetter(classOutline, jf);
                }
            }
        }

        return true;
    }

    private void replaceGetter(ClassOutline co, JFieldVar f) {
        //Create the method name
        String get = "get";
        String set = "set";
        String name = f.name().startsWith("_") ? f.name().substring(1) : f.name();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);

        String getMethodName = get + name;
        String setMethodName = set + name;

        for (JDefinedClass clazz : Arrays.asList(co.implClass, co.ref)) {
            //Find and remove Old Getter
            JMethod oldGetter = clazz.getMethod(getMethodName, new JType[0]);
            clazz.methods().remove(oldGetter);

            //Create New Getter
            JMethod getter = clazz.method(JMod.PUBLIC, f.type(), getMethodName);

            if (!clazz.isInterface()) {
                getter.body()._return(JExpr.ref(f.name()));
            }
        }

        for (JDefinedClass clazz : Arrays.asList(co.implClass, co.ref)) {
            //Find and remove Old Setter
            JMethod oldSetter = this.findSetter(clazz.methods(), setMethodName);
            clazz.methods().remove(oldSetter);

            //Create New Setter
            JMethod setter = clazz.method(JMod.PUBLIC, co.parent().getCodeModel().VOID, setMethodName);
            JVar var = setter.param(f.type(), "value");

            if (!clazz.isInterface()) {
                setter.body().assign(JExpr.refthis(f.name()), var);
            }
        }
    }

    private JMethod findSetter(Collection<JMethod> methods, String setterName) {
        for (JMethod method : methods) {
            if (method.name().equals(setterName)) {
                return method;
            }
        }

        return null;
    }

    private static class Hl7Primitive {

        private String hl7Type;
        private Class<?> primitive;
        private String adapter;

        private Hl7Primitive(String hl7Type, Class<?> primitive, String adapter) {
            this.hl7Type = hl7Type;
            this.primitive = primitive;
            this.adapter = adapter;
        }
    }

}