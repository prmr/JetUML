package org.jetuml.diagram.validator;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;

/**
 * Constraint on how an edge is connected to nodes in a diagram.
 */
public interface EdgeConstraint 
{
	/**
	 * Determines if a constraint is satisfied.
	 * 
	 * @param pEdge The edge being validated.
	 * @param pDiagram The diagram containing the edge.
	 * @return True if the edge is satisfied.
	 * @pre pEdge != null && pDiagram != null && pDiagram.contains(pEdge)
	 * @pre pEdge.start() != null && pEdge.end() != null;
	 */
	boolean satisfied(Edge pEdge, Diagram pDiagram);
}
