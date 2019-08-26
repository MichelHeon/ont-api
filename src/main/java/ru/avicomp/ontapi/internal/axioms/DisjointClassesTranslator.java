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

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import ru.avicomp.ontapi.internal.InternalConfig;
import ru.avicomp.ontapi.internal.InternalObjectFactory;
import ru.avicomp.ontapi.internal.ONTObject;
import ru.avicomp.ontapi.jena.model.OntCE;
import ru.avicomp.ontapi.jena.model.OntDisjoint;
import ru.avicomp.ontapi.jena.model.OntStatement;
import ru.avicomp.ontapi.jena.vocabulary.OWL;

/**
 * see {@link AbstractTwoWayNaryTranslator}
 * example:
 * <ul>
 * <li>{@code :Complex2 owl:disjointWith  :Simple2 , :Simple1 . }</li>
 * <li>OWL2 alternative way:
 * {@code [ a owl:AllDisjointClasses ; owl:members ( :Complex2 :Simple1 :Simple2 ) ] . }</li>
 * </ul>
 * <p>
 * Created by @szuev on 28.09.2016.
 *
 * @see OWLDisjointClassesAxiom
 */
public class DisjointClassesTranslator extends AbstractTwoWayNaryTranslator<OWLDisjointClassesAxiom, OWLClassExpression, OntCE> {
    @Override
    Property getPredicate() {
        return OWL.disjointWith;
    }

    @Override
    Class<OntCE> getView() {
        return OntCE.class;
    }

    @Override
    Resource getMembersType() {
        return OWL.AllDisjointClasses;
    }

    @Override
    Property getMembersPredicate() {
        return OWL.members;
    }

    @Override
    Class<OntDisjoint.Classes> getDisjointView() {
        return OntDisjoint.Classes.class;
    }

    @Override
    public ONTObject<OWLDisjointClassesAxiom> toAxiom(OntStatement statement, InternalObjectFactory reader, InternalConfig config) {
        return makeAxiom(statement, reader.getAnnotations(statement, config),
                reader::getClass,
                (members, annotations) -> reader.getOWLDataFactory()
                        .getOWLDisjointClassesAxiom(ONTObject.extractWildcards(members), ONTObject.extract(annotations)));
    }
}
