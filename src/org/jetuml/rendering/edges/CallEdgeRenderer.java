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

import java.util.ArrayList;

import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.ConstructorEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.Direction;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.geom.TextPosition;
import org.jetuml.gui.ColorScheme;
import org.jetuml.rendering.ArrowHead;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.FontMetrics;
import org.jetuml.rendering.GraphicsRenderingContext;
import org.jetuml.rendering.LineStyle;
import org.jetuml.rendering.RenderingContext;
import org.jetuml.rendering.StringRenderer;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

/**
 * A renderer to show call edges in a sequence diagrams. These are labeled
 * edges that are either straight or self-edges, in a solid line, and 
 * have either a V or half-V arrow head.
 */
public final class CallEdgeRenderer extends AbstractEdgeRenderer
{	
	private static final StringRenderer CENTERED_STRING_VIEWER = new StringRenderer(TextPosition.CENTER_CENTER);
	private static final StringRenderer LEFT_CENTER_STRING_RENDERER = new StringRenderer(TextPosition.CENTER_LEFT);

	private static final int LEFT_MARGIN = 5;
	
	/**
	 * @param pParent The renderer for the parent diagram.
	 */
	public CallEdgeRenderer(DiagramRenderer pParent)
	{
		super(pParent);
	}
	
	@Override
	protected Shape getShape(Edge pEdge)
	{
		Point[] points = getPoints(pEdge);
		Path path = new Path();
		Point point = points[points.length - 1];
		MoveTo moveTo = new MoveTo(point.x(), point.y());
		path.getElements().add(moveTo);
		for(int i = points.length - 2; i >= 0; i--)
		{
			point = points[i];
			LineTo lineTo = new LineTo(point.x(), point.y());
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
	
	private static ArrowHead getArrowHead(CallEdge pEdge)
	{
		if(pEdge.isSignal())
		{
			return ArrowHead.HALF_V;
		}
		else
		{
			return ArrowHead.V;
		}
	}
	
	@Override
	public Rectangle getBounds(DiagramElement pElement)
	{
		Rectangle bounds = super.getBounds(pElement);
		Edge edge = (Edge) pElement;
		
		bounds = bounds.add(ArrowHeadRenderer.getBounds(getArrowHead((CallEdge)edge), getConnectionPoints(edge)));
		final String label = ((CallEdge)edge).getMiddleLabel();
		if( label.length() > 0 )
		{
			bounds = bounds.add(getStringBounds((CallEdge)edge));
		}
		return bounds;
	}

	@Override
	public void draw(DiagramElement pElement, RenderingContext pContext)
	{
		Edge edge = (Edge) pElement;
		pContext.strokePath((Path) getShape(edge), ColorScheme.get().stroke(), LineStyle.SOLID);
		
		Point[] points = getPoints(edge); // TODO already called by getShape(), find a way to avoid having to do 2 calls.
		ArrowHeadRenderer.draw(pContext, getArrowHead((CallEdge)edge), points[points.length - 2], points[points.length - 1]);
		drawLabel((CallEdge)edge, pContext);
	}
	
	/*
	 * The label for the self edge is centered on the middle of the "knee" in the self edge,
	 * with a LEFT_MARGIN space to the left.
	 */
	private Rectangle getSelfEdgeLabelBox(CallEdge pEdge)
	{
		Dimension dimensions = LEFT_CENTER_STRING_RENDERER.getDimensionNoPadding(pEdge.getMiddleLabel());
		Point[] points = getPoints(pEdge);
		int x = points[1].x() + LEFT_MARGIN; // The extent of the self edge plus a margin
		int y = (points[1].y() + points[2].y())/2 -dimensions.height() / 2; // Align box with center of edge
		return new Rectangle(x, y, dimensions.width(), dimensions.height());		
	}
	
	/*
	 * The label for the normal edge is centered horizontally along the call edge
	 * and placed a bit above so the descendants don't cross the edge.
	 */
	private Rectangle getNormalEdgeLabelBox(CallEdge pEdge)
	{
		Rectangle spanning = getConnectionPoints(pEdge).spanning();
		int lineHeight = FontMetrics.getHeight(pEdge.getMiddleLabel());
		return new Rectangle(spanning.x(), spanning.y() - lineHeight, spanning.width(), lineHeight);

	}
	
	private Rectangle getStringBounds(CallEdge pEdge)
	{
		assert pEdge != null;
		if( pEdge.isSelfEdge() )
		{
			return getSelfEdgeLabelBox(pEdge);
		}
		else
		{
			return getNormalEdgeLabelBox(pEdge);
		}
	}

	private void drawLabel(CallEdge pEdge, RenderingContext pContext)
	{
		if( pEdge.isSelfEdge() )
		{
			LEFT_CENTER_STRING_RENDERER.draw(pEdge.getMiddleLabel(), getSelfEdgeLabelBox(pEdge), pContext);
		}
		else
		{
			CENTERED_STRING_VIEWER.draw(pEdge.getMiddleLabel(), getNormalEdgeLabelBox(pEdge), pContext);
		}
	}
	
	/* Gets the points on a segmented path */ 
	private Point[] getPoints(Edge pEdge)
	{
		ArrayList<Point> points = new ArrayList<>();
		Node endNode = pEdge.end();
		if( pEdge.getClass() == ConstructorEdge.class )
		{
			endNode = pEdge.end().getParent();
		}
		Rectangle start = parent().getBounds(pEdge.start());	
		Rectangle end = parent().getBounds(endNode);
		if( ((CallEdge)pEdge).isSelfEdge() )
		{
			Point p = new Point(start.maxX(), end.y() - CallNode.CALL_YGAP / 2);
			Point q = new Point(end.maxX(), end.y());
			Point s = new Point(q.x() + end.width(), q.y());
			Point r = new Point(s.x(), p.y());

			points.add(p);
			points.add(r);
			points.add(s);
			points.add(q);
		}
		else     
		{
			Direction direction = Direction.WEST;
			if( start.x() > end.x() )
			{
				direction = Direction.EAST;
			}
			Point endPoint = parent().getConnectionPoints(endNode, direction);
         
			if(start.center().x() < endPoint.x())
			{
				points.add(new Point(start.maxX(), endPoint.y()));
			}
			else
			{
				points.add(new Point(start.x(), endPoint.y()));
			}
			points.add(endPoint);
		}
		return points.toArray(new Point[points.size()]);
	}
	
	@Override
	public Canvas createIcon(DiagramType pDiagramType, DiagramElement pElement)
	{
		final float scale = 0.6f;
		final int offset = 15;
		Canvas canvas = new Canvas(BUTTON_SIZE, BUTTON_SIZE);
		GraphicsContext graphics = canvas.getGraphicsContext2D();
		canvas.getGraphicsContext2D().scale(scale, scale);
		Path path = new Path();
		path.getElements().addAll(new MoveTo(1, offset), new LineTo(BUTTON_SIZE*(1/scale)-1, offset));
		GraphicsRenderingContext context = new GraphicsRenderingContext(graphics);
		context.strokePath(path, ColorScheme.get().stroke(), LineStyle.SOLID);
		ArrowHeadRenderer.draw(context, ArrowHead.V, new Point(1, offset), new Point((int)(BUTTON_SIZE*(1/scale)-1), offset));
		return canvas;
	}
}
