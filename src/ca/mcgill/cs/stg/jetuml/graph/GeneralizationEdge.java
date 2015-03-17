package ca.mcgill.cs.stg.jetuml.graph;

import ca.mcgill.cs.stg.jetuml.framework.ArrowHead;
import ca.mcgill.cs.stg.jetuml.framework.BentStyle;
import ca.mcgill.cs.stg.jetuml.framework.LineStyle;

/**
 *  A straight edge with a triangle arrowhead representing including the generalization between two classes.
 */
public class GeneralizationEdge extends ClassRelationshipEdge
{
	/**
     *  Constructs the include relation edge.
     */
	public GeneralizationEdge()
	{
		setBentStyle(BentStyle.STRAIGHT);
	    setLineStyle(LineStyle.SOLID);
	    setEndArrowHead(ArrowHead.TRIANGLE);
	}
}
