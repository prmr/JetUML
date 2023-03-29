package org.jetuml.diagram.validator.constraints;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.PointNode;

/**
 * Constraints for edges.
 */
public final class EdgeSemanticConstraints
{
	private EdgeSemanticConstraints() {}

	/**
	 * An ending point node can only exist if it's connected to note edge.
	 */
	public static SemanticConstraint pointNode()
	{
		return (Edge pEdge, Diagram pDiagram) -> !(pEdge.getEnd().getClass() == PointNode.class && pEdge.getClass() != NoteEdge.class);

	}

	/**
	 * A note edge can only be added between: - Any node and a note node. - A
	 * note node and a point node.
	 */
	public static SemanticConstraint noteEdge()
	{
		return (Edge pEdge, Diagram pDiagram) -> !(pEdge.getClass() == NoteEdge.class && 
					!(pEdge.getStart().getClass() == NoteNode.class && pEdge.getEnd().getClass() == PointNode.class || 
							pEdge.getEnd().getClass() == NoteNode.class));
	}

	/**
	 * An edge can only be added to or from a note node if it is a note edge.
	 */
	public static SemanticConstraint noteNode()
	{
		return (Edge pEdge, Diagram pDiagram) -> {
			if( pEdge.getStart().getClass() == NoteNode.class || pEdge.getEnd().getClass() == NoteNode.class )
			{
				return pEdge.getClass() == NoteEdge.class;
			}
			return true;
		};
	}

	/**
	 * Only pNumber of edges of the same type are allowed in one direction
	 * between two nodes.
	 */
	public static SemanticConstraint maxEdges(int pNumber)
	{
		assert pNumber > 0;
		return (Edge pEdge, Diagram pDiagram) -> {
			return numberOfEdges(pEdge, pDiagram) <= pNumber;
		};
	}

	/**
	 * Self-edges are not allowed.
	 */
	public static SemanticConstraint noSelfEdge()
	{
		return (Edge pEdge, Diagram pDiagram) -> {
			return pEdge.getStart() != pEdge.getEnd();
		};
	}

	/*
	 * Returns the number of edges of type pType between pStart and pEnd
	 */
	private static int numberOfEdges(Edge pEdge, Diagram pDiagram)
	{
		assert pEdge != null && pDiagram != null;
		int result = 0;
		for( Edge edge : pDiagram.edges() )
		{
			if( edge.getClass() == pEdge.getClass() && edge.getStart() == pEdge.getStart() && edge.getEnd() == pEdge.getEnd() )
			{
				result++;
			}
		}
		return result;
	}
}