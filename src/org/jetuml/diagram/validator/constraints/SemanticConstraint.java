package org.jetuml.diagram.validator.constraints;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;

/**
 * Root type for all constraints.
 */
public interface SemanticConstraint 
{
	/**
	 * Determines if a constraint is satisfied.
	 * 
	 * @param pEdge The edge being checked.
	 * @param pDiagram The diagram containing the edge.
	 * @return True if the edge is satisfied.
	 */
	boolean satisfied(Edge pEdge, Diagram pDiagram);
}
