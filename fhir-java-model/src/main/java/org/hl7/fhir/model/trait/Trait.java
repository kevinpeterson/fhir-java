package org.hl7.fhir.model.trait;

import org.hl7.fhir.model.Element;
import org.hl7.fhir.model.Extension;
import org.hl7.fhir.model.impl.ExtensionImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 */
public class Trait {

    private static String SET_METHOD_PREFIX = "setValue";
    private static String GET_METHOD_PREFIX = "getValue";

    @SuppressWarnings("unchecked")
	public static <T extends Element,I extends T> I don(final T resource, Class<I> clazz) {
        InvocationHandler handler = new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                if(method.getName().endsWith("getTarget")) {
                    return resource;
                }

                if(method.isAnnotationPresent(FhirExtension.class)){
                    FhirExtension extension = (FhirExtension) method.getDeclaredAnnotations()[0];

                    String extensionUrl = extension.value();

                    if(method.getName().startsWith("set")) {

                        Extension e = findExtension(extensionUrl, resource.getExtension());
                        if(e == null) {
                            e = new ExtensionImpl();
                            e.setUrl(extensionUrl);

                            resource.getExtension().add(e);
                        }

                        Class<?> parameterClass = args[0].getClass();
                        String parameterName = method.getParameterTypes()[0].getSimpleName();

                        Method m = e.getClass().getDeclaredMethod(SET_METHOD_PREFIX + parameterName, parameterClass);
                        m.invoke(e, args[0]);
                    }

                    if(method.getName().startsWith("get")) {
                        Extension e = findExtension(extensionUrl, resource.getExtension());
                        if(e == null) {
                            return null;
                        } else {
                            Class<?> returnType = method.getReturnType();
                            Method m = e.getClass().getDeclaredMethod(GET_METHOD_PREFIX + returnType.getSimpleName());
                            return m.invoke(e);
                        }
                    }
                }

                else {
                    return method.invoke(resource, args);
                }

                return null;
            }

        };

        Proxied proxy = (Proxied) Proxy.newProxyInstance(resource.getClass().getClassLoader(), new Class[]{clazz, Proxied.class}, handler);

        return (I) proxy;
    }

    private static Extension findExtension(String url, Iterable<Extension> extensions) {
        for(Extension extension : extensions) {
            if(extension.getUrl().equals(url)) {
                return extension;
            }
        }

        return null;
    }

}
