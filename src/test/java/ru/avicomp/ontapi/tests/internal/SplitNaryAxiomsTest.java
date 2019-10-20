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

package ru.avicomp.ontapi.tests.internal;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLNaryAxiom;
import ru.avicomp.ontapi.OntManagers;
import ru.avicomp.ontapi.OntologyManager;
import ru.avicomp.ontapi.OntologyModel;
import ru.avicomp.ontapi.internal.ONTObject;
import ru.avicomp.ontapi.internal.objects.ModelObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by @ssz on 03.10.2019.
 */
public class SplitNaryAxiomsTest extends NaryAxiomsTestBase {
    public SplitNaryAxiomsTest(Data data) {
        super(data);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Data> getData() {
        return getObjects().stream()
                .filter(Data::isAxiom)
                // TODO: see https://github.com/avicomp/ont-api/issues/87
                .filter(x -> isOneOf(x
                        , AxiomType.EQUIVALENT_CLASSES
                        , AxiomType.INVERSE_OBJECT_PROPERTIES))
                .collect(Collectors.toList());
    }

    static Collection<? extends OWLAxiom> createONTAxioms(OntologyManager m, OWLAxiom toWrite) {
        OntologyModel o = m.createOntology();
        o.add(toWrite);
        o.clearCache();
        List<OWLAxiom> res = o.axioms(toWrite.getAxiomType())
                .peek(x -> Assert.assertTrue(x instanceof ONTObject))
                .sorted()
                .collect(Collectors.toList());
        Assert.assertFalse(res.isEmpty());
        if (res.size() == 1) {
            return new HashSet<>(res);
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testONTObject() {
        OWLNaryAxiom owl = (OWLNaryAxiom) data.create(OWL_DATA_FACTORY);
        LOGGER.debug("Test: '{}'", owl);
        OWLNaryAxiom ont = (OWLNaryAxiom) data.create(ONT_DATA_FACTORY);

        Assert.assertTrue(ont.getClass().getName().startsWith("ru.avicomp.ontapi.owlapi"));
        Assert.assertTrue(owl.getClass().getName().startsWith("uk.ac.manchester.cs.owl.owlapi"));

        Collection<? extends OWLAxiom> expectedPairwise = owl.asPairwiseAxioms();
        Collection<? extends OWLAxiom> testPairwise = ont.asPairwiseAxioms();
        Assert.assertEquals(expectedPairwise, testPairwise);

        Collection<? extends OWLAxiom> fromModel = createONTAxioms(OntManagers.createONT(), owl);
        Assert.assertEquals(expectedPairwise, fromModel);

        for (OWLAxiom expected : expectedPairwise) {
            OWLAxiom test = fromModel.stream().filter(expected::equals)
                    .findFirst().orElseThrow(AssertionError::new);
            OWLAxiom fromFactory = testPairwise.stream().filter(expected::equals)
                    .findFirst().orElseThrow(AssertionError::new);
            Assert.assertTrue(test instanceof ModelObject);

            testONTObject(expected, fromFactory, test);
        }
    }
}
