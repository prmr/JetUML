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

/**
 * Validator for sequence diagrams.
 */
public class SequenceDiagramValidator extends AbstractDiagramValidator
{
	private static final Set<EdgeConstraint> CONSTRAINTS = Set.of(
			AbstractDiagramValidator.createConstraintMaxNumberOfEdgesOfGivenTypeBetweenNodes(1),
			SequenceDiagramValidator::constraintCallEdgeBetweenCallNodes,
			SequenceDiagramValidator::constraintMaxOneCaller,
			SequenceDiagramValidator::constraintReturnEdgeBetweenCallNodes,
			SequenceDiagramValidator::constraintReturnsToCaller );

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
	
	/*
	 * A call or constructor edge (subtype of CallEdge) can only be between call nodes
	 */
	private static boolean constraintCallEdgeBetweenCallNodes(Edge pEdge, Diagram pDiagram)
	{
		return !(pEdge instanceof CallEdge && (pEdge.start().getClass() != CallNode.class ||
				pEdge.end().getClass() != CallNode.class));
	}
	
	/*
	 * A return can only be between call nodes
	 */
	private static boolean constraintReturnEdgeBetweenCallNodes(Edge pEdge, Diagram pDiagram)
	{
		return !(pEdge instanceof ReturnEdge && (pEdge.start().getClass() != CallNode.class ||
				pEdge.end().getClass() != CallNode.class));
	}
	
	/*
	 * There can be at most one caller to a call node. 
	 */
	private static boolean constraintMaxOneCaller(Edge pEdge, Diagram pDiagram)
	{
		return pDiagram.allNodes().stream()								// Nodes
				.filter(CallNode.class::isInstance)						// Call nodes
				.map(node -> pDiagram.edgesTo(node, CallEdge.class))	// Lists of callers to call nodes
				.mapToInt(List::size)									// Size of such lists
				.allMatch(size -> size <= 1);
	}
	
	/*
	 * A return edge must return to its caller, which must be a different node.
	 */
	private static boolean constraintReturnsToCaller(Edge pEdge, Diagram pDiagram)
	{
		if( pEdge.getClass() != ReturnEdge.class )
		{
			return true;
		}
		List<Edge> calls = pDiagram.edgesTo(pEdge.start(), CallEdge.class);
		if(calls.size() != 1) 
		{
			return false;
		}
		return pEdge.end() == calls.get(0).start() && pEdge.end().getParent() != pEdge.start().getParent();
	}
}
