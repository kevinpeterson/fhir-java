package org.hl7.fhir.model.trait;

import org.hl7.fhir.model.Extension;

public final class TraitUtils {
	
    public static final String SET_METHOD_PREFIX = "setValue";
    public static final String GET_METHOD_PREFIX = "getValue";

    public static Extension findExtension(String url, Iterable<Extension> extensions) {
        for(Extension extension : extensions) {
            if(extension.getUrl().equals(url)) {
                return extension;
            }
        }

        return null;
    }

    
}
