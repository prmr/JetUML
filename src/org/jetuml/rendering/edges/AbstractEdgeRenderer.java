/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package org.jetuml.rendering.edges;

import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.Edge;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.Direction;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.StringRenderer;
import org.jetuml.rendering.ToolGraphics;
import org.jetuml.rendering.StringRenderer.Alignment;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

/**
 * Provides shared services for viewing an edge.
 */
public abstract class AbstractEdgeRenderer implements EdgeRenderer
{
	protected static final int MAX_DISTANCE = 3;
	protected static final int BUTTON_SIZE = 25;
	protected static final int OFFSET = 3;
	protected static final int MAX_LENGTH_FOR_NORMAL_FONT = 15;
	private static final StringRenderer SIZE_TESTER = StringRenderer.get(Alignment.TOP_LEFT);
	
	private static final int DEGREES_180 = 180;
	
	private final DiagramRenderer aParent;
	
	protected AbstractEdgeRenderer(DiagramRenderer pParent)
	{
		aParent = pParent;
	}
	
	protected DiagramRenderer parent()
	{
		return aParent;
	}
	
	/**
	 * The default behavior is to draw a straight line between
	 * the connections points oriented in the direction of each 
	 * other node.
	 * 
	 * @param pEdge The edge whose shape we want
	 * @return The shape. 
	 * @pre pEdge != null
	 */
	protected Shape getShape(Edge pEdge)
	{
		assert pEdge != null;
		Line endPoints = getConnectionPoints(pEdge);
		Path path = new Path();
		path.getElements().addAll(new MoveTo(endPoints.x1(), endPoints.y1()), 
				new LineTo(endPoints.x2(), endPoints.y2()));
		return path;
	}
	
	/**
	 * @param pText Some text to test.
	 * @return The width and height of the text.
	 */
	protected static Dimension textDimensions( String pText )
	{
		return SIZE_TESTER.getDimension(pText);
	}
	
	@Override
	public boolean contains(DiagramElement pElement, Point pPoint)
	{
		Edge edge = (Edge) pElement;
		// Purposefully does not include the arrow head and labels, which create large bounds.
		Line conn = getConnectionPoints(edge);
		if(pPoint.distance(conn.point1()) <= MAX_DISTANCE || pPoint.distance(conn.point2()) <= MAX_DISTANCE)
		{
			return false;
		}

		Shape fatPath = getShape(edge);
		fatPath.setStrokeWidth(2 * MAX_DISTANCE);
		return fatPath.contains(pPoint.x(), pPoint.y());
	}
	
	@Override
	public Rectangle getBounds(DiagramElement pElement)
	{
		Bounds bounds = getShape((Edge)pElement).getBoundsInLocal();
		return new Rectangle((int)bounds.getMinX(), (int)bounds.getMinY(), (int)bounds.getWidth(), (int)bounds.getHeight());
	}
	
	/*
	 * The default behavior implemented by this method
	 * is to find the connection point that each start/end
	 * node provides for a direction that is oriented
	 * following a straight line connecting the center
	 * of the rectangular bounds for each node.
	 */
	@Override
	public Line getConnectionPoints(Edge pEdge)
	{
		Rectangle startBounds = parent().getBounds(pEdge.start());
		Rectangle endBounds = parent().getBounds(pEdge.end());
		Point startCenter = startBounds.center();
		Point endCenter = endBounds.center();
		Direction toEnd = Direction.fromLine(startCenter, endCenter);
		return new Line(parent().getConnectionPoints(pEdge.start(), toEnd), 
				parent().getConnectionPoints(pEdge.end(), toEnd.rotatedBy(DEGREES_180)));
	}

	@Override
	public void drawSelectionHandles(DiagramElement pElement, GraphicsContext pGraphics)
	{
		ToolGraphics.drawHandles(pGraphics, getConnectionPoints((Edge)pElement));		
	}
	
	protected String wrapLabel(String pString, int pDistanceInX, int pDistanceInY)
	{
		final int singleCharWidth = SIZE_TESTER.getDimension(" ").width();
		final int singleCharHeight = SIZE_TESTER.getDimension(" ").height();

		int lineLength = MAX_LENGTH_FOR_NORMAL_FONT;
		double distanceInX = pDistanceInX / singleCharWidth;
		double distanceInY = pDistanceInY / singleCharHeight;
		if(distanceInX > 0)
		{
			double angleInDegrees = Math.toDegrees(Math.atan(distanceInY/distanceInX));
			lineLength = Math.max(MAX_LENGTH_FOR_NORMAL_FONT, (int)((distanceInX / 4) * (1 - angleInDegrees / DEGREES_180)));
		}
		return StringRenderer.wrapString(pString, lineLength);
	}
}
