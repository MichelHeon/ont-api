/*
 * This file is part of the ONT API.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright (c) 2019, Avicomp Services, AO
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package ru.avicomp.ontapi.jena.model;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.vocabulary.RDFS;
import ru.avicomp.ontapi.jena.OntJenaException;
import ru.avicomp.ontapi.jena.vocabulary.OWL;
import ru.avicomp.ontapi.jena.vocabulary.RDF;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A common interface for any <b>C</b>lass <b>E</b>xpressions (both named and anonymous).
 * Examples of rdf-patterns see <a href='https://www.w3.org/TR/owl2-quick-reference/'>here</a>.
 * <p>
 * Created by szuev on 01.11.2016.
 *
 * @see OntClass
 * @see <a href='https://www.w3.org/TR/owl2-quick-reference/'>2.1 Class Expressions</a>
 * @see <a href='https://www.w3.org/TR/owl2-syntax/#Class_Expressions'>8 Class Expressions</a>
 */
public interface OntCE extends OntObject {

    /**
     * Creates an anonymous individual which is of this class-expression type.
     *
     * @return {@link OntIndividual.Anonymous}
     * @see OntIndividual#attachClass(OntCE)
     * @see #individuals()
     */
    OntIndividual.Anonymous createIndividual();

    /**
     * Creates a named individual which is of this class type.
     *
     * @param uri, String, not {@code null}
     * @return {@link OntIndividual.Named}
     * @see OntIndividual#attachClass(OntCE)
     * @see #individuals()
     */
    OntIndividual.Named createIndividual(String uri);

    /**
     * Answers a {@code Stream} over the class-expressions
     * for which this class expression is declared to be sub-class.
     * The return {@code Stream} is distinct and this instance is not included into it.
     * <p>
     * The flag {@code direct} allows some selectivity over the classes that appear in the {@code Stream}.
     * If it is {@code true} only direct sub-classes are returned,
     * and the method is equivalent to the method {@link #subClassOf()}
     * with except of some boundary cases (e.g. {@code <A> rdfs:subClassOf <A>}).
     * If it is {@code false}, the method returns all super classes recursively.
     * Consider the following scenario:
     * <pre>{@code
     *   :A rdfs:subClassOf :B .
     *   :B rdfs:subClassOf :C .
     * }</pre>
     * If the flag {@code direct} is {@code true},
     * the listing super classes for the class {@code A} will return only {@code B}.
     * And otherwise, if the flag {@code direct} is {@code false}, it will return {@code B} and also {@code C}.
     *
     * @param direct boolean: if {@code true}, only answers the directly adjacent classes in the super-class relation,
     *               otherwise answers all super-classes found in the {@code Graph} recursively
     * @return <b>distinct</b> {@code Stream} of super {@link OntCE class expression}s
     * @see #subClassOf()
     * @see #listSubClasses(boolean)
     * @since 1.4.0
     */
    Stream<OntCE> listSuperClasses(boolean direct);

    /**
     * Answer a {@code Stream} over all of the class expressions
     * that are declared to be sub-classes of this class expression.
     * The return {@code Stream} is distinct and this instance is not included into it.
     * The flag {@code direct} allows some selectivity over the classes that appear in the {@code Stream}.
     * Consider the following scenario:
     * <pre>{@code
     *   :B rdfs:subClassOf :A .
     *   :C rdfs:subClassOf :B .
     * }</pre>
     * If the flag {@code direct} is {@code true},
     * the listing sub classes for the class {@code A} will return only {@code B}.
     * And otherwise, if the flag {@code direct} is {@code false}, it will return {@code B} and also {@code C}.
     *
     * @param direct boolean: if {@code true}, only answers the directly adjacent classes in the sub-class relation,
     *               otherwise answers all sub-classes found in the {@code Graph} recursively
     * @return <b>distinct</b> {@code Stream} of sub {@link OntCE class expression}s
     * @see #listSuperClasses(boolean)
     * @since 1.4.0
     */
    Stream<OntCE> listSubClasses(boolean direct);

