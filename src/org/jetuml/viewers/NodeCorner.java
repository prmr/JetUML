/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2022 by McGill University.
 *     
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package org.jetuml.viewers;

import org.jetuml.diagram.Node;
import org.jetuml.geom.Direction;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.RenderingFacade;
import org.jetuml.viewers.edges.NodeIndex;

/**
 * Represents the 4 corners of a node. Used to plan the placement of self-edges.
 *
 */
public enum NodeCorner 
{
	TOP_RIGHT, TOP_LEFT, BOTTOM_LEFT, BOTTOM_RIGHT;
	
	private static final int TWENTY_PIXELS = 20;
	
	/**
	 * Gets the horizontal (North or South) NodeIndex associated with pCorner.
	 * @param pCorner the node corner
	 * @return +3 if the corner is a right corner, -3 otherwise
	 */
	public static NodeIndex getHorizontalIndex(NodeCorner pCorner)
	{
		if (pCorner == BOTTOM_RIGHT || pCorner == TOP_RIGHT)
		{
			return NodeIndex.PLUS_THREE;
		}
		else
		{
			return NodeIndex.MINUS_THREE;
		}
	}
	
	/**
	 * Gets the vertical (East or West) NodeIndex associated with pCorner.
	 * @param pCorner the node corner
	 * @return +1 if the corner is a top corner, -1 otherwise.
	 */
	public static NodeIndex getVerticalIndex(NodeCorner pCorner)
	{
		if (pCorner == TOP_LEFT || pCorner == TOP_RIGHT)
		{
			return NodeIndex.MINUS_ONE;
		}
		else
		{
			return NodeIndex.PLUS_ONE;
		}
	}
	
	/**
	 * Gets the Direction (either North or South) describing pCorner's horizontal position on a node.
	 * @param pCorner the node corner of interest
	 * @return NORTH if the corner is a top corner, SOUTH if the corner is a bottom corner.
	 * @pre pCorner != null;
	 */
	public static Direction horizontalSide(NodeCorner pCorner)
	{
		assert pCorner != null;
		if (pCorner == TOP_RIGHT || pCorner == TOP_LEFT)
		{
			return Direction.NORTH;
		}
		else
		{
			return Direction.SOUTH;
		}
	}
	
	
	/**
	 * Gets the Direction (either EAST or WEST) describing pCorner's vertical position on a node.
	 * @param pCorner the node corner of interest
	 * @return EAST if the corner is a right corner, WEST if the corner is a left corner.
	 */
	public static Direction verticalSide(NodeCorner pCorner)
	{
		assert pCorner != null;
		if (pCorner == TOP_RIGHT || pCorner == BOTTOM_RIGHT)
		{
			return Direction.EAST;
		}
		else
		{
			return Direction.WEST;
		}
	}
	
	/**
	 * Returns an array of [startPoint, endPoint] where a self-edge attached to the pCorner of pNode would connect.
	 * @param pCorner the NodeCorner of interest
	 * @param pNode the node of interest 
	 * @return an array containing the start point and end point for a self edge attached to the pCorner corner of pNode.
	 */
	public static Point[] toPoints(NodeCorner pCorner, Node pNode)
	{
		Rectangle nodeBounds = RenderingFacade.getBounds(pNode);
		Point startPoint;
		Point endPoint;		
		if (pCorner == TOP_RIGHT)
		{
			startPoint = new Point(nodeBounds.getMaxX() - TWENTY_PIXELS, nodeBounds.getY());
			endPoint = new Point(nodeBounds.getMaxX(), nodeBounds.getY() + TWENTY_PIXELS);
		}
		else if (pCorner == TOP_LEFT)
		{
			startPoint = new Point(nodeBounds.getX() + TWENTY_PIXELS, nodeBounds.getY());
			endPoint = new Point(nodeBounds.getX(), nodeBounds.getY() + TWENTY_PIXELS);
		}
		else if (pCorner == BOTTOM_LEFT)
		{
			startPoint = new Point(nodeBounds.getX() + TWENTY_PIXELS, nodeBounds.getMaxY());
			endPoint = new Point(nodeBounds.getX(), nodeBounds.getMaxY() - TWENTY_PIXELS);
		}
		else //BOTTOM_RIGHT
		{
			startPoint = new Point(nodeBounds.getMaxX() - TWENTY_PIXELS, nodeBounds.getMaxY());
			endPoint = new Point(nodeBounds.getMaxX(), nodeBounds.getMaxY() - TWENTY_PIXELS);
		}
		return new Point[] {startPoint, endPoint};
	}
}

