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
package ru.avicomp.owlapi.tests.api.fileroundtrip;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.*;
import ru.avicomp.owlapi.tests.api.baseclasses.TestBase;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static ru.avicomp.owlapi.OWLFunctionalSyntaxFactory.Class;
import static ru.avicomp.owlapi.OWLFunctionalSyntaxFactory.*;

/**
 * @author Matthew Horridge, The University of Manchester, Information
 *         Management Group
 * @since 3.0.0
 */
@RunWith(Parameterized.class)
@SuppressWarnings("javadoc")
public class NonSymmetricAxiomsRoundTrippingTestCase extends TestBase {

    private static final IRI iriA = iri("A");
    private static final OWLClass clsA = Class(iriA);
    private static final OWLClass clsB = Class(iri("B"));
    private static final OWLClass clsC = Class(iri("C"));
    private static final OWLDatatype dataD = Datatype(iri("D"));
    private static final OWLDatatype dataE = Datatype(iri("E"));
    private static final OWLObjectProperty propA = ObjectProperty(iri("propA"));
    private static final OWLDataProperty propB = DataProperty(iri("propB"));
    private static final OWLObjectSomeValuesFrom d = ObjectSomeValuesFrom(propA, ObjectIntersectionOf(clsB, clsC));
    private static final OWLDataSomeValuesFrom e = DataSomeValuesFrom(propB, DataIntersectionOf(dataD, dataE));
    private static final OWLClassExpression du = ObjectUnionOf(clsB, clsC);
    private static final OWLDataUnionOf eu = DataUnionOf(dataD, dataE);
    private OWLAxiom in;
    private OWLAxiom out;

    public NonSymmetricAxiomsRoundTrippingTestCase(OWLAxiom in, OWLAxiom out) {
        this.in = in;
        this.out = out;
    }

    @Parameters
    public static List<OWLAxiom[]> getData() {
        List<OWLAxiom[]> list = new ArrayList<>();
        list.add(new OWLAxiom[]{SubClassOf(clsA, ObjectIntersectionOf(d, d)), SubClassOf(clsA, d)});
        list.add(new OWLAxiom[]{SubClassOf(clsA, ObjectUnionOf(e, e)), SubClassOf(clsA, e)});
        list.add(new OWLAxiom[]{SubClassOf(clsA, ObjectIntersectionOf(du, du)), SubClassOf(clsA, du)});
        list.add(new OWLAxiom[]{DatatypeDefinition(dataD, DataUnionOf(eu, eu)), DatatypeDefinition(dataD, eu)});
        return list;
    }

    @Test
    public void shouldRoundTripAReadableVersion() throws OWLOntologyCreationException, OWLOntologyStorageException {
        OWLOntology output = getOWLOntology();
        output.add(in);
        OWLOntology o = roundTrip(output, new FunctionalSyntaxDocumentFormat());
        assertEquals(1, o.logicalAxioms().count());
        assertEquals(out, o.logicalAxioms().iterator().next());
    }
}
