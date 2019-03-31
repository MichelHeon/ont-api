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

package ru.avicomp.ontapi.jena.impl;

import org.apache.jena.enhanced.EnhGraph;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import ru.avicomp.ontapi.jena.OntJenaException;
import ru.avicomp.ontapi.jena.model.*;
import ru.avicomp.ontapi.jena.utils.Iter;
import ru.avicomp.ontapi.jena.vocabulary.OWL;

import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * owl:Class Implementation
 * <p>
 * Created by szuev on 03.11.2016.
 */
@SuppressWarnings("WeakerAccess")
public class OntClassImpl extends OntObjectImpl implements OntClass {

    public OntClassImpl(Node n, EnhGraph eg) {
        super(OntObjectImpl.checkNamed(n), eg);
    }

    @Override
    public Optional<OntStatement> findRootStatement() {
        return getOptionalRootStatement(this, OWL.Class);
    }

    @Override
    public boolean isBuiltIn() {
        return getModel().isBuiltIn(this);
    }

    @Override
    public Class<OntClass> getActualClass() {
        return OntClass.class;
    }

    @Override
    public OntIndividual.Anonymous createIndividual() {
        return OntCEImpl.createAnonymousIndividual(getModel(), this);
    }

    @Override
    public OntIndividual.Named createIndividual(String uri) {
        return OntCEImpl.createNamedIndividual(getModel(), this, uri);
    }

    @Override
    public Stream<OntCE> listSuperClasses(boolean direct) {
        return listHierarchy(this, OntCE.class, RDFS.subClassOf, false, direct);
    }

    @Override
    public Stream<OntCE> listSubClasses(boolean direct) {
        return listHierarchy(this, OntCE.class, RDFS.subClassOf, true, direct);
    }

    @Override
    public OntList<OntDOP> createHasKey(Collection<OntOPE> ope, Collection<OntNDP> dpe) {
        return OntCEImpl.createHasKey(getModel(), this, Stream.of(ope, dpe).flatMap(Collection::stream));
    }

    @Override
    public OntStatement addHasKey(OntDOP... properties) {
        return OntCEImpl.createHasKey(getModel(), this, Arrays.stream(properties)).getRoot();
    }

    @Override
    public Stream<OntList<OntDOP>> listHasKeys() {
        return OntCEImpl.listHasKeys(getModel(), this);
    }

    @Override
    public void removeHasKey(RDFNode list) throws OntJenaException.IllegalArgument {
        OntCEImpl.removeHasKey(getModel(), this, list);
    }

    @Override
    public OntList<OntCE> createDisjointUnion(Collection<OntCE> classes) {
        return OntListImpl.create(getModel(), this, OWL.disjointUnionOf, OntCE.class,
                Objects.requireNonNull(classes).stream().distinct().iterator());
    }

    @Override
    public Stream<OntList<OntCE>> listDisjointUnions() {
        return OntListImpl.stream(getModel(), this, OWL.disjointUnionOf, OntCE.class);
    }

    @Override
    public void removeDisjointUnion(RDFNode rdfList) throws OntJenaException.IllegalArgument {
        getModel().deleteOntList(this, OWL.disjointUnionOf, findDisjointUnion(rdfList).orElse(null));
    }

