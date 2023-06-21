package org.jetuml.diagram.validator;

import java.util.Set;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.ObjectCollaborationEdge;
import org.jetuml.diagram.edges.ObjectReferenceEdge;
import org.jetuml.diagram.nodes.FieldNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.jetuml.diagram.validator.constraints.EdgeConstraint;
import org.jetuml.diagram.validator.constraints.EdgeSemanticConstraints;
import org.jetuml.diagram.validator.constraints.ObjectDiagramSemanticConstraints;

/**
 * Validator for object diagrams.
 */
public class ObjectDiagramValidator extends AbstractDiagramValidator
{
	private static final Set<EdgeConstraint> CONSTRAINTS = Set.of(
			EdgeSemanticConstraints.maxEdges(1), 
			ObjectDiagramSemanticConstraints.collaboration(),
			ObjectDiagramSemanticConstraints.reference());

	private static final Set<Class<? extends Node>> VALID_NODE_TYPES = Set.of(
			ObjectNode.class, 
			FieldNode.class);
	
	private static final Set<Class<? extends Edge>> VALID_EDGE_TYPES = Set.of(
			ObjectReferenceEdge.class,
			ObjectCollaborationEdge.class);

	/**
	 * Creates a new validator for one object diagram.
	 *
	 * @param pDiagram The diagram to do semantic validity check on.
	 * @pre pDiagram != null && pDiagram.getType() == DiagramType.OBJECT
	 */
	public ObjectDiagramValidator(Diagram pDiagram)
	{
		super(pDiagram, VALID_NODE_TYPES, VALID_EDGE_TYPES, CONSTRAINTS);
		assert pDiagram.getType() == DiagramType.OBJECT;
	}

	/**
	 * 1. All root nodes must not be FieldNode 2. For ObjectNode root node, the
	 * children nodes it contains must only contain FieldNode
	 */
	@Override
	protected boolean hasValidNodes()
	{
		return diagram().rootNodes().stream().noneMatch(node -> node instanceof FieldNode);
	}
}
