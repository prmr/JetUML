/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2025 by McGill University.
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
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.edges.StateTransitionEdge;
import org.jetuml.geom.Alignment;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.Direction;
import org.jetuml.geom.GeomUtils;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.gui.ColorScheme;
import org.jetuml.rendering.ArrowHead;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.LineStyle;
import org.jetuml.rendering.RenderingContext;
import org.jetuml.rendering.GraphicsRenderingContext;
import org.jetuml.rendering.StringRenderer;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Shape;

/**
 * An edge view specialized for state transitions.
 */
public final class StateTransitionEdgeRenderer extends AbstractEdgeRenderer
{
	private static final int SELF_EDGE_OFFSET = 15;
	private static final int DEGREES_5 = 5;
	private static final int DEGREES_10 = 10;
	private static final int DEGREES_20 = 20;
	private static final int DEGREES_270 = 270;
	
	private static final int RADIANS_TO_PIXELS = 7;
	private static final StringRenderer STRING_VIEWER = new StringRenderer(Alignment.CENTER);
	
	// The amount of vertical difference in connection points to tolerate
	// before centering the edge label on one side instead of in the center.
	private static final int VERTICAL_TOLERANCE = 20; 
	
	/**
	 * @param pParent The renderer for the parent diagram.
	 */
	public StateTransitionEdgeRenderer(DiagramRenderer pParent)
	{
		super(pParent);
	}
	
	@Override
	public void draw(DiagramElement pElement, RenderingContext pContext)
	{
		Edge edge = (Edge) pElement;
		if (isSelfEdge(edge))
		{
			Arc arc = getSelfEdgeShape(edge);
			pContext.strokeArc((int) arc.getCenterX(), (int) arc.getCenterY(), 
					(int) arc.getRadiusX(), (int) arc.getStartAngle(), (int) arc.getLength(), 
					ColorScheme.get().stroke());
		}
		else 
		{
			pContext.strokePath(getNormalEdgeShape(edge), ColorScheme.get().stroke(), LineStyle.SOLID);
		}
		drawLabel((StateTransitionEdge)edge, pContext);
		drawArrowHead(edge, pContext);
	}
	
	private void drawArrowHead(Edge pEdge, RenderingContext pContext)
	{
		if( isSelfEdge(pEdge) )
		{
			Point connectionPoint2 = getSelfEdgeConnectionPoints(pEdge).point2();
			if( getPosition(pEdge) == 1 )
			{
				ArrowHeadRenderer.draw(pContext, ArrowHead.V, new Point(connectionPoint2.x()+SELF_EDGE_OFFSET, 
						connectionPoint2.y()-SELF_EDGE_OFFSET/4), getConnectionPoints(pEdge).point2());
			}
			else
			{
				ArrowHeadRenderer.draw(pContext, ArrowHead.V, new Point(connectionPoint2.x()-SELF_EDGE_OFFSET/4, 
						connectionPoint2.y()-SELF_EDGE_OFFSET), getConnectionPoints(pEdge).point2());
			}
		}
		else
		{
			ArrowHeadRenderer.draw(pContext, ArrowHead.V, 
					getControlPoint(pEdge), getConnectionPoints(pEdge).point2());
		}
	}
	
	/*
	 *  Draws the label.
	 *  @param pGraphics2D the graphics context
	 */
	private void drawLabel(StateTransitionEdge pEdge, RenderingContext pContext)
	{
		String label = wrapLabel(pEdge);
		Rectangle labelBounds = getLabelBounds(pEdge);
		STRING_VIEWER.draw(label, labelBounds, pContext);
	}
	
	private Rectangle getLabelBounds(StateTransitionEdge pEdge)
	{
		if( isSelfEdge(pEdge) )
		{
			return getSelfEdgeLabelBounds(pEdge);
		}
		else
		{
			return getNormalEdgeLabelBounds(pEdge);
		}
	}
	
	/**
     * Gets the dimensions for pString.
     * @param pString The input string. Can be null.
     * @return The dimensions of the string.
	 */
	private static Dimension getLabelBounds(String pString)
	{
		if(pString == null || pString.length() == 0) 
		{
			return Dimension.NULL;
		}
		return textDimensions(pString);
	}
	
