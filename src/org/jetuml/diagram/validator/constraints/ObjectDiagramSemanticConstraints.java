package org.jetuml.diagram.validator.constraints;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
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
		return (Edge pEdge, Diagram pDiagram) -> {
			return !(pEdge.getClass() == ObjectCollaborationEdge.class &&
				(pEdge.getStart().getClass() != ObjectNode.class || pEdge.getEnd().getClass() != ObjectNode.class));
		};
	}

	/**
	 * A reference edge can only be between an object node and a field node.
	 */
	public static SemanticConstraint reference()
	{
		return (Edge pEdge, Diagram pDiagram) -> {
			return !(pEdge.getClass() == ObjectReferenceEdge.class &&
					(pEdge.getStart().getClass() != FieldNode.class || pEdge.getEnd().getClass() != ObjectNode.class));
		};
	}
}