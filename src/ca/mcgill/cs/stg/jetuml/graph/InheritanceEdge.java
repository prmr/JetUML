package ca.mcgill.cs.stg.jetuml.graph;

import ca.mcgill.cs.stg.jetuml.framework.ArrowHead;
import ca.mcgill.cs.stg.jetuml.framework.BentStyle;

/**
 *  A segmented solid edge with a triangle arrowhead representing inheritance from a class.
 */
public class InheritanceEdge extends ClassRelationshipEdge
{
	
	/**
     *  Constructs the inheritance edge.
     */
	public InheritanceEdge()
	{
		setBentStyle(BentStyle.VHV);
		setEndArrowHead(ArrowHead.TRIANGLE);
	}
}
