package org.hl7.fhir.model.trait;

/**
 */
public interface Traitable<T> {

    <I extends T> I don(Class<I> extensionInterface);

}
