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

package ru.avicomp.ontapi.internal.axioms;

import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDFS;
import org.semanticweb.owlapi.model.*;
import ru.avicomp.ontapi.internal.*;
import ru.avicomp.ontapi.internal.objects.ONTAxiomImpl;
import ru.avicomp.ontapi.jena.model.OntCE;
import ru.avicomp.ontapi.jena.model.OntGraphModel;
import ru.avicomp.ontapi.jena.model.OntStatement;
import ru.avicomp.ontapi.jena.utils.OntModels;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A translator that provides {@link OWLSubClassOfAxiom} implementations.
 * Examples:
 * <pre>{@code
 * pizza:JalapenoPepperTopping
 *         rdfs:subClassOf   pizza:PepperTopping ;
 *         rdfs:subClassOf   [ a                   owl:Restriction ;
 *                             owl:onProperty      pizza:hasSpiciness ;
 *                             owl:someValuesFrom  pizza:Hot
 *                           ] .
 * }</pre>
 * <p>
 * Created by @szuev on 28.09.2016.
 * @see <a href='https://www.w3.org/TR/owl-syntax/#Subclass_Axioms'>9.1.1 Subclass Axioms</a>
 */
@SuppressWarnings("WeakerAccess")
public class SubClassOfTranslator extends AxiomTranslator<OWLSubClassOfAxiom> {

    @Override
    public void write(OWLSubClassOfAxiom axiom, OntGraphModel model) {
        WriteHelper.writeTriple(model, axiom.getSubClass(), RDFS.subClassOf, axiom.getSuperClass(), axiom.annotations());
    }

    @Override
    public ExtendedIterator<OntStatement> listStatements(OntGraphModel model, InternalConfig config) {
        return OntModels.listLocalStatements(model, null, RDFS.subClassOf, null).filterKeep(this::filter);
    }

    @Override
    public boolean testStatement(OntStatement statement, InternalConfig config) {
        return statement.getPredicate().equals(RDFS.subClassOf) && filter(statement);
    }

    public boolean filter(Statement s) {
        return s.getSubject().canAs(OntCE.class) && s.getObject().canAs(OntCE.class);
    }

    @Override
    public ONTObject<OWLSubClassOfAxiom> toAxiom(OntStatement statement, Supplier<OntGraphModel> m,
                                                 InternalObjectFactory factory,
                                                 InternalConfig config) {
        return AxiomImpl.create(statement, m, factory, config);
    }

    @Override
    public ONTObject<OWLSubClassOfAxiom> toAxiom(OntStatement statement,
                                                 InternalObjectFactory factory,
                                                 InternalConfig config) {
        ONTObject<? extends OWLClassExpression> sub = factory.getClass(statement.getSubject(OntCE.class));
        ONTObject<? extends OWLClassExpression> sup = factory.getClass(statement.getObject().as(OntCE.class));
        Collection<ONTObject<OWLAnnotation>> annotations = factory.getAnnotations(statement, config);
        OWLSubClassOfAxiom res = factory.getOWLDataFactory()
                .getOWLSubClassOfAxiom(sub.getOWLObject(), sup.getOWLObject(), ONTObject.extract(annotations));
        return ONTObjectImpl.create(res, statement).append(annotations).append(sub).append(sup);
    }

    /**
     * @see ru.avicomp.ontapi.owlapi.axioms.OWLSubClassOfAxiomImpl
     */
    public static class AxiomImpl extends ONTAxiomImpl
            implements ONTObject<OWLSubClassOfAxiom>, OWLSubClassOfAxiom, HasConfig {

        protected AxiomImpl(Object subject, String predicate, Object object, Supplier<OntGraphModel> m) {
            super(subject, predicate, object, m);
        }

        /**
         * Wraps the given {@link OntStatement} as {@link OWLSubClassOfAxiom} and {@link ONTObject}.
         *
         * @param s  {@link OntStatement}, not {@code null}
         * @param m  {@link OntGraphModel} provider, not {@code null}
         * @param of {@link InternalObjectFactory}, not {@code null}
         * @param c  {@link InternalConfig}, not {@code null}
         * @return {@link AxiomImpl}
         */
        public static AxiomImpl create(OntStatement s,
                                       Supplier<OntGraphModel> m,
                                       InternalObjectFactory of,
                                       InternalConfig c) {
            AxiomImpl res = new AxiomImpl(fromNode(s.getSubject()),
                    s.getPredicate().getURI(), fromNode(s.getObject()), m);
            res.content.put(res, res.collectContent(s, c, of));
            return res;
        }

        @SuppressWarnings("unchecked")
        @Override
        public OWLSubClassOfAxiom getAxiomWithoutAnnotations() {
            return createAnnotatedAxiom(Collections.emptySet());
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends OWLAxiom> T getAnnotatedAxiom(@Nonnull Stream<OWLAnnotation> annotations) {
            return (T) createAnnotatedAxiom(appendAnnotations(annotations));
        }

        private OWLSubClassOfAxiom createAnnotatedAxiom(Collection<OWLAnnotation> annotations) {
            return getDataFactory().getOWLSubClassOfAxiom(getSubClass(), getSuperClass(), annotations);
        }

        @Override
        public OWLClassExpression getSubClass() {
            return getONTSubClass().getOWLObject();
        }

        @Override
        public OWLClassExpression getSuperClass() {
            return getONTSuperClass().getOWLObject();
        }

        @SuppressWarnings("unchecked")
        public ONTObject<? extends OWLClassExpression> getONTSubClass() {
            return (ONTObject<? extends OWLClassExpression>) getContent()[0];
        }

        @SuppressWarnings("unchecked")
        public ONTObject<? extends OWLClassExpression> getONTSuperClass() {
            return (ONTObject<? extends OWLClassExpression>) getContent()[1];
        }

        @Override
        public boolean isGCI() {
            return getSubClass().isAnonymous();
        }

        @Override
        public boolean canContainAnnotationProperties() {
            return isAnnotated();
        }

        @Override
        public boolean isAnnotated() {
            return getContent().length != 2;
        }

        @Override
        public OWLSubClassOfAxiom getOWLObject() {
            return this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Stream<ONTObject<? extends OWLObject>> objects() {
            List res = Arrays.asList(getContent());
            return (Stream<ONTObject<? extends OWLObject>>) res.stream();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Object[] collectContent(OntStatement s, InternalConfig c, InternalObjectFactory f) {
            Set<ONTObject<OWLAnnotation>> annotations = collectAnnotations(s, c, f);
            List res = new ArrayList(annotations.isEmpty() ? 2 : (2 + annotations.size()));
            res.add(f.getClass(s.getSubject(OntCE.class)));
            res.add(f.getClass(s.getObject(OntCE.class)));
            if (!annotations.isEmpty()) {
                res.addAll(annotations);
            }
            return res.toArray();
        }

        @SuppressWarnings("unchecked")
        public Stream<OWLAnnotation> annotations() {
            List it = Arrays.asList(getContent());
            return (Stream<OWLAnnotation>) it.stream().skip(2);
        }

        @Override
        public List<OWLAnnotation> annotationsAsList() {
            return annotations().collect(Collectors.toList());
        }
    }

}
