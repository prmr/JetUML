/*
Violet - A program for editing UML diagrams.

Copyright (C) 2002 Cay S. Horstmann (http://horstmann.com)

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package ca.mcgill.cs.stg.jetuml.graph;

import java.awt.Graphics2D;
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
	public Rectangle2D getBounds(Graphics2D pGraphics2D)
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
