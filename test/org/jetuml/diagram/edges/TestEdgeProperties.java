/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
 *     
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package org.jetuml.diagram.edges;

import static org.jetuml.testutils.CollectionAssertions.assertThat;
import static org.jetuml.testutils.CollectionAssertions.hasSetOfElementsEqualsTo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Property;
import org.jetuml.diagram.PropertyName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Checks that each edge type has the expected properties.
 */
@DisplayName("Each edge type should have the expected properties")
public class TestEdgeProperties
{
    @ParameterizedTest
    @MethodSource("argumentProvider")
	public void testEdgeProperties(Edge pEdge, Object[] pPropertyNames)
	{
		assertThat(getPropertyNamesAsList(pEdge), hasSetOfElementsEqualsTo, pPropertyNames);
	}
	
	private static List<PropertyName> getPropertyNamesAsList(Edge pEdge)
	{
		return StreamSupport.stream(pEdge.properties().spliterator(), false)
		    .map(Property::name).collect(Collectors.toList());
	}
	
	private static Stream<Arguments> argumentProvider() 
	{
        return Stream.of(
                Arguments.of(new AggregationEdge(), new PropertyName[] {PropertyName.AGGREGATION_TYPE, 
                		PropertyName.START_LABEL,  PropertyName.MIDDLE_LABEL, PropertyName.END_LABEL}),
                Arguments.of(new AssociationEdge(), new PropertyName[] {PropertyName.DIRECTIONALITY, 
                		PropertyName.START_LABEL,  PropertyName.MIDDLE_LABEL, PropertyName.END_LABEL}),
                Arguments.of(new CallEdge(), new PropertyName[] {PropertyName.SIGNAL, PropertyName.MIDDLE_LABEL}),
                Arguments.of(new DependencyEdge(), new PropertyName[] {PropertyName.MIDDLE_LABEL, PropertyName.DIRECTIONALITY}),
                Arguments.of(new GeneralizationEdge(), new PropertyName[] {PropertyName.GENERALIZATION_TYPE}),
                Arguments.of(new NoteEdge(), new PropertyName[] {}),
                Arguments.of(new ObjectCollaborationEdge(), new PropertyName[] {PropertyName.MIDDLE_LABEL}),
                Arguments.of(new ObjectReferenceEdge(), new PropertyName[] {}),
                Arguments.of(new ReturnEdge(), new PropertyName[] {PropertyName.MIDDLE_LABEL}),
                Arguments.of(new StateTransitionEdge(), new PropertyName[] {PropertyName.MIDDLE_LABEL}),
                Arguments.of(new UseCaseAssociationEdge(), new PropertyName[] {}),
                Arguments.of(new UseCaseDependencyEdge(), new PropertyName[] {PropertyName.USE_CASE_DEPENDENCY_TYPE}),
                Arguments.of(new UseCaseGeneralizationEdge(), new PropertyName[] {})
        );
    }
}
