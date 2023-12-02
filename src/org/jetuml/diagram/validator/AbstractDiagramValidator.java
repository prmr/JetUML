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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.jetuml.annotations.TemplateMethod;
import org.jetuml.application.ApplicationResources;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.diagram.validator.constraints.ConstraintNoEdgeToPointExceptNoteEdge;
import org.jetuml.diagram.validator.constraints.ConstraintValidNoteEdge;
import org.jetuml.gui.NotificationService;
import org.jetuml.gui.ToastNotification;

/**
 * Implementation of the general scaffolding for validating a diagram.
 */
abstract class AbstractDiagramValidator implements DiagramValidator
{
	private static final Set<Class<? extends Node>> UNIVERSAL_NODES_TYPES = 
			Set.of(PointNode.class, NoteNode.class);
	private static final Set<Class<? extends Edge>> UNIVERSAL_EDGES_TYPES = 
			Set.of(NoteEdge.class);
	private static final Set<EdgeConstraint> UNIVERSAL_CONSTRAINTS =
			Set.of(new ConstraintValidNoteEdge(),
					new ConstraintNoEdgeToPointExceptNoteEdge());
	
	private final Diagram aDiagram;
	private final Set<Class<? extends Node>> aValidNodeTypes = new HashSet<>();
	private final Set<Class<? extends Edge>> aValidEdgeTypes = new HashSet<>();
	private final Set<EdgeConstraint> aConstraints = new HashSet<>();

	/**
	 * Creates a validator for pDiagram.
	 *
	 * @param pDiagram The diagram that we want to validate
	 * @param pValidNodeTypes The node types valid for this diagram, in addition to the 
 	 *	universal nodes valid by default.
 	 * @param pValidEdgeTypes The edge types valid for this diagram, in addition to the 
 	 *	universal edges valid by default.
	 * @pre pDiagram != null && pValidNodeTypes != null && pValidEdgeTypes != null;
	 */
	protected AbstractDiagramValidator(Diagram pDiagram, Set<Class<? extends Node>> pValidNodeTypes, 
			Set<Class<? extends Edge>> pValidEdgeTypes, Set<EdgeConstraint> pEdgeConstraints)
	{
		assert pDiagram != null && pValidNodeTypes != null && pValidEdgeTypes != null;
		
		aDiagram = pDiagram;
		
		aValidNodeTypes.addAll(UNIVERSAL_NODES_TYPES);
		aValidNodeTypes.addAll(pValidNodeTypes);
		
		aValidEdgeTypes.addAll(UNIVERSAL_EDGES_TYPES);
		aValidEdgeTypes.addAll(pValidEdgeTypes);
		
		aConstraints.addAll(UNIVERSAL_CONSTRAINTS);
		aConstraints.addAll(pEdgeConstraints);
	}

	@Override
	public final boolean isValid()
	{
		return hasValidStructure() && hasValidSemantics();
	}

	@Override
	@TemplateMethod
	public final boolean hasValidStructure()
	{
		return hasValidElementTypes() && hasValidNodes();
	}

	/**
	 * @return True iff the diagram respects all required semantic validation
	 * rules.
	 * @pre hasValidStructure()
	 */
	@Override
	public final boolean hasValidSemantics()
	{
		return aDiagram.edges().stream()
				.allMatch(edge -> allConstraintsSatisfied(edge));
	}
	
	private boolean allConstraintsSatisfied(Edge pEdge)
	{
		// We retrieve the first constraint that is not satisfied (if it exists)
		Optional<EdgeConstraint> invalidatingConstraint = aConstraints.stream()
				.filter(constraint -> !constraint.satisfied(pEdge, aDiagram))
				.findFirst();

		// If no such constraint exist, then they are all satisfied
		if (invalidatingConstraint.isEmpty())
		{
			return true;
		}

		EdgeConstraint constraint = invalidatingConstraint.get();
		String errorMessage = ApplicationResources.RESOURCES.getString("error."+constraint.getClass().getSimpleName());

		NotificationService.instance().spawnNotification(errorMessage, ToastNotification.Type.ERROR);

		return false;
	}

	private boolean hasValidElementTypes()
	{
		return aDiagram.allNodes().stream()
					.allMatch(node -> aValidNodeTypes.contains(node.getClass())) &&
			   aDiagram.edges().stream()
			   		.allMatch(edge -> aValidEdgeTypes.contains(edge.getClass()));
	}

	@TemplateMethod
	private boolean hasValidNodes()
	{
		return hasValidPointNodes() && hasValidDiagramNodes();
	}
	
	/**
	 * @return Point nodes must be connected to an edge
	 */
	private boolean hasValidPointNodes()
	{
		return diagram().rootNodes().stream()
			.filter(PointNode.class::isInstance)
			.allMatch(node -> diagram().edgesConnectedTo(node).iterator().hasNext());
	}
	
	/**
	 * Step method in the template method design pattern to allow 
	 * processing diagram-specific node validation.
	 */
	protected boolean hasValidDiagramNodes()
	{
		return true;
	}

	/**
	 * @return The diagram wrapped by this validator.
	 */
	public final Diagram diagram()
	{
		return aDiagram;
	}
}
