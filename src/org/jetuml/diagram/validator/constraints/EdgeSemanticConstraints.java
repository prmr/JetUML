package org.jetuml.diagram.validator.constraints;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.validator.EdgeConstraint;

/**
 * Constraints for edges.
 */
public final class EdgeSemanticConstraints
{
	private EdgeSemanticConstraints() {}
	
	/**
	 * Only pNumber of edges of the same type are allowed in one direction
	 * between two nodes.
	 */
	public static EdgeConstraint maxEdges(int pNumber)
	{
		assert pNumber > 0;
		return (Edge pEdge, Diagram pDiagram) -> {
			return numberOfEdges(pEdge, pDiagram) <= pNumber;
		};
	}

	/**
	 * Self-edges are not allowed.
	 */
	public static EdgeConstraint noSelfEdge()
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