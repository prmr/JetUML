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
import java.util.ArrayList;

import ca.mcgill.cs.stg.jetuml.graph.Node;

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
	ArrayList<Point2D> getPath(Node pStart, Node pEnd);
	
	
	
	/**
	 * Creates a straight (unsegmented) line (except
	 * in the case of self-paths).
	 */
	class Straight implements SegmentationStyle
	{
		@Override
		public ArrayList<Point2D> getPath(Node pStart, Node pEnd)
		{
			ArrayList<Point2D> r = new ArrayList<>();
			Point2D[] a = Utilities.connectionPoints(pStart);
		    Point2D[] b = Utilities.connectionPoints(pEnd);
		    Point2D p = a[0];
		    Point2D q = b[0];
		    double distance = p.distance(q);
		    if(distance == 0)
		    {
			   return null;
		    }
		    for(int i = 0; i < a.length; i++) 
		    {
			   for(int j = 0; j < b.length; j++)
			   {
				   double d = a[i].distance(b[j]);
				   if(d < distance)
				   {
					   p = a[i]; q = b[j];
					   distance = d;
				   }
			   }
		   }
		   r.add(p);
		   r.add(q);
		   return r;
		}
		
	}
} 

/**
 * Helper methods for the Segmentation Style strategies.
 */
final class Utilities
{
	private Utilities(){}
	
	static Point2D[] connectionPoints(Node pNode)
	{
		return new Point2D[] { pNode.getConnectionPoint(Direction.WEST) ,
							   pNode.getConnectionPoint(Direction.NORTH),
							   pNode.getConnectionPoint(Direction.EAST),
							   pNode.getConnectionPoint(Direction.SOUTH)};
	}
}
