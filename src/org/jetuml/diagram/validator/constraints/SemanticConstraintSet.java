package org.jetuml.diagram.validator.constraints;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;

/**
 * A set of constraints.
 */
public class SemanticConstraintSet
{
	private final Set<SemanticConstraint> aSemanticConstraints = new HashSet<>();

	/**
	 * @param pSemanticConstraints Constraints to add to the set.
	 */
	public SemanticConstraintSet(SemanticConstraint... pSemanticConstraints)
	{
		assert pSemanticConstraints != null;
		aSemanticConstraints.addAll(Arrays.asList(pSemanticConstraints));
	}

	/**
	 * Determines if all constraints in the set are satisfied.
	 * 
	 * @param pEdge The target edge.
	 * @param pStart The start node.
	 * @param pEnd The end node.
	 * @param pDiagram The diagram containing the edge and nodes.
	 * @return True if all constraints are satisfied.
	 */
	public boolean satisfied(Edge pEdge, Node pStart, Node pEnd, Diagram pDiagram)
	{
		for( SemanticConstraint semanticConstraint : aSemanticConstraints )
		{
			if( !semanticConstraint.satisfied(pEdge, pStart, pEnd, pDiagram) )
			{
				return false;
			}
		}
		return true;
	}
}