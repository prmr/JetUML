package org.jetuml.diagram.validator;

import java.util.Set;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.UseCaseAssociationEdge;
import org.jetuml.diagram.edges.UseCaseDependencyEdge;
import org.jetuml.diagram.edges.UseCaseGeneralizationEdge;
import org.jetuml.diagram.nodes.ActorNode;
import org.jetuml.diagram.nodes.UseCaseNode;
import org.jetuml.diagram.validator.constraints.EdgeConstraint;
import org.jetuml.diagram.validator.constraints.EdgeSemanticConstraints;

/**
 * Validator for use case diagrams.
 */
public class UseCaseDiagramValidator extends AbstractDiagramValidator
{
	private static final Set<EdgeConstraint> CONSTRAINTS = Set.of(
			EdgeSemanticConstraints.maxEdges(1), 
			EdgeSemanticConstraints.noSelfEdge());

	private static final Set<Class<? extends Node>> VALID_NODE_TYPES = Set.of(
			ActorNode.class, 
			UseCaseNode.class);

	private static final Set<Class<? extends Edge>> VALID_EDGE_TYPES = Set.of(
			UseCaseAssociationEdge.class,
			UseCaseDependencyEdge.class, 
			UseCaseGeneralizationEdge.class);

	/**
	 * Creates a new validator for one use case diagram.
	 *
	 * @param pDiagram The diagram to do semantic validity check on.
	 * @pre pDiagram != null && pDiagram.getType() == DiagramType.USECASE
	 */

	public UseCaseDiagramValidator(Diagram pDiagram)
	{
		super(pDiagram, VALID_NODE_TYPES, VALID_EDGE_TYPES, CONSTRAINTS);
		assert pDiagram.getType() == DiagramType.USECASE;
	}
}