    /**
     * Gets all <b>local</b> built-in OWL Classes from the base graph of the specified model.
     * It seems, the faster way is to check all possible places.
     *
     * @param m {@link OntGraphModelImpl}, not {@code null}
     * @return unmodifiable {@code Set} of built-in {@link OntClass}es
     */
    public static Set<OntClass> getBuiltinClasses(OntGraphModelImpl m) {
        Set<OntClass> res = new HashSet<>();
        int max = m.getOntPersonality().getBuiltins().getClasses().size();
        BooleanSupplier exit = () -> res.size() >= max;
        Predicate<OntClass> test = c -> !exit.getAsBoolean() && c.isBuiltIn();

        // rdfs:range
        m.listLocalObjects(OntOPE.class, RDFS.range, OntClass.class)
                .filterKeep(test)
                .forEachRemaining(res::add);
        if (exit.getAsBoolean()) return Collections.unmodifiableSet(res);

        // rdfs:domain
        m.listLocalObjects(OntDOP.class, RDFS.domain, OntClass.class)
                .filterKeep(test)
                .forEachRemaining(res::add);
        if (exit.getAsBoolean()) return Collections.unmodifiableSet(res);

        // class assertions (rdf:type):
        m.listLocalObjects(OntIndividual.class, RDF.type, OntClass.class)
                .filterKeep(test)
                .forEachRemaining(res::add);
        if (exit.getAsBoolean()) return Collections.unmodifiableSet(res);

        // rdfs:subClassOf, owl:equivalentClass, owl:disjointWith
        filterBuiltin(m, exit, Iter.flatMap(Iter.of(RDFS.subClassOf, OWL.equivalentClass, OWL.disjointWith),
                p -> m.listLocalSubjectAndObjects(p, OntCE.class)))
                .forEachRemaining(res::add);
        if (exit.getAsBoolean()) return Collections.unmodifiableSet(res);

        // UnionOf and IntersectionOf class expressions (owl:unionOf,  owl:intersectionOf)
        filterBuiltin(m, exit, Iter.flatMap(Iter.of(OWL.unionOf, OWL.intersectionOf),
                p -> m.fromLocalList(OntCE.class, p, OntCE.class, false)))
                .forEachRemaining(res::add);
        if (exit.getAsBoolean()) return Collections.unmodifiableSet(res);

        //  ObjectComplementOf (owl:complementOf)
        m.listLocalObjects(OntCE.ComplementOf.class, OWL.complementOf, OntClass.class)
                .filterKeep(test)
                .forEachRemaining(res::add);
        if (exit.getAsBoolean()) return Collections.unmodifiableSet(res);

        // ObjectSomeValuesFrom, ObjectAllValuesFrom (owl:someValuesFrom, owl:allValuesFrom)
        Iter.flatMap(Iter.of(OWL.someValuesFrom, OWL.allValuesFrom), p -> listClassValuesFrom(m, p))
                .filterKeep(test)
                .forEachRemaining(res::add);
        if (exit.getAsBoolean()) return Collections.unmodifiableSet(res);

        // Qualified restrictions
        m.listLocalObjects(CardinalityRestrictionCE.class, OWL.onClass, OntClass.class)
                .filterKeep(test)
                .forEachRemaining(res::add);
        if (exit.getAsBoolean()) return Collections.unmodifiableSet(res);

        // Unqualified restrictions (owl:Thing)
        OntClass topCE = m.getOWLThing();
        if (topCE != null  // maybe null if it is absent in OntPersonality.Builtins
                && !res.contains(topCE)) {
            Iter.findFirst(Iter.flatMap(Iter.of(OWL.maxCardinality, OWL.cardinality, OWL.minCardinality),
                    p -> m.listLocalStatements(null, p, null)).mapWith(OntStatement::getSubject)
                    .filterKeep(OntClassImpl::isRestriction)).ifPresent(x -> res.add(topCE));
        }
        if (exit.getAsBoolean()) return Collections.unmodifiableSet(res);

        // owl:disjointUnionOf
        filterBuiltin(m, exit, m.fromLocalList(OntClass.class, OWL.disjointUnionOf, OntCE.class, true))
                .forEachRemaining(res::add);
        if (exit.getAsBoolean()) return Collections.unmodifiableSet(res);

        // owl:AllDisjointClasses:
        filterBuiltin(m, exit, Iter.flatMap(m.listLocalOntObjects(OntDisjoint.Classes.class)
                .mapWith(OntDisjoint::getList), list -> ((OntListImpl<? extends OntCE>) list).listMembers()))
                .forEachRemaining(res::add);
        if (exit.getAsBoolean()) return Collections.unmodifiableSet(res);

        // hasKey
        filterBuiltin(m, exit, m.listLocalStatements(null, OWL.hasKey, null)
                .filterKeep(x -> x.getObject().canAs(RDFList.class))
                .mapWith(OntStatement::getSubject))
                .forEachRemaining(res::add);
        return Collections.unmodifiableSet(res);
    }

    private static boolean isRestriction(OntObject s) {
        return s.canAs(OntCE.ObjectMinCardinality.class)
                || s.canAs(OntCE.ObjectMaxCardinality.class)
                || s.canAs(OntCE.ObjectCardinality.class);
    }

    private static ExtendedIterator<OntClass> listClassValuesFrom(OntGraphModelImpl m, Property predicate) {
        Class<? extends OntCE.ComponentRestrictionCE> type;
        if (OWL.someValuesFrom.equals(predicate)) {
            type = OntCE.ObjectSomeValuesFrom.class;
        } else if (OWL.allValuesFrom.equals(predicate)) {
            type = OntCE.ObjectAllValuesFrom.class;
        } else {
            throw new IllegalArgumentException();
        }
        return m.listLocalObjects(type, predicate, OntClass.class);
    }

    private static ExtendedIterator<OntClass> filterBuiltin(OntGraphModelImpl m,
                                                            BooleanSupplier exit,
                                                            ExtendedIterator<? extends RDFNode> from) {

        return filterBuiltin(OntClass.class, m, exit, from);
    }

}