    /**
     * Lists all individuals,
     * i.e. subjects from class-assertion statements {@code a rdf:type C}.
     *
     * @return Stream of {@link OntIndividual}s
     */
    default Stream<OntIndividual> individuals() {
        return getModel().statements(null, RDF.type, this)
                .map(OntStatement::getSubject).map(s -> s.as(OntIndividual.class));
    }

    /**
     * Lists all properties attached to the class in a {@code rdfs:domain} statement.
     * The property is considered as attached if
     * it and the class expression are both included in property domain axiom description:
     * <ul>
     * <li>{@code R rdfs:domain C} - {@code R} is a data property {@code C} - this class expression</li>
     * <li>{@code P rdfs:domain C} - {@code P} is an object property expression, {@code C} - this class expression</li>
     * <li>{@code A rdfs:domain U} - {@code A} is annotation property, {@code U} is IRI, this class expression</li>
     * </ul>
     *
     * @return Stream of {@link OntPE}s
     * @see OntPE#domain()
     */
    default Stream<OntPE> properties() {
        return getModel().statements(null, RDFS.domain, this)
                .map(OntStatement::getSubject)
                .filter(s -> s.canAs(OntPE.class))
                .map(s -> s.as(OntPE.class));
    }

    /**
     * Lists all super classes for this class expression.
     * The search pattern is {@code C rdfs:subClassOf Ci},
     * where {@code C} is this instance, and {@code Ci} is one of the returned.
     *
     * @return Stream of {@link OntCE}s
     * @see #listSuperClasses(boolean)
     */
    default Stream<OntCE> subClassOf() {
        return objects(RDFS.subClassOf, OntCE.class);
    }

    /**
     * Adds a super class.
     *
     * @param superClass {@link OntCE}
     * @return {@link OntStatement}
     */
    default OntStatement addSubClassOf(OntCE superClass) {
        return addStatement(RDFS.subClassOf, superClass);
    }

    /**
     * Removes a super class.
     *
     * @param superClass {@link OntCE}
     */
    default void removeSubClassOf(OntCE superClass) {
        remove(RDFS.subClassOf, superClass);
    }

    /**
     * Returns all disjoint classes.
     * The statement patter to search for is {@code C1 owl:disjointWith C2}.
     *
     * @return Stream of {@link OntCE}s
     * @see OntDisjoint.Classes
     */
    default Stream<OntCE> disjointWith() {
        return objects(OWL.disjointWith, OntCE.class);
    }

    /**
     * Adds a disjoint class.
     *
     * @param other {@link OntCE}
     * @return {@link OntStatement}
     * @see OntDisjoint.Classes
     */
    default OntStatement addDisjointWith(OntCE other) {
        return addStatement(OWL.disjointWith, other);
    }

    /**
     * Removes a disjoint class.
     *
     * @param other {@link OntCE}
     * @see OntDisjoint.Classes
     */
    default void removeDisjointWith(OntCE other) {
        remove(OWL.disjointWith, other);
    }

    /**
     * Lists all equivalent classes.
     *
     * @return Stream of {@link OntCE}s
     * @see OntDT#equivalentClass()
     */
    default Stream<OntCE> equivalentClass() {
        return objects(OWL.equivalentClass, OntCE.class);
    }

    /**
     * Adds new equivalent class.
     *
     * @param other {@link OntCE}
     * @return {@link OntStatement}
     * @see OntDT#addEquivalentClass(OntDR)
     */
    default OntStatement addEquivalentClass(OntCE other) {
        return addStatement(OWL.equivalentClass, other);
    }

    /**
     * Removes an equivalent class.
     *
     * @param other {@link OntCE}
     * @see OntDT#removeEquivalentClass(OntDR)
     */
    default void removeEquivalentClass(OntCE other) {
        remove(OWL.equivalentClass, other);
    }

