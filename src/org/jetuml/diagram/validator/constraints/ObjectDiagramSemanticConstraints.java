package org.jetuml.diagram.validator.constraints;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.ObjectCollaborationEdge;
import org.jetuml.diagram.edges.ObjectReferenceEdge;
import org.jetuml.diagram.nodes.FieldNode;
import org.jetuml.diagram.nodes.ObjectNode;

/**
 * Semantic constraints for object diagrams.
 */
public final class ObjectDiagramSemanticConstraints
{
	private ObjectDiagramSemanticConstraints() {}

	/**
	 * A collaboration edge can only be between two object nodes.
	 */
	public static SemanticConstraint collaboration()
	{
		return (Edge pEdge, Node pStart, Node pEnd, Diagram pDiagram) -> {
			return !(pEdge.getClass() == ObjectCollaborationEdge.class &&
				(pStart.getClass() != ObjectNode.class || pEnd.getClass() != ObjectNode.class));
		};
	}

	/**
	 * A reference edge can only be between an object node and a field node.
	 */
	public static SemanticConstraint reference()
	{
		return (Edge pEdge, Node pStart, Node pEnd, Diagram pDiagram) -> {
			return !(pEdge.getClass() == ObjectReferenceEdge.class &&
					(pStart.getClass() != FieldNode.class || pEnd.getClass() != ObjectNode.class));
		};
	}
}