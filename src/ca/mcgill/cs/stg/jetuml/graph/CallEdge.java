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

package ca.mcgill.cs.stg.jetuml.graph;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import ca.mcgill.cs.stg.jetuml.framework.ArrowHead;
import ca.mcgill.cs.stg.jetuml.framework.Direction;

/**
 *   An edge that joins two call nodes.
 */
public class CallEdge extends SegmentedLineEdge
{
	private boolean aSignal;
	
	/**
	 * Creates a non-signal edge.
	 */
	public CallEdge()
	{
		setSignal(false);
	}

	/**
     * Gets the signal property.
     * @return true if this is a signal edge
	 */
	public boolean isSignal() 
	{ return aSignal; }

	/**
     * Sets the signal property.
     * @param pNewValue true if this is a signal edge
     */      
	public void setSignal(boolean pNewValue) 
	{ 
		aSignal = pNewValue; 
		if(aSignal)
		{
			setEndArrowHead(ArrowHead.HALF_V);
		}
		else
		{
			setEndArrowHead(ArrowHead.V);
		}
	}

	@Override
	protected ArrayList<Point2D> getPoints()
	{
		ArrayList<Point2D> a = new ArrayList<>();
		Node endNode = getEnd();
		Rectangle2D start = getStart().getBounds();
		Rectangle2D end = endNode.getBounds();
      
		if(endNode instanceof CallNode && ((CallNode)endNode).getParent() == ((CallNode)getStart()).getParent())
		{
			Point2D p = new Point2D.Double(start.getMaxX(), end.getY() - CallNode.CALL_YGAP / 2);
			Point2D q = new Point2D.Double(end.getMaxX(), end.getY());
			Point2D s = new Point2D.Double(q.getX() + end.getWidth(), q.getY());
			Point2D r = new Point2D.Double(s.getX(), p.getY());
			a.add(p);
			a.add(r);
			a.add(s);
			a.add(q);
		}
		else if(endNode instanceof PointNode) // show nicely in tool bar
		{
			a.add(new Point2D.Double(start.getMaxX(), start.getY()));
			a.add(new Point2D.Double(end.getX(), start.getY()));
		}
		else     
		{
			Direction d = new Direction(start.getX() - end.getX(), 0);
			Point2D endPoint = getEnd().getConnectionPoint(d);
         
			if(start.getCenterX() < endPoint.getX())
			{
				a.add(new Point2D.Double(start.getMaxX(), endPoint.getY()));
			}
			else
			{
				a.add(new Point2D.Double(start.getX(), endPoint.getY()));
			}
			a.add(endPoint);
		}
		return a;
	}
}


