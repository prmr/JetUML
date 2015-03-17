package ca.mcgill.cs.stg.jetuml.graph;

import ca.mcgill.cs.stg.jetuml.framework.ArrowHead;
import ca.mcgill.cs.stg.jetuml.framework.BentStyle;

/**
 *  A segmented edge with a diamond arrowhead representing a collection of the class.
 */
public class AggregationEdge extends ClassRelationshipEdge
{
	/**
     *  Constructs the aggregation edge.
     */
	public AggregationEdge()
	{
		setBentStyle(BentStyle.HVH);
		setStartArrowHead(ArrowHead.DIAMOND);
	}
}
