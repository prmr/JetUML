package ca.mcgill.cs.stg.jetuml.graph;

import ca.mcgill.cs.stg.jetuml.framework.ArrowHead;
import ca.mcgill.cs.stg.jetuml.framework.BentStyle;
import ca.mcgill.cs.stg.jetuml.framework.LineStyle;

/**
 *  A straight dotted edge with a v arrowhead and proper middle label representing extending the relation between two classes.
 */
public class ExtendRelationEdge extends ClassRelationshipEdge
{
	/**
     *  Constructs the communication edge.
     */
	public ExtendRelationEdge()
	{
		setBentStyle(BentStyle.STRAIGHT);
		setLineStyle(LineStyle.DOTTED);
		setEndArrowHead(ArrowHead.V);
		setMiddleLabel("\u00ABextend\u00BB");
	}
}
