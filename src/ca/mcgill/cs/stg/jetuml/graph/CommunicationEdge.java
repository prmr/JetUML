package ca.mcgill.cs.stg.jetuml.graph;

import ca.mcgill.cs.stg.jetuml.framework.ArrowHead;
import ca.mcgill.cs.stg.jetuml.framework.BentStyle;
import ca.mcgill.cs.stg.jetuml.framework.LineStyle;

/**
 *  A straight edge representing communication between two classes.
 */
public class CommunicationEdge extends ClassRelationshipEdge
{
	/**
     *  Constructs the communication edge.
     */
	public CommunicationEdge()
	{
		setBentStyle(BentStyle.STRAIGHT);
		setLineStyle(LineStyle.SOLID);
		setEndArrowHead(ArrowHead.NONE);
	}
}
