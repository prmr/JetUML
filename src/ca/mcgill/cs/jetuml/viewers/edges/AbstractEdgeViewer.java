/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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
package ca.mcgill.cs.jetuml.viewers.edges;

import static ca.mcgill.cs.jetuml.views.StringViewer.FONT;

import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.nodes.NodeViewerRegistry;
import ca.mcgill.cs.jetuml.views.ToolGraphics;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

/**
 * Provides shared services for viewing an edge.
 */
public abstract class AbstractEdgeViewer implements EdgeViewer
{
	protected static final int MAX_DISTANCE = 3;
	protected static final int BUTTON_SIZE = 25;
	protected static final int OFFSET = 3;
	private static final Text SIZE_TESTER = new Text();
	
	static
	{
		SIZE_TESTER.setFont(FONT);
	}
	
	private static final int DEGREES_180 = 180;
	
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
		path.getElements().addAll(new MoveTo(endPoints.getX1(), endPoints.getY1()), 
				new LineTo(endPoints.getX2(), endPoints.getY2()));
		return path;
	}
	
	/**
	 * @param pText Some text to test.
	 * @return The width and height of the text.
	 */
	protected static Dimension textDimensions( String pText )
	{
		SIZE_TESTER.setText(pText);
		Bounds bounds = SIZE_TESTER.getBoundsInLocal();
		return new Dimension((int)bounds.getWidth(), (int)bounds.getHeight());
	}
	
	@Override
	public boolean contains(Edge pEdge, Point pPoint)
	{
		// Purposefully does not include the arrow head and labels, which create large bounds.
		Line conn = getConnectionPoints(pEdge);
		if(pPoint.distance(conn.getPoint1()) <= MAX_DISTANCE || pPoint.distance(conn.getPoint2()) <= MAX_DISTANCE)
		{
			return false;
		}

		Shape fatPath = getShape(pEdge);
		fatPath.setStrokeWidth(2 * MAX_DISTANCE);
		return fatPath.contains(pPoint.getX(), pPoint.getY());
	}
	
	@Override
	public Rectangle getBounds(Edge pEdge)
	{
		Bounds bounds = getShape(pEdge).getBoundsInLocal();
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
		Rectangle startBounds = NodeViewerRegistry.getBounds(pEdge.getStart());
		Rectangle endBounds = NodeViewerRegistry.getBounds(pEdge.getEnd());
		Point startCenter = startBounds.getCenter();
		Point endCenter = endBounds.getCenter();
		Direction toEnd = new Direction(startCenter, endCenter);
		return new Line(NodeViewerRegistry.getConnectionPoints(pEdge.getStart(), toEnd), 
				NodeViewerRegistry.getConnectionPoints(pEdge.getEnd(), toEnd.turn(DEGREES_180)));
	}

	@Override
	public void drawSelectionHandles(Edge pEdge, GraphicsContext pGraphics)
	{
		ToolGraphics.drawHandles(pGraphics, getConnectionPoints(pEdge));		
	}
}
