/*
 * This file is part of the ONT API.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright (c) 2020, The University of Manchester, owl.cs group.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.github.owlcs.ontapi.jena.model;

/**
 * Provides a "sugar" functionality to represent an expression as an entity.
 * Just in convenience sake.
 * Created by @ssz on 09.03.2020.
 *
 * @param <E> - an {@link OntEntity} instance
 */
interface AsNamed<E extends OntEntity> {

    /**
     * Represents this OWL expression as a named OWL entity if it is possible, otherwise throws an exception.
     * Effectively equivalent to the expression {@code this.as(Named.class)}.
     *
     * @return {@link E}, never {@code null}
     * @throws org.apache.jena.enhanced.UnsupportedPolymorphismException if the expression is not named OWL entity
     * @see org.apache.jena.rdf.model.RDFNode#as(Class)
     */
    E asNamed();
}
