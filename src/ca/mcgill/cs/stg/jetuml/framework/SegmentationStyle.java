/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 by the contributors of the JetUML project.
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

package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.geom.Point2D;

import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.graph.PackageNode;

/**
 * A strategy for drawing a segmented line between two nodes.
 * 
 * @author Martin P. Robillard
 *
 */
public interface SegmentationStyle
{
	/**
     * Gets the points at which a line joining two nodes
     * is bent according to this strategy.
     * @param pStart the starting node
     * @param pEnd the ending node
     * @return an array list of points at which to bend the
     * segmented line joining the two nodes
	 */
	Point2D[] getPath(Node pStart, Node pEnd);
	
	/**
	 * Creates a straight (unsegmented) line by choosing
	 * the connection points that induce the shortest path
	 * between two nodes (except in the case of self-paths). 
	 */
	class Straight implements SegmentationStyle
	{
		@Override
		public Point2D[] getPath(Node pStart, Node pEnd)
		{
			if( pStart == pEnd )
			{
				return Utilities.createSelfPath(pStart);
			}
			Point2D[] startConnectionPoints = Utilities.connectionPoints(pStart);
		    Point2D[] endConnectionPoints = Utilities.connectionPoints(pEnd);
		    Point2D start = startConnectionPoints[0];
		    Point2D end = endConnectionPoints[0];
		    double distance = start.distance(end);
		    
		    for( Point2D startPoint : startConnectionPoints)
		    {
		    	for( Point2D endPoint : endConnectionPoints )
		    	{
		    		double newDistance = startPoint.distance(endPoint);
		    		if( newDistance < distance )
		    		{
		    			start = startPoint;
		    			end = endPoint;
		    			distance = newDistance;
		    		}
		    	}
		    }

		    return new Point2D[] {start, end};
		}
		
	}
} 

/**
 * Helper methods for the Segmentation Style strategies.
 */
final class Utilities
{
	private static final int MARGIN = 20;
	
	private Utilities(){}
	
	static Point2D[] connectionPoints(Node pNode)
	{
		return new Point2D[] { pNode.getConnectionPoint(Direction.WEST) ,
							   pNode.getConnectionPoint(Direction.NORTH),
							   pNode.getConnectionPoint(Direction.EAST),
							   pNode.getConnectionPoint(Direction.SOUTH)};
	}
	
	/*
	 * The idea for creating a self path is to find the top left corner of 
	 * the actual figure and walk back N pixels away from it.
	 * Assumes that pNode is composed of rectangles with sides at least
	 * N wide.
	 */
	static Point2D[] createSelfPath(Node pNode)
	{
		Point2D topRight = findTopRightCorner(pNode);
		double x1 = topRight.getX() - MARGIN;
		double y1 = topRight.getY();
		double x2 = x1;
		double y2 = y1 - MARGIN;
		double x3 = x2 + MARGIN * 2;
		double y3 = y2;
		double x4 = x3;
		double y4 = topRight.getY() + MARGIN;
		double x5 = topRight.getX();
		double y5 = y4;
		
		return new Point2D[] {new Point2D.Double(x1, y1), new Point2D.Double(x2, y2),
							  new Point2D.Double(x3, y3), new Point2D.Double(x4, y4), new Point2D.Double(x5, y5)};
	}
	
	/*
	 * This solution is very complex if we can't assume any knowledge
	 * of Node types and only rely on getConnectionPoints, but it can
	 * be made quite optimal in exchange for an unpretty dependency to
	 * specific node types.
	 */
	private static Point2D findTopRightCorner(Node pNode)
	{
		if( pNode instanceof PackageNode )
		{
			return ((PackageNode)pNode).getTopRightCorner();
		}
		else
		{
			return new Point2D.Double(pNode.getBounds().getMaxX(), pNode.getBounds().getMinY());
		}
	}
}
