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
package ca.mcgill.cs.jetuml.viewers.edges;

import java.util.ArrayList;

import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ConstructorEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.geom.Conversions;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.ArrowHead;
import ca.mcgill.cs.jetuml.viewers.ArrowHeadViewer;
import ca.mcgill.cs.jetuml.viewers.LineStyle;
import ca.mcgill.cs.jetuml.viewers.StringViewer;
import ca.mcgill.cs.jetuml.viewers.ToolGraphics;
import ca.mcgill.cs.jetuml.viewers.StringViewer.Alignment;
import ca.mcgill.cs.jetuml.viewers.StringViewer.TextDecoration;
import ca.mcgill.cs.jetuml.viewers.nodes.NodeViewerRegistry;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

/**
 * A viewer to show call edges in a sequence diagrams. These are labeled
 * edges that are either straight or self-edges, in a solid line, and 
 * have either a V or half-V arrow head.
 */
public final class CallEdgeViewer extends AbstractEdgeViewer
{	
	private static final StringViewer CENTERED_STRING_VIEWER = StringViewer.get(Alignment.CENTER_CENTER, TextDecoration.PADDED);
	private static final StringViewer LEFT_JUSTIFIED_STRING_VIEWER = StringViewer.get(Alignment.TOP_LEFT, TextDecoration.PADDED);

	private static final int SHIFT = 5;
	
	@Override
	protected Shape getShape(Edge pEdge)
	{
		Point[] points = getPoints(pEdge);
		Path path = new Path();
		Point point = points[points.length - 1];
		MoveTo moveTo = new MoveTo(point.getX(), point.getY());
		path.getElements().add(moveTo);
		for(int i = points.length - 2; i >= 0; i--)
		{
			point = points[i];
			LineTo lineTo = new LineTo(point.getX(), point.getY());
			path.getElements().add(lineTo);
		}
		return path;
	}
	
	@Override
	public Line getConnectionPoints(Edge pEdge)
	{
		Point[] points = getPoints(pEdge);
		assert points.length >= 2;
		return new Line(points[0], points[points.length-1]);
	}
	
	private static ArrowHeadViewer getArrowHeadView(CallEdge pEdge)
	{
		if(pEdge.isSignal())
		{
			return ArrowHead.HALF_V.view();
		}
		else
		{
			return ArrowHead.V.view();
		}
	}
	
	@Override
	public Rectangle getBounds(Edge pEdge)
	{
		Rectangle bounds = super.getBounds(pEdge);
		Line connectionPoints = getConnectionPoints(pEdge);
		bounds = bounds.add(Conversions.toRectangle(getArrowHeadView((CallEdge)pEdge).getPath(connectionPoints.getPoint1(), 
					connectionPoints.getPoint2()).getBoundsInLocal()));
		final String label = ((CallEdge)pEdge).getMiddleLabel();
		if( label.length() > 0 )
		{
			bounds = bounds.add(getStringBounds((CallEdge)pEdge));
		}
		return bounds;
	}

	@Override
	public void draw(Edge pEdge, GraphicsContext pGraphics)
	{
		ToolGraphics.strokeSharpPath(pGraphics, (Path) getShape(pEdge), LineStyle.SOLID);
		
		Point[] points = getPoints(pEdge); // TODO already called by getShape(), find a way to avoid having to do 2 calls.
		getArrowHeadView((CallEdge)pEdge).draw(pGraphics, points[points.length - 2], points[points.length - 1]);
		String label = ((CallEdge)pEdge).getMiddleLabel();
		if( label.length() > 0 )
		{
			drawLabel((CallEdge)pEdge, pGraphics, label);
		}
	}
	
	private Rectangle getStringBounds(CallEdge pEdge)
	{
		assert pEdge != null;
		final String label = pEdge.getMiddleLabel();
		if( pEdge.isSelfEdge() )
		{
			Dimension dimensions = LEFT_JUSTIFIED_STRING_VIEWER.getDimension(label);
			Point[] points = getPoints(pEdge);
			int heightDelta = (points[2].getY() -  points[1].getY() - dimensions.height())/2 + SHIFT;
			return new Rectangle(points[1].getX(), points[1].getY() + heightDelta, dimensions.width() , dimensions.height());
		}
		else
		{
			Dimension dimensions = CENTERED_STRING_VIEWER.getDimension(label);
			Point center = getConnectionPoints(pEdge).spanning().getCenter();
			return new Rectangle(center.getX() - dimensions.width()/2, 
					center.getY() - dimensions.height() + SHIFT, dimensions.width(), dimensions.height());
		}
	}

	private void drawLabel(CallEdge pEdge, GraphicsContext pGraphics, String pLabel)
	{
		if( pEdge.isSelfEdge() )
		{
			LEFT_JUSTIFIED_STRING_VIEWER.draw(pLabel, pGraphics, getStringBounds(pEdge));
		}
		else
		{
			CENTERED_STRING_VIEWER.draw(pLabel, pGraphics, getStringBounds(pEdge));
		}
	}
	
	/* Gets the points on a segmented path */ 
	private static Point[] getPoints(Edge pEdge)
	{
		ArrayList<Point> points = new ArrayList<>();
		Node endNode = pEdge.getEnd();
		if( pEdge.getClass() == ConstructorEdge.class )
		{
			endNode = pEdge.getEnd().getParent();
		}
		Rectangle start = NodeViewerRegistry.getBounds(pEdge.getStart());	
		Rectangle end = NodeViewerRegistry.getBounds(endNode);
		if( ((CallEdge)pEdge).isSelfEdge() )
		{
			Point p = new Point(start.getMaxX(), end.getY() - CallNode.CALL_YGAP / 2);
			Point q = new Point(end.getMaxX(), end.getY());
			Point s = new Point(q.getX() + end.getWidth(), q.getY());
			Point r = new Point(s.getX(), p.getY());

			points.add(p);
			points.add(r);
			points.add(s);
			points.add(q);
		}
		else     
		{
			Direction direction = Direction.WEST;
			if( start.getX() > end.getX() )
			{
				direction = Direction.EAST;
			}
			Point endPoint = NodeViewerRegistry.getConnectionPoints(endNode, direction);
         
			if(start.getCenter().getX() < endPoint.getX())
			{
				points.add(new Point(start.getMaxX(), endPoint.getY()));
			}
			else
			{
				points.add(new Point(start.getX(), endPoint.getY()));
			}
			points.add(endPoint);
		}
		return points.toArray(new Point[points.size()]);
	}
	
	@Override
	public Canvas createIcon(Edge pEdge)
	{
		final float scale = 0.6f;
		final int offset = 15;
		Canvas canvas = new Canvas(BUTTON_SIZE, BUTTON_SIZE);
		GraphicsContext graphics = canvas.getGraphicsContext2D();
		canvas.getGraphicsContext2D().scale(scale, scale);
		Path path = new Path();
		path.getElements().addAll(new MoveTo(1, offset), new LineTo(BUTTON_SIZE*(1/scale)-1, offset));
		ToolGraphics.strokeSharpPath(graphics, path, LineStyle.SOLID);
		ArrowHead.V.view().draw(graphics, new Point(1, offset), new Point((int)(BUTTON_SIZE*(1/scale)-1), offset));
		return canvas;
	}
}
