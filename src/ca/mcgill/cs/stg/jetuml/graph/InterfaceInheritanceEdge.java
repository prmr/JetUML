package ca.mcgill.cs.stg.jetuml.graph;

import ca.mcgill.cs.stg.jetuml.framework.ArrowHead;
import ca.mcgill.cs.stg.jetuml.framework.BentStyle;
import ca.mcgill.cs.stg.jetuml.framework.LineStyle;

/**
 *  A segmented dotted edge with a triangle arrowhead representing implementation of an interface.
 */
public class InterfaceInheritanceEdge extends ClassRelationshipEdge
{
	
	/**
     *  Constructs the implementation edge.
     */
	public InterfaceInheritanceEdge()
	{
		setBentStyle(BentStyle.VHV);
		setLineStyle(LineStyle.DOTTED);
		setEndArrowHead(ArrowHead.TRIANGLE);
	}
}
