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

import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.edges.StateTransitionEdge;
import ca.mcgill.cs.jetuml.geom.Conversions;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.ArrowHead;
import ca.mcgill.cs.jetuml.viewers.LineStyle;
import ca.mcgill.cs.jetuml.viewers.StringViewer;
import ca.mcgill.cs.jetuml.viewers.ToolGraphics;
import ca.mcgill.cs.jetuml.viewers.StringViewer.Alignment;
import ca.mcgill.cs.jetuml.viewers.nodes.NodeViewerRegistry;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Shape;

/**
 * An edge view specialized for state transitions.
 */
public final class StateTransitionEdgeViewer extends AbstractEdgeViewer
{
	private static final int SELF_EDGE_OFFSET = 15;
	private static final int DEGREES_5 = 5;
	private static final int DEGREES_10 = 10;
	private static final int DEGREES_20 = 20;
	private static final int DEGREES_270 = 270;
	private static final double LINE_WIDTH = 0.6;
	
	private static final int RADIANS_TO_PIXELS = 7;
	private static final StringViewer STRING_VIEWER = StringViewer.get(Alignment.CENTER_CENTER);
	
	// The amount of vertical difference in connection points to tolerate
	// before centering the edge label on one side instead of in the center.
	private static final int VERTICAL_TOLERANCE = 20; 
	
	@Override
	public void draw(Edge pEdge, GraphicsContext pGraphics)
	{
		if(isSelfEdge(pEdge))
		{
			pGraphics.setStroke(Color.BLACK);
			drawSelfEdge(pEdge, pGraphics);
		}
		else 
		{
			ToolGraphics.strokeSharpPath(pGraphics, (Path) getShape(pEdge), LineStyle.SOLID);
		}
		drawLabel((StateTransitionEdge)pEdge, pGraphics);
		drawArrowHead(pEdge, pGraphics);
	}
	
	private void drawArrowHead(Edge pEdge, GraphicsContext pGraphics)
	{
		if( isSelfEdge(pEdge) )
		{
			Point connectionPoint2 = getSelfEdgeConnectionPoints(pEdge).getPoint2();
			if( getPosition(pEdge) == 1 )
			{
				ArrowHead.V.view().draw(pGraphics, new Point(connectionPoint2.getX()+SELF_EDGE_OFFSET, 
						connectionPoint2.getY()-SELF_EDGE_OFFSET/4), getConnectionPoints(pEdge).getPoint2());
			}
			else
			{
				ArrowHead.V.view().draw(pGraphics, new Point(connectionPoint2.getX()-SELF_EDGE_OFFSET/4, 
						connectionPoint2.getY()-SELF_EDGE_OFFSET), getConnectionPoints(pEdge).getPoint2());
			}
		}
		else
		{
			ArrowHead.V.view().draw(pGraphics, Conversions.toPoint(getControlPoint(pEdge)), getConnectionPoints(pEdge).getPoint2());
		}
	}
	
	/*
	 *  Draws the label.
	 *  @param pGraphics2D the graphics context
	 */
	private void drawLabel(StateTransitionEdge pEdge, GraphicsContext pGraphics)
	{
		String label = wrapLabel(pEdge);
		Rectangle2D labelBounds = getLabelBounds(pEdge);
		Rectangle drawingRectangle = new Rectangle((int) Math.round(labelBounds.getMinX()), (int) Math.round(labelBounds.getMinY()), 
				(int) Math.round(labelBounds.getWidth()), (int) Math.round(labelBounds.getHeight()));

		STRING_VIEWER.draw(label, pGraphics, drawingRectangle);
	}
	
	private void drawSelfEdge(Edge pEdge, GraphicsContext pGraphics)
	{
		Arc arc = (Arc) getShape(pEdge);
		double width = pGraphics.getLineWidth();
		pGraphics.setLineWidth(LINE_WIDTH);
		pGraphics.strokeArc(arc.getCenterX(), arc.getCenterY(), arc.getRadiusX(), arc.getRadiusY(), arc.getStartAngle(), 
				arc.getLength(), arc.getType());
		pGraphics.setLineWidth(width);
	}
	
