/*
 * This file is part of the ONT API.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright (c) 2019, The University of Manchester, owl.cs group.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package ru.avicomp.ontapi.jena.utils;

import org.apache.jena.datatypes.BaseDatatype;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.TypeMapper;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.vocabulary.DC;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import ru.avicomp.ontapi.jena.OntJenaException;
import ru.avicomp.ontapi.jena.vocabulary.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Helper to work with constants from {@link ru.avicomp.ontapi.jena.vocabulary} and {@link org.apache.jena.vocabulary} packages.
 * The access provided through {@link Vocabulary} interface.
 * TODO: move to another place ?
 * <p>
 * Created by @szuev on 21.12.2016.
 */
@SuppressWarnings("WeakerAccess")
public class BuiltIn {

    public static final Vocabulary DUMMY = new Empty();
    public static final Vocabulary OWL_VOCABULARY = new OWLVocabulary();
    public static final Vocabulary DC_VOCABULARY = new DCVocabulary();
    public static final Vocabulary SKOS_VOCABULARY = new SKOSVocabulary();
    public static final Vocabulary SWRL_VOCABULARY = new SWRLVocabulary();
    public static final Vocabulary OWL_SKOS_DC_VOCABULARY = MultiVocabulary.create(OWL_VOCABULARY,
            DC_VOCABULARY, SKOS_VOCABULARY, SWRL_VOCABULARY);

    protected static Vocabulary defaultVocabulary = OWL_SKOS_DC_VOCABULARY;

    /**
     * Gets a system-wide vocabulary.
     *
     * @return {@link Vocabulary}
     */
    public static Vocabulary get() {
        return defaultVocabulary;
    }

    /**
     * Sets a new system-wide vocabulary.
     *
     * @param vocabulary {@link Vocabulary}, not {@code null}
     * @return {@link Vocabulary}
     */
    public static Vocabulary set(Vocabulary vocabulary) {
        Vocabulary prev = get();
        defaultVocabulary = OntJenaException.notNull(vocabulary, "Null vocabulary specified.");
        return prev;
    }

    private static Stream<Field> directFields(Class vocabulary, Class<?> type) {
        return Arrays.stream(vocabulary.getDeclaredFields()).
                filter(field -> Modifier.isPublic(field.getModifiers())).
                filter(field -> Modifier.isStatic(field.getModifiers())).
                filter(field -> type.equals(field.getType()));
    }

    private static Stream<Field> fields(Class vocabulary, Class<?> type) {
        Stream<Field> res = directFields(vocabulary, type);
        return vocabulary.getSuperclass() != null ? Stream.concat(res, fields(vocabulary.getSuperclass(), type)) : res;
    }

    private static <T> Stream<T> constants(Class vocabulary, Class<T> type) {
        return fields(vocabulary, type).map(field -> getValue(field, type)).filter(Objects::nonNull);
    }

    private static <T> T getValue(Field field, Class<T> type) {
        try {
            return type.cast(field.get(null));
        } catch (IllegalAccessException e) {
            throw new OntJenaException(e);
        }
    }

    protected static <T> Set<T> getConstants(Class<? extends T> type, Class... vocabularies) {
        return Arrays.stream(vocabularies)
                .map(voc -> constants(voc, type)).flatMap(Function.identity()).collect(Iter.toUnmodifiableSet());
    }

    /**
     * The point to access to the built-in resources and properties.
     * All methods must return unmodifiable collections.
     * <p>
     * Created by @szuev on 04.04.2017.
     */
    public interface Vocabulary {

        /**
         * Returns a collection of all built-in properties
         * with implicit {@code rdf:type} equal to {@link OWL#AnnotationProperty owl:AnnotationProperty}.
         *
         * @return {@code Set} of {@link Property Properties}
         */
        Set<Property> annotationProperties();

        /**
         * Returns a collection of all built-in properties
         * with implicit {@code rdf:type} equal to {@link OWL#DatatypeProperty owl:DatatypeProperty}.
         *
         * @return {@code Set} of {@link Property Properties}
         */
        Set<Property> datatypeProperties();

        /**
         * Returns a collection of all built-in properties
         * with implicit {@code rdf:type} equal to {@link OWL#ObjectProperty owl:ObjectProperty}.
         *
         * @return {@code Set} of {@link Property Properties}
         */
        Set<Property> objectProperties();

        /**
         * Returns a collection of all built-in uri-resources
         * with implicit {@code rdf:type} equal to {@link RDFS#Datatype rdfs:Datatype}.
         *
         * @return {@code Set} of {@link Resource Resources}
         */
        Set<Resource> datatypes();

