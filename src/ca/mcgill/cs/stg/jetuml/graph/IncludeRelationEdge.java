package ca.mcgill.cs.stg.jetuml.graph;

import ca.mcgill.cs.stg.jetuml.framework.ArrowHead;
import ca.mcgill.cs.stg.jetuml.framework.BentStyle;
import ca.mcgill.cs.stg.jetuml.framework.LineStyle;

/**
 *  A straight dotted edge with a v arrowhead and proper middle label representing including the relation between two classes.
 */
public class IncludeRelationEdge extends ClassRelationshipEdge
{
	/**
     *  Constructs the include relation edge.
     */
	public IncludeRelationEdge()
	{
		setBentStyle(BentStyle.STRAIGHT);
	    setLineStyle(LineStyle.DOTTED);
	    setEndArrowHead(ArrowHead.V);
	    setMiddleLabel("\u00ABinclude\u00BB");
	}
}
