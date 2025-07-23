/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2025 by McGill University.
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

import java.util.List;
import java.util.Set;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.ConstructorEdge;
import org.jetuml.diagram.edges.ReturnEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.diagram.validator.constraints.ConstraintCallEdgeBetweenCallNodes;
import org.jetuml.diagram.validator.constraints.ConstraintMaxNumberOfEdgesOfGivenTypeBetweenNodes;
import org.jetuml.diagram.validator.constraints.ConstraintMaxOneCaller;
import org.jetuml.diagram.validator.constraints.ConstraintReturnEdgeBetweenCallNodes;
import org.jetuml.diagram.validator.constraints.ConstraintReturnsToCaller;

/**
 * Validator for sequence diagrams.
 */
public class SequenceDiagramValidator extends AbstractDiagramValidator
{
	private static final Set<EdgeConstraint> CONSTRAINTS = Set.of(
			new ConstraintMaxNumberOfEdgesOfGivenTypeBetweenNodes(1),
			new ConstraintCallEdgeBetweenCallNodes(),
			new ConstraintMaxOneCaller(),
			new ConstraintReturnEdgeBetweenCallNodes(),
			new ConstraintReturnsToCaller());

	private static final Set<Class<? extends Node>> VALID_NODE_TYPES = Set.of(
			ImplicitParameterNode.class,
			CallNode.class);

	private static final Set<Class<? extends Edge>> VALID_EDGE_TYPES = Set.of(
			ConstructorEdge.class, 
			CallEdge.class,
			ReturnEdge.class);

	/**
	 * Creates a new validator for one sequence diagram.
	 *
	 * @param pDiagram The diagram to do semantic validity check on.
	 * @pre pDiagram != null && pDiagram.getType() == DiagramType.SEQUENCE
	 */
	public SequenceDiagramValidator(Diagram pDiagram)
	{
		super(pDiagram, VALID_NODE_TYPES, VALID_EDGE_TYPES, CONSTRAINTS);
		assert pDiagram.getType() == DiagramType.SEQUENCE;
	}

	/**
	 * Root nodes contain no call nodes.
	 */
	@Override
	protected boolean hasValidDiagramNodes()
	{
		return diagram().rootNodes().stream()
				.allMatch(node -> node.getClass() != CallNode.class) && maxOneRoot();
	}
	
	/*
	 * There can be at most one call node without a caller
	 */
	private boolean maxOneRoot()
	{
		return diagram().allNodes().stream()							// Nodes
				.filter(CallNode.class::isInstance)						// Call nodes
				.map(node -> diagram().edgesTo(node, CallEdge.class))	// Lists of callers to call nodes
				.mapToInt(List::size)									// Size of such lists
				.filter(nbOfCalleers -> nbOfCalleers == 0)				// Number of cases call nodes with no callers
				.count() <= 1;
	}
}