	private Rectangle2D getLabelBounds(StateTransitionEdge pEdge)
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
	private Rectangle2D getNormalEdgeLabelBounds(StateTransitionEdge pEdge)
	{
		Line line = getConnectionPoints(pEdge);
		Point2D control = getControlPoint(pEdge);
		double x = control.getX() / 2 + line.getX1() / 4 + line.getX2() / 4;
		double y = control.getY() / 2 + line.getY1() / 4 + line.getY2() / 4;

		String label = wrapLabel(pEdge);
		Dimension textDimensions = getLabelBounds(label);

		int gap = 3;
		if( line.getY1() >= line.getY2() - VERTICAL_TOLERANCE && 
				line.getY1() <= line.getY2() + VERTICAL_TOLERANCE ) 
		{
			// The label is centered if the edge is (mostly) horizontal
			x -= textDimensions.width() / 2;
		}
		else if( line.getY1() <= line.getY2() )
		{
			x += gap;
		}
		else
		{
			x -= textDimensions.width() + gap;
		}
		
		if( line.getX1() <= line.getX2() )
		{
			y -= textDimensions.height() + gap;
		}
		else
		{
			y += gap;
		}
		
		// Additional gap to make sure the labels don't overlap
		if( pEdge.getDiagram() != null && getPosition(pEdge) > 1 )
		{
			final double angleLowerBound = Math.atan2(1, 1);
			final double angleUpperBound = 3 * angleLowerBound;
			double angle = Math.abs(Math.atan2(line.getX2()-line.getX1(), line.getY2()-line.getY1()));
			double delta = textDimensions.height();
			if ( angleLowerBound <= angle && angle <= angleUpperBound )
			{
				delta -= angle*RADIANS_TO_PIXELS;
			}
			if( line.getX1() <= line.getX2() )
			{
				y -= delta;
			}
			else
			{
				y += delta;
			}
		}
		return new Rectangle2D(x, y, textDimensions.width(), textDimensions.height());
}   
	
	/*
	 * Positions the label above the self edge, centered
	 * in the middle of it.
	 * @return the bounds of the label text
	 */
	private Rectangle2D getSelfEdgeLabelBounds(StateTransitionEdge pEdge)
	{
		Line line = getConnectionPoints(pEdge);
		String label = wrapLabel(pEdge);
		Dimension textDimensions = getLabelBounds(label);
		if( getPosition(pEdge) == 1 )
		{
            return new Rectangle2D(line.getX1() + SELF_EDGE_OFFSET - textDimensions.width()/2,  
                    line.getY1() - SELF_EDGE_OFFSET - textDimensions.height(), textDimensions.width(), textDimensions.height());
        }
        else
        {
            return new Rectangle2D(line.getX1() - textDimensions.width()/2, 
                    line.getY1() - 2*SELF_EDGE_OFFSET - textDimensions.height(), textDimensions.width(), 
                    textDimensions.height());
        }
	}  

	/**
	 * Wraps the edge label.
	 */
	private String wrapLabel(StateTransitionEdge pEdge)
	{
		int distanceInX = Math.abs(NodeViewerRegistry.getBounds(pEdge.getStart()).getCenter().getX() -
				NodeViewerRegistry.getBounds(pEdge.getEnd()).getCenter().getX());
		int distanceInY = Math.abs(NodeViewerRegistry.getBounds(pEdge.getStart()).getCenter().getY() -
				NodeViewerRegistry.getBounds(pEdge.getEnd()).getCenter().getY());
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
		return pEdge.getStart() == pEdge.getEnd();
	}
	
	private static Shape getSelfEdgeShape(Edge pEdge)
	{
		Line line = getSelfEdgeConnectionPoints(pEdge);
		Arc arc = new Arc();
		arc.setRadiusX(SELF_EDGE_OFFSET*2);
		arc.setRadiusY(SELF_EDGE_OFFSET*2);
		arc.setLength(DEGREES_270);
		arc.setType(ArcType.OPEN);
		if( getPosition(pEdge) == 1 )
		{
			arc.setCenterX(line.getX1());
			arc.setCenterY(line.getY1()-SELF_EDGE_OFFSET);
			arc.setStartAngle(DEGREES_270);
		}
		else
		{		
			arc.setCenterX(line.getX1()-SELF_EDGE_OFFSET);
			arc.setCenterY(line.getY1()-SELF_EDGE_OFFSET*2);
			arc.setStartAngle(1);
		}
		return arc;
	}
	
	@Override
	public boolean contains(Edge pEdge, Point pPoint)
	{
		boolean result = super.contains(pEdge, pPoint);
		if (getShape(pEdge) instanceof Arc)
		{
			Arc arc = (Arc) getShape(pEdge);
			arc.setRadiusX(arc.getRadiusX() + 2 * MAX_DISTANCE);
			arc.setRadiusY(arc.getRadiusY() + 2 * MAX_DISTANCE);
			result = arc.contains(pPoint.getX(), pPoint.getY());
		}
		return result;
	}
	
