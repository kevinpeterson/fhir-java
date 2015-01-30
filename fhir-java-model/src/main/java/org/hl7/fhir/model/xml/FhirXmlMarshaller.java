package org.hl7.fhir.model.xml;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.beanutils.BeanUtils;
import org.hl7.fhir.model.Extension;
import org.hl7.fhir.model.Resource;
import org.hl7.fhir.model.impl.ExtensionImpl;
import org.hl7.fhir.model.trait.FhirExtension;
import org.hl7.fhir.model.trait.Proxied;
import org.hl7.fhir.model.trait.TraitUtils;
import org.w3._2005.atom.FeedType;
import org.w3._2005.atom.impl.ContentTypeImpl;
import org.w3._2005.atom.impl.EntryTypeImpl;
import org.w3._2005.atom.impl.FeedTypeImpl;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FhirXmlMarshaller {

	private MarshallerFactory marshallerFactory;
	private UnmarshallerFactory unmarshallerFactory;

    private interface MarshallerFactory {
        Marshaller getMarshaller();
    }

    private interface UnmarshallerFactory {
        Unmarshaller getUnmarshaller();
    }

    private static final LoadingCache<String, JAXBContext> jaxbContextsCache = CacheBuilder.newBuilder()
            .build(
                    new CacheLoader<String, JAXBContext>() {
                        @Override
                        public JAXBContext load(String extraContext) throws Exception {
                            JAXBContext jaxbContext = JAXBContext
                                    .newInstance("com.a9.spec.opensearch.impl:"
                                            + "com.a9.spec.opensearch.extensions.relevance.impl:"
                                            + "org.hl7.fhir.model.impl:"
                                            + "org.purl.atompub.tombstones._1.impl:"
                                            + "org.w3._1999.xhtml.impl:"
                                            + "org.w3._2000._09.xmldsig.impl:"
                                            + "org.w3._2005.atom.impl:" + extraContext);

                            return jaxbContext;
                        }
                    }
            );
	
	public FhirXmlMarshaller() throws JAXBException {
		this("");
	}
	
	public FhirXmlMarshaller(String extraContext) throws JAXBException {
        final JAXBContext jaxbContext;
        try {
            jaxbContext = jaxbContextsCache.get(extraContext);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

		this.marshallerFactory = new MarshallerFactory() {
            @Override
            public Marshaller getMarshaller() {
                Marshaller marshaller;
                try {
                    marshaller = jaxbContext.createMarshaller();
                    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                    marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-16");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                return marshaller;
            }
        };

        this.unmarshallerFactory = new UnmarshallerFactory() {
            @Override
            public Unmarshaller getUnmarshaller() {
                try {
                    return jaxbContext.createUnmarshaller();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
	}

	public void marshall(Object resource, OutputStream out)
			throws JAXBException {
		this.marshallerFactory.getMarshaller().marshal(this.unwrap(resource), out);
	}
	
	public <T> T unmarshall(InputStream in)
			throws JAXBException {
		return this.unmarshall(in, null);
	}
	
	public <T> T unmarshall(InputStream in, Class<T> clazz)
			throws JAXBException {
		Object obj = this.unmarshallerFactory.getUnmarshaller().unmarshal(in);
		if(obj instanceof JAXBElement) {
			obj = ((JAXBElement)obj).getValue();
		}
		if(clazz != null && obj.getClass() != clazz) {
			try {
				T newType = clazz.newInstance();
				
				BeanUtils.copyProperties(newType, obj);
				
				obj = this.unPopulateExtensions((Resource) newType);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		
		return (T) obj;
	}

	public void marshallAtom(List<? extends Resource> resources,
			OutputStream out) throws JAXBException {
		FeedType feed = new FeedTypeImpl();

		for (Resource resource : resources) {
			resource = this.unwrap(resource);

			ContentTypeImpl c = new ContentTypeImpl();
			c.getContent().add((Serializable) resource);

			EntryTypeImpl e = new EntryTypeImpl();
			e.getTitleOrLinkOrId().add(
					new JAXBElement<ContentTypeImpl>(new QName(
							"http://www.w3.org/2005/Atom", "content"),
							ContentTypeImpl.class, c));
			feed.getTitleOrUpdatedOrId().add(e);
		}

		JAXBElement feedElement = new JAXBElement(new QName(
				"http://www.w3.org/2005/Atom", "feed"), FeedTypeImpl.class,
				feed);

		this.marshall(feedElement, out);
	}

	private <T> T unwrap(T resource) {
		if (resource instanceof Proxied) {
			return (T) ((Proxied) resource).getTarget();
		} else if (resource instanceof Resource) {
			return (T) this.populateExtensions((Resource)resource);
		} else {
			return resource;
		}
	}

	protected Resource populateExtensions(Resource obj) {
		for (Method method : obj.getClass().getDeclaredMethods()) {

			if (method.isAnnotationPresent(FhirExtension.class)
					&& method.getName().startsWith("get")) {
				FhirExtension extension = (FhirExtension) method
						.getDeclaredAnnotations()[0];

				String extensionUrl = extension.value();

				Extension e = TraitUtils.findExtension(extensionUrl,
						obj.getExtension());
				if (e == null) {
					e = new ExtensionImpl();
					e.setUrl(extensionUrl);

					obj.getExtension().add(e);
				}

				Class<?> returnClass = method.getReturnType();
				String returnName = returnClass.getSimpleName();

				try {
					Method m = e.getClass().getDeclaredMethod(
							TraitUtils.SET_METHOD_PREFIX + returnName,
							returnClass);
					
					m.invoke(e, method.invoke(obj));
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		}
		
		return obj;
	}
	
	protected Resource unPopulateExtensions(Resource obj) {
		for(Extension e : obj.getExtension()) {
			String url = e.getUrl();
			for(Method m : obj.getClass().getDeclaredMethods()) {
				if(m.isAnnotationPresent(FhirExtension.class) && m.getName().startsWith("set")) {
					FhirExtension ext = m.getAnnotation(FhirExtension.class);
					if(ext.value().equals(url)) {			
						Class<?> paramClass = m.getParameterTypes()[0];
						String paramName = paramClass.getSimpleName();

						Object value;
						try {
							value = e.getClass().getDeclaredMethod(
										TraitUtils.GET_METHOD_PREFIX + paramName).invoke(e);

							m.invoke(obj, value);
						} catch (Exception ex) {
							throw new IllegalStateException(ex);
						}
					}
				}
			}
		}
		
		return obj;
	}

}
