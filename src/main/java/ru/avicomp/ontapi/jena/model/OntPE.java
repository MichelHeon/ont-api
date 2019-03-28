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

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;

import java.util.stream.Stream;

/**
 * An abstraction for any Ontology <b>P</b>roperty <b>E</b>xpression.
 * In OWL2 there are four such property expressions: Data Property, Object Property (Entity and InverseOf) and Annotation Property.
 * <p>
 * Created by @szuev on 02.11.2016.
 *
 * @see <a href='https://www.w3.org/TR/owl2-quick-reference/'>2.2 Properties</a>
 * @see OntOPE
 * @see OntNAP
 * @see OntNDP
 */
public interface OntPE extends OntObject {

    /**
     * Lists all property domains.
     *
     * @return Stream of {@link Resource}s
     * @see OntNAP#domain()
     * @see OntOPE#domain()
     * @see OntNDP#domain()
     */
    Stream<? extends Resource> domain();

    /**
     * Lists all property ranges.
     *
     * @return Stream of {@link Resource}s
     * @see OntNAP#range()
     * @see OntOPE#range()
     * @see OntNDP#range()
     */
    Stream<? extends Resource> range();

    /**
     * List all super properties for this property expression.
     *
     * @return Stream of {@link Resource jena resource}s
     * @see OntNAP#subPropertyOf()
     * @see OntOPE#subPropertyOf()
     * @see OntNDP#subPropertyOf()
     */
    Stream<? extends OntPE> subPropertyOf();

    /**
     * Returns a named part of this property expression.
     *
     * @return {@link Property}
     */
    Property asProperty();

    /**
     * Removes the specified domain (predicate {@link RDFS#domain rdfs:domain}).
     *
     * @param domain {@link Resource}
     */
    default void removeDomain(Resource domain) {
        remove(RDFS.domain, domain);
    }

    /**
     * Removes specified {@code rdfs:range}.
     *
     * @param range {@link Resource}
     */
    default void removeRange(Resource range) {
        remove(RDFS.range, range);
    }

    /**
     * Removes specified super property (predicate {@link RDFS#subPropertyOf rdfs:subPropertyOf}).
     *
     * @param superProperty {@link Resource}
     */
    default void removeSubPropertyOf(Resource superProperty) {
        remove(RDFS.subPropertyOf, superProperty);
    }
}