	/*
	 * Gets the bounds of the label text.
	 * @return the bounds of the label text
	 */
	private Rectangle getNormalEdgeLabelBounds(StateTransitionEdge pEdge)
	{
		Line line = getConnectionPoints(pEdge);
		Point control = getControlPoint(pEdge);
		int x = control.x() / 2 + line.x1() / 4 + line.x2() / 4;
		int y = control.y() / 2 + line.y1() / 4 + line.y2() / 4;

		String label = wrapLabel(pEdge);
		Dimension textDimensions = getLabelBounds(label);

		int gap = 3;
		if( line.y1() >= line.y2() - VERTICAL_TOLERANCE && 
				line.y1() <= line.y2() + VERTICAL_TOLERANCE ) 
		{
			// The label is centered if the edge is (mostly) horizontal
			x -= textDimensions.width() / 2;
		}
		else if( line.y1() <= line.y2() )
		{
			x += gap;
		}
		else
		{
			x -= textDimensions.width() + gap;
		}
		
		if( line.x1() <= line.x2() )
		{
			y -= textDimensions.height() + gap;
		}
		else
		{
			y += gap;
		}
		
		// Additional gap to make sure the labels don't overlap
		if( getPosition(pEdge) > 1 )
		{
			final double angleLowerBound = Math.atan2(1, 1);
			final double angleUpperBound = 3 * angleLowerBound;
			double angle = Math.abs(Math.atan2(line.x2()-line.x1(), line.y2()-line.y1()));
			double delta = textDimensions.height();
			if( angleLowerBound <= angle && angle <= angleUpperBound )
			{
				delta -= angle*RADIANS_TO_PIXELS;
			}
			if( line.x1() <= line.x2() )
			{
				y -= delta;
			}
			else
			{
				y += delta;
			}
		}
		return new Rectangle(x, y, textDimensions.width(), textDimensions.height());
}   
	
	/*
	 * Positions the label above the self edge, centered
	 * in the middle of it.
	 * @return the bounds of the label text
	 */
	private Rectangle getSelfEdgeLabelBounds(StateTransitionEdge pEdge)
	{
		Line line = getConnectionPoints(pEdge);
		String label = wrapLabel(pEdge);
		Dimension textDimensions = getLabelBounds(label);
		if( getPosition(pEdge) == 1 )
		{
            return new Rectangle(line.x1() + SELF_EDGE_OFFSET - textDimensions.width()/2,  
                    line.y1() - SELF_EDGE_OFFSET - textDimensions.height(), textDimensions.width(), textDimensions.height());
        }
        else
        {
            return new Rectangle(line.x1() - textDimensions.width()/2, 
                    line.y1() - 2*SELF_EDGE_OFFSET - textDimensions.height(), textDimensions.width(), 
                    textDimensions.height());
        }
	}  

	/**
	 * Wraps the edge label.
	 */
	private String wrapLabel(StateTransitionEdge pEdge)
	{
		int distanceInX = Math.abs(parent().getBounds(pEdge.start()).center().x() -
				parent().getBounds(pEdge.end()).center().x());
		int distanceInY = Math.abs(parent().getBounds(pEdge.start()).center().y() -
				parent().getBounds(pEdge.end()).center().y());
		return super.wrapLabel(pEdge.getMiddleLabel(), distanceInX, distanceInY);	
	}

	@Override
	protected Shape getShape(Edge pEdge)
	{
		if( isSelfEdge(pEdge) )
		{
			return getSelfEdgeShape(pEdge);
		}
		else
		{
			return getNormalEdgeShape(pEdge);
		}
	}
	
	private static boolean isSelfEdge(Edge pEdge)
	{
		return pEdge.start() == pEdge.end();
	}
	
	private Arc getSelfEdgeShape(Edge pEdge)
	{
		/*
		 * The starting position is determined from the bottom middle
		 * (0 degrees) and going counter-clockwise. The extent runs
		 * clockwise.
		 */
		Line line = getSelfEdgeConnectionPoints(pEdge);
		Arc arc = new Arc();
		arc.setRadiusX(SELF_EDGE_OFFSET);
		arc.setRadiusY(SELF_EDGE_OFFSET);
		arc.setLength(DEGREES_270);
		arc.setType(ArcType.OPEN);
		if( getPosition(pEdge) == 1 )
		{
			arc.setCenterX(line.x1() + SELF_EDGE_OFFSET);
			arc.setCenterY(line.y1());
			arc.setStartAngle(DEGREES_270);
		}
		else
		{		
			arc.setCenterX(line.x1());
			arc.setCenterY(line.y1()-SELF_EDGE_OFFSET);
			arc.setStartAngle(0);
		}
		return arc;
	}
	
	@Override
	public boolean contains(DiagramElement pElement, Point pPoint)
	{
		boolean result = super.contains(pElement, pPoint);
		if(getShape((Edge)pElement) instanceof Arc arc)
		{
			arc.setRadiusX(arc.getRadiusX() + 2 * MAX_DISTANCE);
			arc.setRadiusY(arc.getRadiusY() + 2 * MAX_DISTANCE);
			result = arc.contains(pPoint.x(), pPoint.y());
		}
		return result;
	}
	
	/** 
	 * @return An index that represents the position in the list of
	 *     edges between the same start and end nodes. 
	 * @pre getGraph() != null
	 */
	private int getPosition(Edge pEdge)
	{
		int lReturn = 0;
		for( Edge edge : parent().diagram().edgesConnectedTo(pEdge.start()))
		{
			if( edge.start() == pEdge.start() && edge.end() == pEdge.end())
			{
				lReturn++;
			}
			if( edge == pEdge )
			{
				return lReturn;
			}
		}
		assert lReturn > 0;
		return lReturn;
	}
	
