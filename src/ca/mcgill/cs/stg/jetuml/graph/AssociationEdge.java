package ca.mcgill.cs.stg.jetuml.graph;

import ca.mcgill.cs.stg.jetuml.framework.ArrowHead;
import ca.mcgill.cs.stg.jetuml.framework.BentStyle;

/**
 *  A segmented edge with a v arrowhead representing implementation of an interface.
 */
public class AssociationEdge extends ClassRelationshipEdge
{
	/**
     *  Constructs the association edge.
     */
	public AssociationEdge()
	{
		setBentStyle(BentStyle.HVH);
		setEndArrowHead(ArrowHead.V);
	}
}
