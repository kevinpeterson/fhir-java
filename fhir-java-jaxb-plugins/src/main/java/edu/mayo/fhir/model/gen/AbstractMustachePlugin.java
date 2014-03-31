package edu.mayo.fhir.model.gen;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sun.codemodel.JDefinedClass;

public abstract class AbstractMustachePlugin extends com.sun.tools.xjc.Plugin {
	
    private MustacheFactory mf = new DefaultMustacheFactory("template");
    
    protected void doWrite(JDefinedClass clazz, String template, Map<String,String> params) {
    	StringWriter sw = new StringWriter();
        Mustache mustache = this.mf.compile(template);
        try {
            mustache.execute(sw, params).flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        clazz.direct(sw.toString());
    }

}
