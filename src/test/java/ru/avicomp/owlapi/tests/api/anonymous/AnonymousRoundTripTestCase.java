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
package ru.avicomp.owlapi.tests.api.anonymous;

import org.junit.Test;
import org.semanticweb.owlapi.formats.ManchesterSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.*;
import ru.avicomp.owlapi.tests.api.baseclasses.TestBase;

import static org.junit.Assert.assertNull;
import static ru.avicomp.owlapi.OWLFunctionalSyntaxFactory.Class;
import static ru.avicomp.owlapi.OWLFunctionalSyntaxFactory.*;

@SuppressWarnings("javadoc")
public class AnonymousRoundTripTestCase extends TestBase {

    @Test
    public void shouldNotFailOnAnonymousOntologySearch() throws OWLOntologyCreationException {
        m.createOntology(new OWLOntologyID());
        assertNull(m.getOntology(new OWLOntologyID()));
    }

    @Test
    public void testRoundTrip() throws Exception {
        String ns = "http://smi-protege.stanford.edu/ontologies/AnonymousIndividuals.owl";
        OWLClass a = Class(IRI(ns + "#", "A"));
        OWLAnonymousIndividual h = AnonymousIndividual();
        OWLAnonymousIndividual i = AnonymousIndividual();
        OWLAnnotationProperty p = AnnotationProperty(IRI(ns + "#", "p"));
        OWLObjectProperty q = ObjectProperty(IRI(ns + "#", "q"));
        OWLOntology ontology = getOWLOntology();
        OWLAnnotation annotation1 = df.getOWLAnnotation(p, h);
        OWLAnnotation annotation2 = df.getRDFSLabel(Literal("Second", "en"));
        ontology.add(df.getOWLAnnotationAssertionAxiom(a.getIRI(), annotation1), ClassAssertion(a, h),
                ObjectPropertyAssertion(q, h, i), df.getOWLAnnotationAssertionAxiom(h, annotation2));
        OWLOntology o = roundTrip(ontology, new ManchesterSyntaxDocumentFormat());
        equal(ontology, o);
    }
}
