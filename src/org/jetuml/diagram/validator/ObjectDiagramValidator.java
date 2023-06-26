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

/**
 * Validator for object diagrams.
 */
public class ObjectDiagramValidator extends AbstractDiagramValidator
{
	private static final Set<EdgeConstraint> CONSTRAINTS = Set.of(
			ObjectDiagramValidator::constraintValidReferenceEdge,
			ObjectDiagramValidator::constraintValidCollaborationEdge,
			AbstractDiagramValidator.createConstraintNoSelfEdgeForEdgeType(ObjectCollaborationEdge.class),
			AbstractDiagramValidator.createConstraintMaxNumberOfEdgesOfGivenTypeBetweenNodes(1),
			AbstractDiagramValidator.createConstraintNoDirectCyclesForEdgeType(ObjectCollaborationEdge.class));
	
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
	 * All root nodes must not be FieldNode.
	 */
	@Override
	protected boolean hasValidDiagramNodes()
	{
		return diagram().rootNodes().stream().noneMatch(node -> node instanceof FieldNode);
	}
	
	/*
	 * A reference edge can only be between an object node and a field node.
	 */
	private static boolean constraintValidReferenceEdge(Edge pEdge, Diagram pDiagram)
	{
		return !(pEdge.getClass() == ObjectReferenceEdge.class &&
					(pEdge.start().getClass() != FieldNode.class || pEdge.end().getClass() != ObjectNode.class));
	}
	
	/*
	 * A collaboration edge can only be between two object nodes.
	 */
	private static boolean constraintValidCollaborationEdge(Edge pEdge, Diagram pDiagram)
	{
		return !(pEdge.getClass() == ObjectCollaborationEdge.class &&
			(pEdge.start().getClass() != ObjectNode.class || pEdge.end().getClass() != ObjectNode.class));
	}
}
