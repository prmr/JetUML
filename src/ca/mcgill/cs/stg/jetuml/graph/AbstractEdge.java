/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 Cay S. Horstmann and the contributors of the 
 * JetUML project.
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

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import ca.mcgill.cs.stg.jetuml.framework.Direction;

/**
 *  A class that supplies convenience implementations for 
 *  a number of methods in the Edge interface.
 */
abstract class AbstractEdge implements Edge
{  
	private static final int DEGREES_180 = 180;
	private Node aStart;
	private Node aEnd;
	
	@Override
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException exception)
		{
			return null;
		}
	}

	@Override
	public void connect(Node pStart, Node pEnd)
	{  
		aStart = pStart;
		aEnd = pEnd;
	}

	@Override
	public Node getStart()
	{
		return aStart;
	}

	@Override
	public Node getEnd()
	{
		return aEnd;
	}

	@Override
	public Rectangle2D getBounds()
	{
		Line2D conn = getConnectionPoints();      
		Rectangle2D rectangle = new Rectangle2D.Double();
		rectangle.setFrameFromDiagonal(conn.getX1(), conn.getY1(), conn.getX2(), conn.getY2());
		return rectangle;
	}

	@Override
	public Line2D getConnectionPoints()
	{
		Rectangle2D startBounds = aStart.getBounds();
		Rectangle2D endBounds = aEnd.getBounds();
		Point2D startCenter = new Point2D.Double(startBounds.getCenterX(), startBounds.getCenterY());
		Point2D endCenter = new Point2D.Double(endBounds.getCenterX(), endBounds.getCenterY());
		Direction toEnd = new Direction(startCenter, endCenter);
		return new Line2D.Double(aStart.getConnectionPoint(toEnd), aEnd.getConnectionPoint(toEnd.turn(DEGREES_180)));
   }
}
