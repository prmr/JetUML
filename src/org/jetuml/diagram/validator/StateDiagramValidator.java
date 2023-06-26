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
import org.jetuml.diagram.edges.StateTransitionEdge;
import org.jetuml.diagram.nodes.FinalStateNode;
import org.jetuml.diagram.nodes.InitialStateNode;
import org.jetuml.diagram.nodes.StateNode;

/**
 * Validator for state diagrams.
 */
public class StateDiagramValidator extends AbstractDiagramValidator
{
	private static final Set<EdgeConstraint> CONSTRAINTS = Set.of(
			AbstractDiagramValidator.createConstraintMaxNumberOfEdgesOfGivenTypeBetweenNodes(2),
			StateDiagramValidator::constraintValidTransitionEdgeStartNode,
			StateDiagramValidator::constraintValidTransitionEdgeEndNode);

	private static final Set<Class<? extends Node>> VALID_NODE_TYPES = Set.of(
			StateNode.class,
			InitialStateNode.class, 
			FinalStateNode.class);

	private static final Set<Class<? extends Edge>> VALID_EDGE_TYPES = Set.of(
			StateTransitionEdge.class);

	/**
	 * Creates a new validator for one state diagram.
	 *
	 * @param pDiagram The diagram to do semantic validity check on.
	 * @pre pDiagram != null && pDiagram.getType() == DiagramType.STATE
	 */
	public StateDiagramValidator(Diagram pDiagram)
	{
		super(pDiagram, VALID_NODE_TYPES, VALID_EDGE_TYPES, CONSTRAINTS);
		assert pDiagram.getType() == DiagramType.STATE;
	}
	
	/*
	 * A transition can only start in an initial node or a state node
	 */
	private static boolean constraintValidTransitionEdgeStartNode(Edge pEdge, Diagram pDiagram)
	{
		return !(pEdge.getClass() == StateTransitionEdge.class && 
				pEdge.start().getClass() != InitialStateNode.class &&
				 pEdge.start().getClass() != StateNode.class);
	}
	
	/*
	 * A transition can only end in an final node or a state node or a note node
	 */
	private static boolean constraintValidTransitionEdgeEndNode(Edge pEdge, Diagram pDiagram)
	{
		return !(pEdge.getClass() == StateTransitionEdge.class && 
				 pEdge.end().getClass() != FinalStateNode.class && 
				 pEdge.end().getClass() != StateNode.class);
	}
}
