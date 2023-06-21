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
	 * A note edge cannot connect two notes.
	 */
	public static SemanticConstraint noteEdgeDoesNotConnectTwoNoteNodes()
	{
		return (edge, diagram) -> !(edge.getClass() == NoteEdge.class && 
				edge.start().getClass() == NoteNode.class && edge.end().getClass() == NoteNode.class);
	}

	/**
	 * A note edge that ends in a point must start with a note.
	 */
	public static SemanticConstraint noteEdgeToPointMustStartWithNote()
	{
		return (Edge pEdge, Diagram pDiagram) -> !(pEdge.getClass() == NoteEdge.class && 
					!(pEdge.start().getClass() == NoteNode.class && pEdge.end().getClass() == PointNode.class || 
							pEdge.end().getClass() == NoteNode.class));
	}
	
	/**
	 * An edge can only be added to or from a note node if it is a note edge.
	 */
	public static SemanticConstraint noteNode()
	{
		return (Edge pEdge, Diagram pDiagram) -> {
			if( pEdge.start().getClass() == NoteNode.class || pEdge.end().getClass() == NoteNode.class )
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
			return pEdge.start() != pEdge.end();
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
			if( edge.getClass() == pEdge.getClass() && edge.start() == pEdge.start() && edge.end() == pEdge.end() )
			{
				result++;
			}
		}
		return result;
	}
}