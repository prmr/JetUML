/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018 by the contributors of the JetUML project.
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

import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Edge;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Shape;

/**
 * Provides shared services for rendering an edge.
 * 
 * @author Martin P. Robillard
 *
 */
public abstract class AbstractEdgeView2 implements EdgeView2
{
	private static final int MAX_DISTANCE = 3;
	private static final int DEGREES_180 = 180;
	
	private Edge aEdge;
	
	/**
	 * @param pEdge The edge to wrap.
	 */
	protected AbstractEdgeView2(Edge pEdge)
	{
		aEdge = pEdge;
	}
	
	/**
	 * @return The shape.
	 */
	protected abstract Shape getShape();
	
	/**
     * Completes drawing a path on the graphics context.
     * @param pGraphics the graphics context
     * @param pPath the path
	 */
	protected void completeDrawPath(GraphicsContext pGraphics, Path pPath)
	{
		for (PathElement element : pPath.getElements())
		{
			if (element instanceof MoveTo)
			{
				pGraphics.moveTo(((MoveTo) element).getX(), ((MoveTo) element).getY());
			}
			else if (element instanceof LineTo)
			{
				pGraphics.lineTo(((LineTo) element).getX(), ((LineTo) element).getY());
			}
			else if (element instanceof QuadCurveTo)
			{
				QuadCurveTo curve = (QuadCurveTo) element;
				pGraphics.quadraticCurveTo(curve.getControlX(), curve.getControlY(), curve.getX(), curve.getY());
			}
		}
		pGraphics.stroke();
	}
	
	/**
	 * @return The wrapped edge.
	 */
	protected Edge edge()
	{
		return aEdge;
	}
	
	@Override
	public boolean contains(Point pPoint)
	{
		Line conn = getConnectionPoints();
		if(pPoint.distance(conn.getPoint1()) <= MAX_DISTANCE || pPoint.distance(conn.getPoint2()) <= MAX_DISTANCE)
		{
			return false;
		}

		Shape fatPath = getShape();
		fatPath.setStrokeWidth(2 * MAX_DISTANCE);
		return fatPath.contains(pPoint.getX(), pPoint.getY());
	}
	
	@Override
	public Rectangle getBounds()
	{
		Bounds bounds = getShape().getBoundsInLocal();	// may need to fix
		return new Rectangle((int)bounds.getMinX(), (int)bounds.getMinY(), (int)bounds.getWidth(), (int)bounds.getHeight());
	}
	
	/** 
	 * The default behavior implemented by this method
	 * is to find the connection point that each start/end
	 * node provides for a direction that is oriented
	 * following a straight line connecting the center
	 * of the rectangular bounds for each node.
	 */
	@Override
	public Line getConnectionPoints()
	{
		Rectangle startBounds = edge().getStart().view2().getBounds();
		Rectangle endBounds = edge().getEnd().view2().getBounds();
		Point startCenter = startBounds.getCenter();
		Point endCenter = endBounds.getCenter();
		Direction toEnd = new Direction(startCenter, endCenter);
		return new Line(edge().getStart().view2().getConnectionPoint(toEnd), 
				edge().getEnd().view2().getConnectionPoint(toEnd.turn(DEGREES_180)));
	}
}
