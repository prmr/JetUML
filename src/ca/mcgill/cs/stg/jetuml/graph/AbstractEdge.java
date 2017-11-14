/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2017 by the contributors of the JetUML project.
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

import java.awt.BasicStroke;
import java.awt.Shape;

import ca.mcgill.cs.stg.jetuml.geom.Conversions;
import ca.mcgill.cs.stg.jetuml.geom.Direction;
import ca.mcgill.cs.stg.jetuml.geom.Line;
import ca.mcgill.cs.stg.jetuml.geom.Point;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;

/**
 *  Supplies convenience implementations for a number of methods
 *  in the Edge interface. In particular, the class implements
 *  support for "containment testing" of edges, i.e., testing
 *  whether a point falls on a edge. This is done by obtaining 
 *  the shape of the edge and stroking it with a fat stroke.
 *  NOTE: Ideally, you should be able to draw the same shape that
 *  is used for containment testing. However, in JDK 1.4, 
 *  BasicStroke.createStrokedShape returned shitty-looking shapes,
 *  so drawing the stroked shapes should be visually tested for 
 *  each edge type.
 */
abstract class AbstractEdge implements Edge
{  
	private static final int DEGREES_180 = 180;
	private static final double MAX_DISTANCE = 3.0;
	private Node aStart;
	private Node aEnd;
	private Graph aGraph;

	/**
	 * Returns the path that should be stroked to
	 * draw this edge. The path does not include
	 * arrow tips or labels.
	 * @return a path along the edge
	 */
	protected abstract Shape getShape();

	@Override
	public Rectangle getBounds()
	{
		return Conversions.toRectangle(getShape().getBounds()); 
	}

	@Override
	public final boolean contains(Point pPoint)
	{
		// The end points may contain small nodes, so don't match them
		Line conn = getConnectionPoints();
		if(pPoint.distance(conn.getPoint1()) <= MAX_DISTANCE || pPoint.distance(conn.getPoint2()) <= MAX_DISTANCE)
		{
			return false;
		}

		Shape fatPath = new BasicStroke((float)(2 * MAX_DISTANCE)).createStrokedShape(getShape());
		return fatPath.contains(Conversions.toPoint2D(pPoint));
	}

	@Override
	public Edge clone()
	{
		try
		{
			return (Edge) super.clone();
		}
		catch (CloneNotSupportedException exception)
		{
			return null;
		}
	}

	@Override
	public void connect(Node pStart, Node pEnd, Graph pGraph)
	{  
		assert pStart != null && pEnd != null;
		aStart = pStart;
		aEnd = pEnd;
		aGraph = pGraph;
	}

	@Override
	public Graph getGraph()
	{
		return aGraph;
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

	/* 
	 * The default behavior implemented by this method
	 * is to find the connection point that each start/end
	 * node provides for a direction that is oriented
	 * following a straight line connecting the center
	 * of the rectangular bounds for each node.

	 * @see ca.mcgill.cs.stg.jetuml.graph.Edge#getConnectionPoints()
	 */
	@Override
	public Line getConnectionPoints()
	{
		Rectangle startBounds = aStart.getBounds();
		Rectangle endBounds = aEnd.getBounds();
		Point startCenter = startBounds.getCenter();
		Point endCenter = endBounds.getCenter();
		Direction toEnd = new Direction(startCenter, endCenter);
		return new Line(aStart.getConnectionPoint(toEnd), aEnd.getConnectionPoint(toEnd.turn(DEGREES_180)));
	}

	/**
	 * Wrap the string in an html container and 
	 * escape the angle brackets.
	 * @param pRawLabel The initial string.
	 * @pre pRawLabel != null;
	 * @return The string prepared for rendering as HTML
	 */
	protected static String toHtml(String pRawLabel)
	{
		assert pRawLabel != null;
		StringBuilder lReturn = new StringBuilder();
		lReturn.append("<html>");
		lReturn.append(pRawLabel.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
		lReturn.append("</html>");
		return lReturn.toString();
	}
}
