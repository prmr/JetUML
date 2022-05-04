package ca.mcgill.cs.jetuml.viewers.edges;

import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.viewers.Grid;
import ca.mcgill.cs.jetuml.viewers.NodeSide;

/**
 * Represents indexed positions on the faces of nodes where edges can attach.
 * North-facing and South-facing sides of nodes have indices in range -4 to +4.
 * East and West-facing sides of nodes have indices in range -2 to +2.
 */
public enum NodeIndex 
{
	MINUS_FOUR, MINUS_THREE, MINUS_TWO, MINUS_ONE, ZERO,
	PLUS_ONE, PLUS_TWO, PLUS_THREE, PLUS_FOUR;
	
	private static final int NUM_SPACES_NS = 10;
	private static final int NUM_SPACES_EW = 6;
	
	/**
	 * Returns a point on pNodeFace at the position represented by this index.
	 * @param pNodeFace a Line representing the side of pNode where the point is needed.
	 * @param pAttachmentSide the side of the node of interest
	 * @return a point on pNodeFace at the pNodeIndex position
	 * @pre pNodeFace != null
	 */
	public Point toPoint(Line pNodeFace, NodeSide pAttachmentSide)
	{
		//determine the offset from the center point. 
		float spacing = spaceBetweenConnectionPoints(pNodeFace, pAttachmentSide);
		int offset = (int) ((ordinal() - 4) * spacing);
		
		//Determine center point and add the offset to the center point
		Point center;
		if(pAttachmentSide.isNorthSouth())
		{
			center = Grid.snappedHorizontally(
					new Point(((pNodeFace.getX2() - pNodeFace.getX1())/2) + pNodeFace.getX1(), pNodeFace.getY1()));
			return new Point(center.getX() + offset, center.getY());
		}
		else 
		{
			center = Grid.snappedVertically( 
					new Point(pNodeFace.getX1(), ((pNodeFace.getY2() - pNodeFace.getY1())/2) + pNodeFace.getY1()));
			return new Point(center.getX(), center.getY() + offset);
		}
	}
	
	/**
	 * Determines the number of pixels in between edge connection points on pNode. 
	 * This allows the space between NodeIndex connection points
	 *  to increase proportionally with the width or height of the node. 
	 * @param pNodeFace a line representing the pAttachmentSide of a node
	 * @param pAttachmentSide A side of a node. 
	 * @return the spacing in between connection points on pNodeFace. 
	 * @pre pNodeFace != null && pAttachmentSide != null
	 */
	private static float spaceBetweenConnectionPoints(Line pNodeFace, NodeSide pAttachmentSide)
	{
		assert pNodeFace != null && pAttachmentSide != null;
		if(pAttachmentSide.isNorthSouth())
		{
			return (float) (Math.abs((pNodeFace.getX2() - pNodeFace.getX1()) / NUM_SPACES_NS));
		}
		else
		{
			return (float) (Math.abs((pNodeFace.getY2() - pNodeFace.getY1()) / NUM_SPACES_EW));
		}
	}
}