	/** 
	 * @return An index that represents the position in the list of
	 *     edges between the same start and end nodes. 
	 * @pre getGraph() != null
	 */
	private static int getPosition(Edge pEdge)
	{
		assert pEdge.getDiagram() != null;
		int lReturn = 0;
		for( Edge edge : pEdge.getDiagram().edgesConnectedTo(pEdge.getStart()))
		{
			if( edge.getStart() == pEdge.getStart() && edge.getEnd() == pEdge.getEnd())
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
	private static Line getSelfEdgeConnectionPoints(Edge pEdge)
	{
		if( getPosition(pEdge) == 1 )
		{
			Point2D point1 = new Point2D(NodeViewerRegistry.getBounds(pEdge.getStart()).getMaxX() - SELF_EDGE_OFFSET, 
					NodeViewerRegistry.getBounds(pEdge.getStart()).getY());
			Point2D point2 = new Point2D(NodeViewerRegistry.getBounds(pEdge.getStart()).getMaxX(), 
					NodeViewerRegistry.getBounds(pEdge.getStart()).getY() + SELF_EDGE_OFFSET);
			return new Line(Conversions.toPoint(point1), Conversions.toPoint(point2));
		}
		else
		{
			Point2D point1 = new Point2D(NodeViewerRegistry.getBounds(pEdge.getStart()).getX(), 
					NodeViewerRegistry.getBounds(pEdge.getStart()).getY() + SELF_EDGE_OFFSET);
			Point2D point2 = new Point2D(NodeViewerRegistry.getBounds(pEdge.getStart()).getX() + SELF_EDGE_OFFSET, 
					NodeViewerRegistry.getBounds(pEdge.getStart()).getY());
			return new Line(Conversions.toPoint(point1), Conversions.toPoint(point2));
		}
	}
	
	private Shape getNormalEdgeShape(Edge pEdge)
	{
		Line line = getConnectionPoints(pEdge);
		Path path = new Path();
		MoveTo moveTo = new MoveTo(line.getPoint1().getX(), line.getPoint1().getY());
		QuadCurveTo curveTo = new QuadCurveTo(getControlPoint(pEdge).getX(), getControlPoint(pEdge).getY(), 
				line.getPoint2().getX(), line.getPoint2().getY());
		path.getElements().addAll(moveTo, curveTo);
		return path;
	}
	
	
	/**
     *  Gets the control point for the quadratic spline.
     * @return the control point
     */
	private Point2D getControlPoint(Edge pEdge)
	{
		Line line = getConnectionPoints(pEdge);
		double tangent = Math.tan(Math.toRadians(DEGREES_10));
		if( getPosition(pEdge) > 1 )
		{
			tangent = Math.tan(Math.toRadians(DEGREES_20));
		}
		double dx = (line.getX2() - line.getX1()) / 2;
		double dy = (line.getY2() - line.getY1()) / 2;
		return new Point2D((line.getX1() + line.getX2()) / 2 + tangent * dy, (line.getY1() + line.getY2()) / 2 - tangent * dx);         
	}
	
	@Override
	public Rectangle getBounds(Edge pEdge)
	{
		return super.getBounds(pEdge).add(Conversions.toRectangle(getLabelBounds((StateTransitionEdge)pEdge)));
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
	private static Line getNormalEdgeConnectionsPoints(Edge pEdge)
	{
		Rectangle start = NodeViewerRegistry.getBounds(pEdge.getStart());
		Rectangle end = NodeViewerRegistry.getBounds(pEdge.getEnd());
		Point startCenter = start.getCenter();
		Point endCenter = end.getCenter();
		int turn = DEGREES_5;
		if( pEdge.getDiagram() != null && getPosition(pEdge) > 1 )
		{
			turn = DEGREES_20;
		}
		Direction d1 = Direction.fromLine(startCenter, endCenter).rotatedBy(-turn);
		Direction d2 = Direction.fromLine(endCenter, startCenter).rotatedBy(turn);
		return new Line(NodeViewerRegistry.getConnectionPoints(pEdge.getStart(), d1), 
				NodeViewerRegistry.getConnectionPoints(pEdge.getEnd(), d2));
	}
	
	@Override
	public Canvas createIcon(Edge pEdge)
	{   //CSOFF: Magic numbers
		Canvas canvas = new Canvas(BUTTON_SIZE, BUTTON_SIZE);
		GraphicsContext graphics = canvas.getGraphicsContext2D();
		graphics.scale(0.6, 0.6);
		Line line = new Line(new Point(2,2), new Point(40,40));
		final double tangent = Math.tan(Math.toRadians(DEGREES_10));
		double dx = (line.getX2() - line.getX1()) / 2;
		double dy = (line.getY2() - line.getY1()) / 2;
		Point control = new Point((int)((line.getX1() + line.getX2()) / 2 + tangent * dy), 
				(int)((line.getY1() + line.getY2()) / 2 - tangent * dx));         
		
		Path path = new Path();
		MoveTo moveTo = new MoveTo(line.getPoint1().getX(), line.getPoint1().getY());
		QuadCurveTo curveTo = new QuadCurveTo(control.getX(), control.getY(), line.getPoint2().getX(), line.getPoint2().getY());
		path.getElements().addAll(moveTo, curveTo);
		
		ToolGraphics.strokeSharpPath(graphics, path, LineStyle.SOLID);
		ArrowHead.V.view().draw(graphics, control, new Point(40, 40));
		return canvas;
	}
}
