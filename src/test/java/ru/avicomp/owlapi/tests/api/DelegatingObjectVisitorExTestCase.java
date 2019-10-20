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
package ru.avicomp.owlapi.tests.api;

import org.junit.Test;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.DelegatingObjectVisitorEx;
import ru.avicomp.owlapi.tests.api.baseclasses.TestBase;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SuppressWarnings({"javadoc"})
public class DelegatingObjectVisitorExTestCase extends TestBase {

    @Test
    public void testAssertion() {
        OWLObjectVisitorEx<Object> test = mock(OWLObjectVisitorEx.class);
        DelegatingObjectVisitorEx<Object> testsubject = new DelegatingObjectVisitorEx<>(
                test);
        testsubject.visit(mock(OWLDeclarationAxiom.class));
        testsubject.visit(mock(OWLDatatypeDefinitionAxiom.class));
        testsubject.visit(mock(OWLAnnotationPropertyRangeAxiom.class));
        testsubject.visit(mock(OWLAnnotationPropertyDomainAxiom.class));
        testsubject.visit(mock(OWLSubAnnotationPropertyOfAxiom.class));
        testsubject.visit(mock(OWLAnnotationAssertionAxiom.class));
        testsubject.visit(mock(OWLEquivalentDataPropertiesAxiom.class));
        testsubject.visit(mock(OWLClassAssertionAxiom.class));
        testsubject.visit(mock(OWLEquivalentClassesAxiom.class));
        testsubject.visit(mock(OWLDataPropertyAssertionAxiom.class));
        testsubject.visit(mock(OWLDisjointUnionAxiom.class));
        testsubject.visit(mock(OWLSymmetricObjectPropertyAxiom.class));
        testsubject.visit(mock(OWLDataPropertyRangeAxiom.class));
        testsubject.visit(mock(OWLFunctionalDataPropertyAxiom.class));
        testsubject.visit(mock(OWLSameIndividualAxiom.class));
        testsubject.visit(mock(OWLSubPropertyChainOfAxiom.class));
        testsubject.visit(mock(OWLInverseObjectPropertiesAxiom.class));
        testsubject.visit(mock(OWLHasKeyAxiom.class));
        testsubject.visit(mock(OWLTransitiveObjectPropertyAxiom.class));
        testsubject.visit(mock(OWLIrreflexiveObjectPropertyAxiom.class));
        testsubject.visit(mock(OWLSubDataPropertyOfAxiom.class));
        testsubject.visit(mock(OWLInverseFunctionalObjectPropertyAxiom.class));
        testsubject.visit(mock(OWLDisjointClassesAxiom.class));
        testsubject.visit(mock(OWLDataPropertyDomainAxiom.class));
        testsubject.visit(mock(OWLObjectPropertyDomainAxiom.class));
        testsubject.visit(mock(OWLEquivalentObjectPropertiesAxiom.class));
        testsubject.visit(mock(OWLSubClassOfAxiom.class));
        testsubject.visit(mock(OWLNegativeObjectPropertyAssertionAxiom.class));
        testsubject.visit(mock(OWLAsymmetricObjectPropertyAxiom.class));
        testsubject.visit(mock(OWLReflexiveObjectPropertyAxiom.class));
        testsubject.visit(mock(OWLObjectPropertyRangeAxiom.class));
        testsubject.visit(mock(OWLObjectPropertyAssertionAxiom.class));
        testsubject.visit(mock(OWLFunctionalObjectPropertyAxiom.class));
        testsubject.visit(mock(OWLSubObjectPropertyOfAxiom.class));
        testsubject.visit(mock(OWLNegativeDataPropertyAssertionAxiom.class));
        testsubject.visit(mock(OWLDifferentIndividualsAxiom.class));
        testsubject.visit(mock(OWLDisjointDataPropertiesAxiom.class));
        testsubject.visit(mock(OWLDisjointObjectPropertiesAxiom.class));
        testsubject.visit(mock(SWRLRule.class));
        testsubject.visit(mock(OWLDataAllValuesFrom.class));
        testsubject.visit(mock(OWLDataSomeValuesFrom.class));
        testsubject.visit(mock(OWLObjectOneOf.class));
        testsubject.visit(mock(OWLObjectHasSelf.class));
        testsubject.visit(mock(OWLObjectMaxCardinality.class));
        testsubject.visit(mock(OWLDataMaxCardinality.class));
        testsubject.visit(mock(OWLDataExactCardinality.class));
        testsubject.visit(mock(OWLDataMinCardinality.class));
        testsubject.visit(mock(OWLDataHasValue.class));
        testsubject.visit(mock(OWLObjectSomeValuesFrom.class));
        testsubject.visit(mock(OWLObjectComplementOf.class));
        testsubject.visit(mock(OWLObjectUnionOf.class));
        testsubject.visit(mock(OWLObjectIntersectionOf.class));
        testsubject.visit(mock(OWLObjectExactCardinality.class));
        testsubject.visit(mock(OWLObjectMinCardinality.class));
        testsubject.visit(mock(OWLObjectHasValue.class));
        testsubject.visit(mock(OWLObjectAllValuesFrom.class));
        testsubject.visit(mock(OWLClass.class));
        testsubject.visit(mock(OWLFacetRestriction.class));
        testsubject.visit(mock(OWLDatatypeRestriction.class));
        testsubject.visit(mock(OWLDataUnionOf.class));
        testsubject.visit(mock(OWLDataIntersectionOf.class));
        testsubject.visit(mock(OWLDataOneOf.class));
        testsubject.visit(mock(OWLDataComplementOf.class));
        testsubject.visit(mock(OWLDatatype.class));
        testsubject.visit(mock(OWLLiteral.class));
        testsubject.visit(mock(OWLObjectInverseOf.class));
        testsubject.visit(mock(OWLObjectProperty.class));
        testsubject.visit(mock(OWLDataProperty.class));
        testsubject.visit(mock(OWLAnnotationProperty.class));
        testsubject.visit(mock(OWLNamedIndividual.class));
        testsubject.visit(mock(OWLAnnotation.class));
        testsubject.visit(mock(IRI.class));
        testsubject.visit(mock(OWLAnonymousIndividual.class));
        testsubject.visit(mock(SWRLVariable.class));
        testsubject.visit(mock(SWRLIndividualArgument.class));
        testsubject.visit(mock(SWRLLiteralArgument.class));
        testsubject.visit(mock(SWRLSameIndividualAtom.class));
        testsubject.visit(mock(SWRLDifferentIndividualsAtom.class));
        testsubject.visit(mock(SWRLClassAtom.class));
        testsubject.visit(mock(SWRLDataRangeAtom.class));
        testsubject.visit(mock(SWRLObjectPropertyAtom.class));
        testsubject.visit(mock(SWRLDataPropertyAtom.class));
        testsubject.visit(mock(SWRLBuiltInAtom.class));
        testsubject.visit(mock(OWLOntology.class));
        verify(test).visit(any(OWLDeclarationAxiom.class));
        verify(test).visit(any(OWLDatatypeDefinitionAxiom.class));
        verify(test).visit(any(OWLAnnotationPropertyRangeAxiom.class));
        verify(test).visit(any(OWLAnnotationPropertyDomainAxiom.class));
        verify(test).visit(any(OWLSubAnnotationPropertyOfAxiom.class));
        verify(test).visit(any(OWLAnnotationAssertionAxiom.class));
        verify(test).visit(any(OWLEquivalentDataPropertiesAxiom.class));
        verify(test).visit(any(OWLClassAssertionAxiom.class));
        verify(test).visit(any(OWLEquivalentClassesAxiom.class));
        verify(test).visit(any(OWLDataPropertyAssertionAxiom.class));
        verify(test).visit(any(OWLDisjointUnionAxiom.class));
        verify(test).visit(any(OWLSymmetricObjectPropertyAxiom.class));
        verify(test).visit(any(OWLDataPropertyRangeAxiom.class));
        verify(test).visit(any(OWLFunctionalDataPropertyAxiom.class));
        verify(test).visit(any(OWLSameIndividualAxiom.class));
        verify(test).visit(any(OWLSubPropertyChainOfAxiom.class));
        verify(test).visit(any(OWLInverseObjectPropertiesAxiom.class));
        verify(test).visit(any(OWLHasKeyAxiom.class));
        verify(test).visit(any(OWLTransitiveObjectPropertyAxiom.class));
        verify(test).visit(any(OWLIrreflexiveObjectPropertyAxiom.class));
        verify(test).visit(any(OWLSubDataPropertyOfAxiom.class));
        verify(test).visit(any(OWLInverseFunctionalObjectPropertyAxiom.class));
        verify(test).visit(any(OWLDisjointClassesAxiom.class));
        verify(test).visit(any(OWLDataPropertyDomainAxiom.class));
        verify(test).visit(any(OWLObjectPropertyDomainAxiom.class));
        verify(test).visit(any(OWLEquivalentObjectPropertiesAxiom.class));
        verify(test).visit(any(OWLSubClassOfAxiom.class));
        verify(test).visit(any(OWLNegativeObjectPropertyAssertionAxiom.class));
        verify(test).visit(any(OWLAsymmetricObjectPropertyAxiom.class));
        verify(test).visit(any(OWLReflexiveObjectPropertyAxiom.class));
        verify(test).visit(any(OWLObjectPropertyRangeAxiom.class));
        verify(test).visit(any(OWLObjectPropertyAssertionAxiom.class));
        verify(test).visit(any(OWLFunctionalObjectPropertyAxiom.class));
        verify(test).visit(any(OWLSubObjectPropertyOfAxiom.class));
        verify(test).visit(any(OWLNegativeDataPropertyAssertionAxiom.class));
        verify(test).visit(any(OWLDifferentIndividualsAxiom.class));
        verify(test).visit(any(OWLDisjointDataPropertiesAxiom.class));
        verify(test).visit(any(OWLDisjointObjectPropertiesAxiom.class));
        verify(test).visit(any(SWRLRule.class));
        verify(test).visit(any(OWLDataAllValuesFrom.class));
        verify(test).visit(any(OWLDataSomeValuesFrom.class));
        verify(test).visit(any(OWLObjectOneOf.class));
        verify(test).visit(any(OWLObjectHasSelf.class));
        verify(test).visit(any(OWLObjectMaxCardinality.class));
        verify(test).visit(any(OWLDataMaxCardinality.class));
        verify(test).visit(any(OWLDataExactCardinality.class));
        verify(test).visit(any(OWLDataMinCardinality.class));
        verify(test).visit(any(OWLDataHasValue.class));
        verify(test).visit(any(OWLObjectSomeValuesFrom.class));
        verify(test).visit(any(OWLObjectComplementOf.class));
        verify(test).visit(any(OWLObjectUnionOf.class));
        verify(test).visit(any(OWLObjectIntersectionOf.class));
        verify(test).visit(any(OWLObjectExactCardinality.class));
        verify(test).visit(any(OWLObjectMinCardinality.class));
        verify(test).visit(any(OWLObjectHasValue.class));
        verify(test).visit(any(OWLObjectAllValuesFrom.class));
        verify(test).visit(any(OWLClass.class));
        verify(test).visit(any(OWLFacetRestriction.class));
        verify(test).visit(any(OWLDatatypeRestriction.class));
        verify(test).visit(any(OWLDataUnionOf.class));
        verify(test).visit(any(OWLDataIntersectionOf.class));
        verify(test).visit(any(OWLDataOneOf.class));
        verify(test).visit(any(OWLDataComplementOf.class));
        verify(test).visit(any(OWLDatatype.class));
        verify(test).visit(any(OWLLiteral.class));
        verify(test).visit(any(OWLObjectInverseOf.class));
        verify(test).visit(any(OWLObjectProperty.class));
        verify(test).visit(any(OWLDataProperty.class));
        verify(test).visit(any(OWLAnnotationProperty.class));
        verify(test).visit(any(OWLNamedIndividual.class));
        verify(test).visit(any(OWLAnnotation.class));
        verify(test).visit(any(IRI.class));
        verify(test).visit(any(OWLAnonymousIndividual.class));
        verify(test).visit(any(SWRLVariable.class));
        verify(test).visit(any(SWRLIndividualArgument.class));
        verify(test).visit(any(SWRLLiteralArgument.class));
        verify(test).visit(any(SWRLSameIndividualAtom.class));
        verify(test).visit(any(SWRLDifferentIndividualsAtom.class));
        verify(test).visit(any(SWRLClassAtom.class));
        verify(test).visit(any(SWRLDataRangeAtom.class));
        verify(test).visit(any(SWRLObjectPropertyAtom.class));
        verify(test).visit(any(SWRLDataPropertyAtom.class));
        verify(test).visit(any(SWRLBuiltInAtom.class));
        verify(test).visit(any(OWLOntology.class));
    }
}
