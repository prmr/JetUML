/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2023 by McGill University.
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
package org.jetuml.diagram.validator;

import java.util.Set;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.AggregationEdge;
import org.jetuml.diagram.edges.AssociationEdge;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.GeneralizationEdge;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.InterfaceNode;
import org.jetuml.diagram.nodes.PackageDescriptionNode;
import org.jetuml.diagram.nodes.PackageNode;
import org.jetuml.diagram.validator.constraints.ConstraintMaxNumberOfEdgesOfGivenTypeBetweenNodes;
import org.jetuml.diagram.validator.constraints.ConstraintNoCombinedAssociationAggregation;
import org.jetuml.diagram.validator.constraints.ConstraintNoDirectCyclesForEdgeType;
import org.jetuml.diagram.validator.constraints.ConstraintNoSelfEdgeForEdgeType;

/**
 * Validator for class diagrams.
 */
public class ClassDiagramValidator extends AbstractDiagramValidator
{
	private static final Set<EdgeConstraint> CONSTRAINTS = Set.of(
			new ConstraintMaxNumberOfEdgesOfGivenTypeBetweenNodes(1),
			new ConstraintNoSelfEdgeForEdgeType(GeneralizationEdge.class),
			new ConstraintNoSelfEdgeForEdgeType(DependencyEdge.class),
			new ConstraintNoDirectCyclesForEdgeType(DependencyEdge.class),
			new ConstraintNoDirectCyclesForEdgeType(GeneralizationEdge.class),
			new ConstraintNoDirectCyclesForEdgeType(AggregationEdge.class),
			new ConstraintNoDirectCyclesForEdgeType(AssociationEdge.class),
			new ConstraintNoCombinedAssociationAggregation());

	private static final Set<Class<? extends Node>> VALID_NODE_TYPES = Set.of(
			ClassNode.class, 
			InterfaceNode.class,
			PackageNode.class, 
			PackageDescriptionNode.class);

	private static final Set<Class<? extends Edge>> VALID_EDGE_TYPES = Set.of(
			DependencyEdge.class,
			GeneralizationEdge.class, 
			AssociationEdge.class, 
			AggregationEdge.class);

	/**
	 * Creates a new validator for a class diagram.
	 *
	 * @param pDiagram The diagram to validate
	 * @pre pDiagram != null && pDiagram.getType() == DiagramType.CLASS
	 */
	public ClassDiagramValidator(Diagram pDiagram)
	{
		super(pDiagram, VALID_NODE_TYPES, VALID_EDGE_TYPES, CONSTRAINTS);
		assert pDiagram.getType() == DiagramType.CLASS;
	}
}
