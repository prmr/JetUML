package ca.mcgill.cs.stg.jetuml.graph;

import ca.mcgill.cs.stg.jetuml.framework.ArrowHead;
import ca.mcgill.cs.stg.jetuml.framework.BentStyle;

/**
 *  A segmented edge with a black diamond arrowhead representing a composition of the class.
 */
public class CompositionEdge extends ClassRelationshipEdge
{
	/**
     *  Constructs the composition edge.
     */
	public CompositionEdge()
	{
		setBentStyle(BentStyle.HVH);
		setStartArrowHead(ArrowHead.BLACK_DIAMOND);
	}
}
