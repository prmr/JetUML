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

import static ca.mcgill.cs.jetuml.views.StringViewer.FONT;

import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.ToolGraphics;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

/**
 * Provides shared services for rendering an edge.
 */
public abstract class AbstractEdgeView implements EdgeView
{
	protected static final int MAX_DISTANCE = 3;
	private static final Text SIZE_TESTER = new Text();
	
	static
	{
		SIZE_TESTER.setFont(FONT);
	}
	
	private static final int DEGREES_180 = 180;
	
	private Edge aEdge;
	
	/**
	 * @param pEdge The edge to wrap.
	 */
	protected AbstractEdgeView(Edge pEdge)
	{
		aEdge = pEdge;
	}
	
	/**
	 * @return The shape.
	 */
	protected abstract Shape getShape();
	
	/**
	 * @param pText Some text to test.
	 * @return A bounds object to be used as
	 * metrics for the size of the string when rendered
	 * in the application font.
	 */
	protected static Bounds textBounds( String pText )
	{
		SIZE_TESTER.setText(pText);
		return SIZE_TESTER.getBoundsInLocal();
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
		Bounds bounds = getShape().getBoundsInLocal();
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
		Rectangle startBounds = edge().getStart().view().getBounds();
		Rectangle endBounds = edge().getEnd().view().getBounds();
		Point startCenter = startBounds.getCenter();
		Point endCenter = endBounds.getCenter();
		Direction toEnd = new Direction(startCenter, endCenter);
		return new Line(edge().getStart().view().getConnectionPoint(toEnd), 
				edge().getEnd().view().getConnectionPoint(toEnd.turn(DEGREES_180)));
	}

	@Override
	public void drawSelectionHandles(GraphicsContext pGraphics)
	{
		ToolGraphics.drawHandles(pGraphics, getConnectionPoints());		
	}
}
