package org.jetuml.diagram.validator.constraints;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.edges.ObjectCollaborationEdge;
import org.jetuml.diagram.edges.ObjectReferenceEdge;
import org.jetuml.diagram.nodes.FieldNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.jetuml.diagram.validator.EdgeConstraint;

/**
 * Semantic constraints for object diagrams.
 */
public final class ObjectDiagramSemanticConstraints
{
	private ObjectDiagramSemanticConstraints() {}

	/**
	 * A collaboration edge can only be between two object nodes.
	 */
	public static EdgeConstraint collaboration()
	{
		return (Edge pEdge, Diagram pDiagram) -> {
			return !(pEdge.getClass() == ObjectCollaborationEdge.class &&
				(pEdge.start().getClass() != ObjectNode.class || pEdge.end().getClass() != ObjectNode.class));
		};
	}

	/**
	 * A reference edge can only be between an object node and a field node.
	 */
	public static EdgeConstraint reference()
	{
		return (Edge pEdge, Diagram pDiagram) -> {
			return !(pEdge.getClass() == ObjectReferenceEdge.class &&
					(pEdge.start().getClass() != FieldNode.class || pEdge.end().getClass() != ObjectNode.class));
		};
	}
}