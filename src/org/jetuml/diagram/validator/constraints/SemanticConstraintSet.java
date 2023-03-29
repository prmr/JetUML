package org.jetuml.diagram.validator.constraints;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;

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
	 * @param pDiagram The diagram containing the edge and nodes.
	 * @return True if all constraints are satisfied.
	 */
	public boolean satisfied(Edge pEdge, Diagram pDiagram)
	{
		for( SemanticConstraint semanticConstraint : aSemanticConstraints )
		{
			if( !semanticConstraint.satisfied(pEdge, pDiagram) )
			{
				return false;
			}
		}
		return true;
	}
}