        /**
         * Returns a collection of all built-in uri resources
         * with implicit {@code rdf:type} equal to {@link OWL#Class owl:Class}.
         *
         * @return {@code Set} of {@link Resource Resources}
         */
        Set<Resource> classes();

        /**
         * Returns a collection of all built-in uri resources
         * with implicit {@code rdf:type} equal to {@link SWRL#Builtin swrl:Builtin}.
         *
         * @return {@code Set} of {@link Resource Resources}
         */
        default Set<Resource> swrlBuiltins() {
            return Collections.emptySet();
        }

        /**
         * Returns all reserved resources:
         * OWL entities can not have an uri belonging to the return collection.
         *
         * @return {@code Set} of {@link Resource Resources}
         */
        Set<Resource> reservedResources();

        /**
         * Returns all reserved properties:
         * OWL2 ontology can not contain assertion with predicate belonging to the return collection.
         *
         * @return {@code Set} of {@link Property Properties}
         */
        Set<Property> reservedProperties();

        default Set<Resource> reserved() {
            return Stream.of(reservedProperties(), reservedResources())
                    .flatMap(Collection::stream).collect(Iter.toUnmodifiableSet());
        }

        default Set<Property> properties() {
            return Stream.of(annotationProperties(), datatypeProperties(), objectProperties())
                    .flatMap(Collection::stream).collect(Iter.toUnmodifiableSet());
        }

        default Set<Resource> entities() {
            return Stream.of(classes(), datatypes(), properties())
                    .flatMap(Collection::stream).collect(Iter.toUnmodifiableSet());
        }
    }

    /**
     * Empty Vocabulary.
     */
    public static class Empty implements Vocabulary {

        @Override
        public Set<Property> annotationProperties() {
            return Collections.emptySet();
        }

        @Override
        public Set<Property> datatypeProperties() {
            return Collections.emptySet();
        }

        @Override
        public Set<Property> objectProperties() {
            return Collections.emptySet();
        }

        @Override
        public Set<Resource> datatypes() {
            return Collections.emptySet();
        }

        @Override
        public Set<Resource> classes() {
            return Collections.emptySet();
        }

        @Override
        public Set<Resource> reservedResources() {
            return Collections.emptySet();
        }

        @Override
        public Set<Property> reservedProperties() {
            return Collections.emptySet();
        }
    }

    /**
     * Access to the {@link OWL OWL2} vocabulary.
     */
    @SuppressWarnings("WeakerAccess")
    public static class OWLVocabulary implements Vocabulary {
        private static final Class[] VOCABULARIES = new Class[]{XSD.class, RDF.class, RDFS.class, OWL.class};
        public static final Set<Property> ALL_PROPERTIES = getConstants(Property.class, VOCABULARIES);
        public static final Set<Resource> ALL_RESOURCES = getConstants(Resource.class, VOCABULARIES);
        /**
         * The list of datatypes from owl-2 specification (35 types)
         * (see <a href='https://www.w3.org/TR/owl2-quick-reference/'>Quick References, 3.1 Built-in Datatypes</a>).
         * It seems it is not full:
         */
        public static final Set<Resource> OWL2_DATATYPES =
                Stream.of(RDF.xmlLiteral, RDF.PlainLiteral, RDF.langString,
                        RDFS.Literal, OWL.real, OWL.rational, XSD.xstring, XSD.normalizedString,
                        XSD.token, XSD.language, XSD.Name, XSD.NCName, XSD.NMTOKEN, XSD.decimal, XSD.integer,
                        XSD.xdouble, XSD.xfloat, XSD.xboolean,
                        XSD.nonNegativeInteger, XSD.nonPositiveInteger, XSD.positiveInteger, XSD.negativeInteger,
                        XSD.xlong, XSD.xint, XSD.xshort, XSD.xbyte,
                        XSD.unsignedLong, XSD.unsignedInt, XSD.unsignedShort, XSD.unsignedByte,
                        XSD.hexBinary, XSD.base64Binary,
                        XSD.anyURI, XSD.dateTime, XSD.dateTimeStamp
                ).collect(Iter.toUnmodifiableSet());
        public static final Set<RDFDatatype> JENA_RDF_DATATYPE_SET = initBuiltInRDFDatatypes(TypeMapper.getInstance());

        public static final Set<Resource> DATATYPES = JENA_RDF_DATATYPE_SET.stream().map(RDFDatatype::getURI).
                map(ResourceFactory::createResource).collect(Iter.toUnmodifiableSet());
        public static final Set<Resource> CLASSES = Stream.of(OWL.Nothing, OWL.Thing).collect(Iter.toUnmodifiableSet());