    /**
     * Creates a HasKey logical construction as {@link OntList ontology list} of {@link OntDOP Object or Data Property Expression}s
     * that is attached to this Class Expression using the predicate {@link OWL#hasKey owl:hasKey}.
     * The resulting rdf-list will consist of all the elements of the specified collection in the same order but with exclusion of duplicates.
     * Note: {@code null}s in collection will cause {@link NullPointerException NullPointerException}.
     * For additional information about HasKey logical construction see
     * <a href='https://www.w3.org/TR/owl2-syntax/#Keys'>9.5 Keys</a> specification.
     *
     * @param objectProperties {@link Collection} (preferably {@link Set})of {@link OntOPE object property expression}s
     * @param dataProperties   {@link Collection} (preferably {@link Set})of {@link OntNDP data property expression}s
     * @return {@link OntList} of {@link OntDOP}s
     * @since 1.3.0
     */
    OntList<OntDOP> createHasKey(Collection<OntOPE> objectProperties, Collection<OntNDP> dataProperties);

    /**
     * Creates a HasKey logical construction as {@link OntList ontology list} and returns statement {@code C owl:hasKey (P1 ... Pm R1 ... Rn)}
     * to allow the addition of annotations.
     * About RDF Graph annotation specification see, for example,
     * <a href='https://www.w3.org/TR/owl2-mapping-to-rdf/#Translation_of_Annotations'>2.3.1 Axioms that Generate a Main Triple</a>.
     *
     * @param properties Array of {@link OntDOP}s without {@code null}s
     * @return {@link OntStatement}
     * @since 1.3.0
     */
    OntStatement addHasKey(OntDOP... properties);

    /**
     * Finds a HasKey logical construction attached to this class expression by the specified rdf-node in the form of {@link OntList}.
     *
     * @param list {@link RDFNode}
     * @return Optional around {@link OntList} of {@link OntDOP data and object property expression}s
     * @since 1.3.0
     */
    Optional<OntList<OntDOP>> findHasKey(RDFNode list);

    /**
     * Lists all HasKey {@link OntList ontology list}s that are attached to this class expression
     * on predicate {@link OWL#hasKey owl:hasKey}.
     *
     * @return Stream of {@link OntList}s with parameter-type {@code OntDOP}
     * @since 1.3.0
     */
    Stream<OntList<OntDOP>> listHasKeys();

    /**
     * Deletes the given HasKey list including its annotations
     * with predicate {@link OWL#hasKey owl:hasKey} for this resource from its associated model.
     *
     * @param list {@link RDFNode} can be {@link OntList} or {@link RDFList}
     * @throws OntJenaException if the list is not found
     * @since 1.3.0
     */
    void removeHasKey(RDFNode list);

    /**
     * Lists all key properties.
     * I.e. returns all object- and datatype- properties which belong to
     * the {@code C owl:hasKey ( P1 ... Pm R1 ... Rn )} statements,
     * where {@code C} is this class expression,
     * {@code Pi} is a property expression, and {@code Ri} is a data(-type) property.
     * If there are several []-lists in the model that satisfy these conditions,
     * all their content will be merged into the one distinct stream.
     *
     * @return <b>distinct</b> Stream of {@link OntOPE object} and {@link OntNDP data} properties
     * @see #listHasKeys()
     */
    default Stream<OntDOP> hasKey() {
        return listHasKeys().flatMap(OntList::members).distinct();
    }

    /**
     * Creates an {@code owl:hasKey} statement returning root statement to allow adding annotations.
     *
     * @param objectProperties the collection of {@link OntOPE}s
     * @param dataProperties   the collection of {@link OntNDP}s
     * @return {@link OntStatement}
     */
    default OntStatement addHasKey(Collection<OntOPE> objectProperties, Collection<OntNDP> dataProperties) {
        return createHasKey(objectProperties, dataProperties).getRoot();
    }

    /**
     * Deletes all HasKey list including its annotations
     * with predicate {@link OWL#hasKey owl:hasKey} for this resource from its associated model.
     *
     * @throws OntJenaException if the list is not found
     * @since 1.3.0
     */
    default void clearHasKeys() {
        listHasKeys().collect(Collectors.toSet()).forEach(this::removeHasKey);
    }

    /*
     * ============================
     * All known Class Expressions:
     * ============================
     */

    interface ObjectSomeValuesFrom extends ComponentRestrictionCE<OntCE, OntOPE> {
    }

