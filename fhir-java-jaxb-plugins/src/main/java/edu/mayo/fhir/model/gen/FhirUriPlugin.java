package edu.mayo.fhir.model.gen;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMod;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import javax.xml.bind.annotation.XmlTransient;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

public class FhirUriPlugin extends AbstractFhirResourcePlugin {

    private static final String IDENTIFIER_FIELD_NAME = "identifier";

    private MustacheFactory mf = new DefaultMustacheFactory("template");

    public String getOptionName() {
        return "XfhirUri";
    }

    public List getCustomizationURIs() {
        return Collections.singletonList("http://edu.mayo/fhir/Uri");
    }

    public boolean isCustomizationTagName(String nsUri, String localName) {
        return false;
    }

    public String getUsage() {
        return "  -XfhirUri ";
    }

    public boolean run(Outline outline, Options options, ErrorHandler errorHandler) throws SAXException {
        JClass uriIdentified = outline.getCodeModel().ref("org.hl7.fhir.model.UriIdentified");
        for (ClassOutline classOutline : outline.getClasses()) {
            if(classOutline.ref.binaryName().equals("org.hl7.fhir.model.ResourceReference")) {
                StringWriter sw = new StringWriter();
                Mustache mustache = this.mf.compile("resourceReferenceUriImpl.mustache");
                try {
                    mustache.execute(sw, null).flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                classOutline.implClass.direct(sw.toString());
                JFieldVar uriField = classOutline.implClass.field(JMod.PROTECTED, String.class, "uri");
                uriField.annotate(XmlTransient.class);

                sw = new StringWriter();
                mustache = this.mf.compile("resourceReferenceUriInterface.mustache");
                try {
                    mustache.execute(sw, null).flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                classOutline.ref.direct(sw.toString());
                classOutline.ref._implements(uriIdentified);
            }
        }

        return super.run(outline, options, errorHandler);
    }

    @Override
    protected void doWithResource(ClassOutline classOutline) {
        boolean isCollection = false;
        boolean hasIdentifiers = false;
        for (FieldOutline fieldOutline : classOutline.getDeclaredFields()) {
            String fieldName = fieldOutline.getPropertyInfo().getName(false);
            if(fieldName.equals(IDENTIFIER_FIELD_NAME) &&
                    (fieldOutline.getRawType().fullName().equals("org.hl7.fhir.model.Identifier") ||
                        fieldOutline.getPropertyInfo().isCollection())) {
                hasIdentifiers = true;
                isCollection = fieldOutline.getPropertyInfo().isCollection();
                break;
            }
        }

        if(! hasIdentifiers) {
            return;
        }

        StringWriter sw = new StringWriter();
        String template = isCollection ? "resourceUriImplList.mustache" : "resourceUriImplSingle.mustache";
        Mustache mustache = this.mf.compile(template);
        try {
            mustache.execute(sw, null).flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        classOutline.implClass.direct(sw.toString());

        sw = new StringWriter();
        mustache = this.mf.compile("resourceUriInterface.mustache");
        try {
            mustache.execute(sw, null).flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        classOutline.ref.direct(sw.toString());
    }


}