        public static final Set<Property> ANNOTATION_PROPERTIES =
                Stream.of(RDFS.label, RDFS.comment, RDFS.seeAlso, RDFS.isDefinedBy, OWL.versionInfo,
                        OWL.backwardCompatibleWith, OWL.priorVersion, OWL.incompatibleWith, OWL.deprecated)
                        .collect(Iter.toUnmodifiableSet());
        public static final Set<Property> DATA_PROPERTIES =
                Stream.of(OWL.topDataProperty, OWL.bottomDataProperty).collect(Iter.toUnmodifiableSet());
        public static final Set<Property> OBJECT_PROPERTIES =
                Stream.of(OWL.topObjectProperty, OWL.bottomObjectProperty).collect(Iter.toUnmodifiableSet());

        private static Set<RDFDatatype> initBuiltInRDFDatatypes(TypeMapper types) {
            Stream.of(OWL.real, OWL.rational).forEach(d -> types.registerDatatype(new BaseDatatype(d.getURI())));
            OWLVocabulary.OWL2_DATATYPES.forEach(iri -> types.getSafeTypeByName(iri.getURI()));
            Set<RDFDatatype> res = new HashSet<>();
            types.listTypes().forEachRemaining(res::add);
            return Collections.unmodifiableSet(res);
        }

        @Override
        public Set<Property> annotationProperties() {
            return ANNOTATION_PROPERTIES;
        }

        @Override
        public Set<Property> datatypeProperties() {
            return DATA_PROPERTIES;
        }

        @Override
        public Set<Property> objectProperties() {
            return OBJECT_PROPERTIES;
        }

        @Override
        public Set<Resource> datatypes() {
            return DATATYPES;
        }

        @Override
        public Set<Resource> classes() {
            return CLASSES;
        }

        @Override
        public Set<Resource> reservedResources() {
            return ALL_RESOURCES;
        }

        @Override
        public Set<Property> reservedProperties() {
            return ALL_PROPERTIES;
        }
    }

    /**
     * Access to {@link DC} vocabulary.
     */
    public static class DCVocabulary extends Empty {

        @Override
        public Set<Property> annotationProperties() {
            return reservedProperties();
        }

        @Override
        public Set<Property> reservedProperties() {
            return getConstants(Property.class, DC.class);
        }
    }

    /**
     * Access to {@link SKOS} vocabulary.
     */
    @SuppressWarnings("WeakerAccess")
    public static class SKOSVocabulary implements Vocabulary {
        public static final Set<Property> ANNOTATION_PROPERTIES =
                Stream.of(SKOS.altLabel, SKOS.changeNote, SKOS.definition,
                        SKOS.editorialNote, SKOS.example, SKOS.hiddenLabel, SKOS.historyNote,
                        SKOS.note, SKOS.prefLabel, SKOS.scopeNote)
                        .collect(Iter.toUnmodifiableSet());
        public static final Set<Property> OBJECT_PROPERTIES =
                Stream.of(SKOS.broadMatch, SKOS.broader, SKOS.broaderTransitive,
                        SKOS.closeMatch, SKOS.exactMatch, SKOS.hasTopConcept, SKOS.inScheme,
                        SKOS.mappingRelation, SKOS.member, SKOS.memberList, SKOS.narrowMatch,
                        SKOS.narrower, SKOS.narrowerTransitive, SKOS.related,
                        SKOS.relatedMatch, SKOS.semanticRelation, SKOS.topConceptOf)
                        .collect(Iter.toUnmodifiableSet());
        /**
         * NOTE: In the {@link org.semanticweb.owlapi.vocab.SKOSVocabulary} there is also skos:TopConcept
         * But in fact there is no such resource in the <a href='https://www.w3.org/2009/08/skos-reference/skos.htm'>specification</a>.
         */
        public static final Set<Resource> CLASSES =
                Stream.of(SKOS.Collection, SKOS.Concept, SKOS.ConceptScheme, SKOS.OrderedCollection)
                        .collect(Iter.toUnmodifiableSet());

        @Override
        public Set<Property> annotationProperties() {
            return ANNOTATION_PROPERTIES;
        }

        @Override
        public Set<Property> datatypeProperties() {
            return Collections.emptySet();
        }

        @Override
        public Set<Property> objectProperties() {
            return OBJECT_PROPERTIES;
        }

        @Override
        public Set<Resource> datatypes() {
            return Collections.emptySet();
        }

        @Override
        public Set<Resource> classes() {
            return CLASSES;
        }

        @Override
        public Set<Resource> reservedResources() {
            return getConstants(Resource.class, SKOS.class);
        }

        @Override
        public Set<Property> reservedProperties() {
            return getConstants(Property.class, SKOS.class);
        }
    }

