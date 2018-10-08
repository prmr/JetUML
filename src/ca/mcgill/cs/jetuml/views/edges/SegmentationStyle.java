/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/

package ca.mcgill.cs.jetuml.views.edges;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.geom.Direction;
import javafx.geometry.Point2D;

/**
 * A strategy for drawing a segmented line between two nodes.
 */
public interface SegmentationStyle
{
	/**
	 * The side of a rectangle.
	 * This seems to be redundant with Direction, but to 
	 * overload Direction to mean both a side and a direction is
	 * confusing.
	 */
	enum Side
	{WEST, NORTH, EAST, SOUTH;
		
		boolean isEastWest() 
		{ return this == WEST || this == EAST; }
		
		Direction getDirection()
		{
			switch(this)
			{
			case WEST:
				return Direction.WEST;
			case NORTH:
				return Direction.NORTH;
			case EAST:
				return Direction.EAST;
			case SOUTH:
				return Direction.SOUTH;
			default:
				return null;
			}
		}
		
		Side flip()
		{
			switch(this)
			{
			case WEST:
				return EAST;
			case NORTH:
				return SOUTH;
			case EAST:
				return WEST;
			case SOUTH:
				return NORTH;
			default:
				return null;
			}
		}
	}
	
	/**
	 * Determines if it is possible to use this segmentation style.
	 * @param pEdge The edge to draw
	 * @return true if it is possible to use the segmentation style.
	 */
	boolean isPossible(Edge pEdge);
	
	/**
     * Gets the points at which the line representing an
     * edge is bent according to this strategy.
     * @param pEdge the Edge for which a path is determine
     * @param pGraph the graph holding the edge. Can be null.
     * @return an array list of points at which to bend the
     * segmented line representing the edge. Never null.
	 */
	Point2D[] getPath(Edge pEdge, Diagram pGraph);
	
	/**
	 * Returns which side of the node attached to
	 * an edge is attached to the edge.
	 * @param pEdge The edge to check.
	 * @param pNode The node to check.
	 * @return The side the edge leaves from.
	 * @pre pNode == pEdge.getStart() || pNode == pEdge.getEnd()
	 */
	Side getAttachedSide(Edge pEdge, Node pNode);
}
