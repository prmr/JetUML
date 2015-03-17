package ca.mcgill.cs.stg.jetuml.graph;

import ca.mcgill.cs.stg.jetuml.framework.ArrowHead;
import ca.mcgill.cs.stg.jetuml.framework.BentStyle;
import ca.mcgill.cs.stg.jetuml.framework.LineStyle;

/**
 *  A straight dotted edge with a v arrowhead representing dependency between classes.
 */
public class DependencyEdge extends ClassRelationshipEdge
{
	
	/**
     *  Constructs the dependency edge.
     */
	public DependencyEdge()
	{
		setBentStyle(BentStyle.STRAIGHT);
		setLineStyle(LineStyle.DOTTED);
		setEndArrowHead(ArrowHead.V);
	}
}
