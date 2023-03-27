package org.jetuml.diagram.validator.constraints;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;

/**
 * Root type for all constraints.
 */
public interface SemanticConstraint 
{
	/**
	 * Determines if a constraint is satisfied.
	 * 
	 * @param pEdge The edge being checked.
	 * @param pStart The start node for the edge.
	 * @param pEnd The end node for the edge.
	 * @param pDiagram The diagram containing the edge.
	 * @return True if the edge is satisfied.
	 */
	boolean satisfied(Edge pEdge, Node pStart, Node pEnd, Diagram pDiagram);
}
