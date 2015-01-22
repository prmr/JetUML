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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import ca.mcgill.cs.stg.jetuml.ArrowHead;
import ca.mcgill.cs.stg.jetuml.LineStyle;

/**
 *  An edge that joins two call nodes.
 */
public class ReturnEdge extends SegmentedLineEdge
{
	/**
	 * Constructs a standard return edge.
	 */
	public ReturnEdge()
	{
		setEndArrowHead(ArrowHead.V);
		setLineStyle(LineStyle.DOTTED);
	}

	@Override
	protected ArrayList<Point2D> getPoints()
	{
		ArrayList<Point2D> lReturn = new ArrayList<>();
		Node endNode = getEnd();
		Rectangle2D start = getStart().getBounds();
		Rectangle2D end = getEnd().getBounds();
		if(endNode instanceof PointNode) // show nicely in tool bar
		{
			lReturn.add(new Point2D.Double(end.getX(), end.getY()));
			lReturn.add(new Point2D.Double(start.getMaxX(), end.getY()));
		}      
		else if(start.getCenterX() < end.getCenterX())
		{
			lReturn.add(new Point2D.Double(start.getMaxX(), start.getMaxY()));
			lReturn.add(new Point2D.Double(end.getX(), start.getMaxY()));
		}
		else
		{
			lReturn.add(new Point2D.Double(start.getX(), start.getMaxY()));
			lReturn.add(new Point2D.Double(end.getMaxX(), start.getMaxY()));
		}
		return lReturn;
	}
}