    /**
     * For SWRL modeling.
     *
     * @see SWRL
     * @see SWRLB
     */
    public static class SWRLVocabulary extends Empty implements Vocabulary {
        private static final Class[] VOCABULARIES = new Class[]{SWRL.class, SWRLB.class};
        public static final Set<Property> ALL_PROPERTIES = getConstants(Property.class, VOCABULARIES);
        public static final Set<Resource> ALL_RESOURCES = getConstants(Resource.class, VOCABULARIES);
        public static final Set<Resource> BUILTINS = getConstants(Property.class, SWRLB.class);

        @Override
        public Set<Resource> swrlBuiltins() {
            return BUILTINS;
        }

        @Override
        public Set<Resource> reservedResources() {
            return ALL_RESOURCES;
        }

        @Override
        public Set<Property> reservedProperties() {
            return ALL_PROPERTIES;
        }
    }

    /**
     * The union vocabulary which consists from several other vocabularies.
     */
    @SuppressWarnings("WeakerAccess")
    public static class MultiVocabulary extends BaseVocabulary implements Vocabulary {
        protected final Collection<Vocabulary> vocabularies;

        protected MultiVocabulary(Collection<Vocabulary> vocabularies) {
            if (Objects.requireNonNull(vocabularies).contains(null)) {
                throw new IllegalArgumentException();
            }
            this.vocabularies = vocabularies;
        }

        public static MultiVocabulary create(Vocabulary... vocabularies) {
            Set<Vocabulary> res = Arrays.stream(vocabularies).collect(Iter.toUnmodifiableSet());
            if (res.isEmpty()) throw new OntJenaException.IllegalArgument("Empty list specified");
            return new MultiVocabulary(res);
        }

        protected <R extends Resource> Set<R> merge(Function<Vocabulary, Set<R>> map) {
            return vocabularies.stream().map(map).flatMap(Collection::stream).collect(Iter.toUnmodifiableSet());
        }

        public boolean contains(Vocabulary voc) {
            return vocabularies.contains(voc);
        }

        @Override
        public Set<Property> annotationProperties() {
            return annotationProperties == null ?
                    annotationProperties = merge(Vocabulary::annotationProperties) : annotationProperties;
        }

        @Override
        public Set<Property> datatypeProperties() {
            return datatypeProperties == null ?
                    datatypeProperties = merge(Vocabulary::datatypeProperties) : datatypeProperties;
        }

        @Override
        public Set<Property> objectProperties() {
            return objectProperties == null ?
                    objectProperties = merge(Vocabulary::objectProperties) : objectProperties;
        }

        @Override
        public Set<Resource> datatypes() {
            return datatypes == null ? datatypes = merge(Vocabulary::datatypes) : datatypes;
        }

        @Override
        public Set<Resource> classes() {
            return classes == null ? classes = merge(Vocabulary::classes) : classes;
        }

        @Override
        public Set<Resource> swrlBuiltins() {
            return swrlBuiltins == null ? swrlBuiltins = merge(Vocabulary::swrlBuiltins) : swrlBuiltins;
        }

        @Override
        public Set<Resource> reservedResources() {
            return reservedResources == null ?
                    reservedResources = merge(Vocabulary::reservedResources) : reservedResources;
        }

        @Override
        public Set<Property> reservedProperties() {
            return reservedProperties == null ?
                    reservedProperties = merge(Vocabulary::reservedProperties) : reservedProperties;
        }

        @Override
        public Set<Property> properties() {
            return properties == null ?
                    properties = Stream.of(annotationProperties(), datatypeProperties(), objectProperties())
                            .flatMap(Collection::stream).collect(Iter.toUnmodifiableSet()) : properties;
        }

        @Override
        public Set<Resource> entities() {
            return entities == null ?
                    entities = Stream.of(classes(), datatypes(), properties())
                            .flatMap(Collection::stream).collect(Iter.toUnmodifiableSet()) : entities;
        }

        @Override
        public Set<Resource> reserved() {
            return reserved == null ?
                    reserved = Stream.of(reservedProperties(), reservedResources())
                            .flatMap(Collection::stream).collect(Iter.toUnmodifiableSet()) : reserved;
        }
    }

    static abstract class BaseVocabulary {
        protected Set<Property> annotationProperties;
        protected Set<Property> datatypeProperties;
        protected Set<Property> objectProperties;
        protected Set<Resource> datatypes;
        protected Set<Resource> classes;

        protected Set<Resource> swrlBuiltins;

        protected Set<Property> properties;
        protected Set<Resource> entities;

        protected Set<Resource> reserved;
        protected Set<Resource> reservedResources;
        protected Set<Property> reservedProperties;
    }

}
