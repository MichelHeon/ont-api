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
package ru.avicomp.ontapi.owlapi.objects.swrl;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.SWRLArgument;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLDArgument;
import org.semanticweb.owlapi.vocab.SWRLBuiltInsVocabulary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health Informatics Group
 * @since 1.2.0
 */
public class SWRLBuiltInAtomImpl extends SWRLAtomImpl implements SWRLBuiltInAtom {

    private final List<SWRLDArgument> args;

    /**
     * @param predicate predicate
     * @param args      builtin argument
     */
    public SWRLBuiltInAtomImpl(IRI predicate, List<SWRLDArgument> args) {
        super(predicate);
        this.args = Collections
                .unmodifiableList(new ArrayList<>(Objects.requireNonNull(args, "args cannot be null")));
        // Do not sort these. They are arguments for built-ins and they are order sensitive.
    }

    @Override
    public IRI getPredicate() {
        return (IRI) super.getPredicate();
    }

    @Override
    public boolean isCoreBuiltIn() {
        return SWRLBuiltInsVocabulary.getBuiltIn(getPredicate()) != null;
    }

    @Override
    public Stream<SWRLDArgument> arguments() {
        return args.stream();
    }

    @Override
    public Stream<SWRLArgument> allArguments() {
        return args.stream().map(x -> x);
    }

    @Override
    public List<SWRLDArgument> getArguments() {
        return args;
    }
}