	/*
	 * The connection points for the self-edge are an offset from the top-right
	 * corner.
	 */
	private Line getSelfEdgeConnectionPoints(Edge pEdge)
	{
		if( getPosition(pEdge) == 1 )
		{
			Point point1 = new Point(parent().getBounds(pEdge.start()).maxX() - SELF_EDGE_OFFSET, 
					parent().getBounds(pEdge.start()).y());
			Point point2 = new Point(parent().getBounds(pEdge.start()).maxX(), 
					parent().getBounds(pEdge.start()).y() + SELF_EDGE_OFFSET);
			return new Line(point1, point2);
		}
		else
		{
			Point point1 = new Point(parent().getBounds(pEdge.start()).x(), 
					parent().getBounds(pEdge.start()).y() + SELF_EDGE_OFFSET);
			Point point2 = new Point(parent().getBounds(pEdge.start()).x() + SELF_EDGE_OFFSET, 
					parent().getBounds(pEdge.start()).y());
			return new Line(point1, point2);
		}
	}
	
	private Path getNormalEdgeShape(Edge pEdge)
	{
		Line line = getConnectionPoints(pEdge);
		Path path = new Path();
		MoveTo moveTo = new MoveTo(line.point1().x(), line.point1().y());
		QuadCurveTo curveTo = new QuadCurveTo(getControlPoint(pEdge).x(), getControlPoint(pEdge).y(), 
				line.point2().x(), line.point2().y());
		path.getElements().addAll(moveTo, curveTo);
		return path;
	}
	
	
	/**
     * Gets the control point for the quadratic spline.
     * @return the control point
     */
	private Point getControlPoint(Edge pEdge)
	{
		Line line = getConnectionPoints(pEdge);
		double tangent = Math.tan(Math.toRadians(DEGREES_10));
		if( getPosition(pEdge) > 1 )
		{
			tangent = Math.tan(Math.toRadians(DEGREES_20));
		}
		double dx = (line.x2() - line.x1()) / 2;
		double dy = (line.y2() - line.y1()) / 2;
		return new Point(GeomUtils.round((line.x1() + line.x2()) / 2 + tangent * dy),
				GeomUtils.round((line.y1() + line.y2()) / 2 - tangent * dx)); 
	}
	
	@Override
	public Rectangle getBounds(DiagramElement pElement)
	{
		return super.getBounds(pElement).add(getLabelBounds((StateTransitionEdge)pElement));
	}
	
	@Override
	public Line getConnectionPoints(Edge pEdge)
	{
		if(isSelfEdge(pEdge))
		{
			return getSelfEdgeConnectionPoints(pEdge);
		}
		else
		{
			return getNormalEdgeConnectionsPoints(pEdge);
		}
	}
	
	/*
	 * The connection points are a slight offset from the center.
	 * @return
	 */
	private Line getNormalEdgeConnectionsPoints(Edge pEdge)
	{
		Rectangle start = parent().getBounds(pEdge.start());
		Rectangle end = parent().getBounds(pEdge.end());
		Point startCenter = start.center();
		Point endCenter = end.center();
		int turn = DEGREES_5;
		if( getPosition(pEdge) > 1 )
		{
			turn = DEGREES_20;
		}
		Direction d1 = Direction.fromLine(startCenter, endCenter).rotatedBy(-turn);
		Direction d2 = Direction.fromLine(endCenter, startCenter).rotatedBy(turn);
		return new Line(parent().getConnectionPoints(pEdge.start(), d1), 
				parent().getConnectionPoints(pEdge.end(), d2));
	}
	
	@Override
	public Canvas createIcon(DiagramType pDiagramType, DiagramElement pElement)
	{   //CSOFF: Magic numbers
		Canvas canvas = new Canvas(BUTTON_SIZE, BUTTON_SIZE);
		GraphicsContext graphics = canvas.getGraphicsContext2D();
		graphics.scale(0.6, 0.6);
		Line line = new Line(new Point(2,2), new Point(40,40));
		final double tangent = Math.tan(Math.toRadians(DEGREES_10));
		double dx = (line.x2() - line.x1()) / 2;
		double dy = (line.y2() - line.y1()) / 2;
		Point control = new Point((int)((line.x1() + line.x2()) / 2 + tangent * dy), 
				(int)((line.y1() + line.y2()) / 2 - tangent * dx));         
		
		Path path = new Path();
		MoveTo moveTo = new MoveTo(line.point1().x(), line.point1().y());
		QuadCurveTo curveTo = new QuadCurveTo(control.x(), control.y(), line.point2().x(), line.point2().y());
		path.getElements().addAll(moveTo, curveTo);
		
		GraphicsRenderingContext context = new GraphicsRenderingContext(graphics);
		context.strokePath(path, ColorScheme.get().stroke(), LineStyle.SOLID);
		ArrowHeadRenderer.draw(context, ArrowHead.V, control, new Point(40, 40));
		return canvas;
	}
}