    interface DataSomeValuesFrom extends ComponentRestrictionCE<OntDR, OntNDP> {
    }

    interface ObjectAllValuesFrom extends ComponentRestrictionCE<OntCE, OntOPE> {
    }

    interface DataAllValuesFrom extends ComponentRestrictionCE<OntDR, OntNDP> {
    }

    interface ObjectHasValue extends ComponentRestrictionCE<OntIndividual, OntOPE> {
    }

    interface DataHasValue extends ComponentRestrictionCE<Literal, OntNDP> {
    }

    interface ObjectMinCardinality extends CardinalityRestrictionCE<OntCE, OntOPE> {
    }

    interface DataMinCardinality extends CardinalityRestrictionCE<OntDR, OntNDP> {
    }

    interface ObjectMaxCardinality extends CardinalityRestrictionCE<OntCE, OntOPE> {
    }

    interface DataMaxCardinality extends CardinalityRestrictionCE<OntDR, OntNDP> {
    }

    interface ObjectCardinality extends CardinalityRestrictionCE<OntCE, OntOPE> {
    }

    interface DataCardinality extends CardinalityRestrictionCE<OntDR, OntNDP> {
    }

    interface HasSelf extends RestrictionCE, ONProperty<OntOPE> {
    }

    interface UnionOf extends ComponentsCE<OntCE> {
    }

    interface OneOf extends ComponentsCE<OntIndividual> {
    }

    interface IntersectionOf extends ComponentsCE<OntCE> {
    }

    interface ComplementOf extends OntCE, Value<OntCE> {
    }

    interface NaryDataAllValuesFrom extends NaryRestrictionCE<OntDR, OntNDP> {
    }

    interface NaryDataSomeValuesFrom extends NaryRestrictionCE<OntDR, OntNDP> {
    }

    /*
     * ===========================
     * Abstract class expressions:
     * ===========================
     */

    interface ComponentsCE<O extends OntObject> extends OntCE, Components<O> {
    }

    interface CardinalityRestrictionCE<O extends OntObject, P extends OntDOP> extends Cardinality, ComponentRestrictionCE<O, P> {
    }

    interface ComponentRestrictionCE<O extends RDFNode, P extends OntDOP> extends RestrictionCE, ONProperty<P>, Value<O> {
    }

    interface NaryRestrictionCE<O extends OntObject, P extends OntDOP> extends RestrictionCE, ONProperties<P>, Value<O> {
    }

    interface RestrictionCE extends OntCE {
    }

    /*
     * ============================
     * Common technical interfaces:
     * ============================
     */

    interface ONProperty<P extends OntDOP> {
        P getOnProperty();

        void setOnProperty(P p);
    }

    interface ONProperties<P extends OntPE> {
        /**
         * Gets the ONT-List that contains resources of type {@link P}.
         *
         * @return {@link OntList}
         * @since 1.3.0
         */
        OntList<P> getList();

        default Stream<P> onProperties() {
            return getList().members();
        }

        default void setOnProperties(Collection<P> properties) {
            getList().clear().addAll(properties);
        }
    }

    interface Components<O extends OntObject> {
        /**
         * Gets the ONT-List that contains resources of type {@link O}.
         *
         * @return {@link OntList}
         * @since 1.3.0
         */
        OntList<O> getList();

        default Stream<O> components() {
            return getList().members();
        }

        default void setComponents(Collection<O> components) {
            getList().clear().addAll(components);
        }
    }

    interface Value<O extends RDFNode> {
        O getValue();

        void setValue(O value);
    }

    interface Cardinality {
        int getCardinality();

        void setCardinality(int cardinality);

        /**
         * Determines if this restriction is qualified.
         * Qualified cardinality restrictions are defined to be cardinality restrictions
         * that have fillers which aren't TOP (owl:Thing or rdfs:Literal).
         * An object restriction is unqualified if it has a filler that is owl:Thing.
         * A data restriction is unqualified if it has a filler which is the top data type (rdfs:Literal).
         *
         * @return {@code true} if this restriction is qualified, or {@code false} if this restriction is unqualified.
         */
        boolean isQualified();
    }
}

