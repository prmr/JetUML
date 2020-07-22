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
import ca.mcgill.cs.jetuml.diagram.edges.StateTransitionEdge;
import ca.mcgill.cs.jetuml.geom.Conversions;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.nodes.NodeViewerRegistry;
import ca.mcgill.cs.jetuml.views.ArrowHead;
import ca.mcgill.cs.jetuml.views.LineStyle;
import ca.mcgill.cs.jetuml.views.ToolGraphics;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

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
	
	private static final int RADIANS_TO_PIXELS = 10;
	private static final double HEIGHT_RATIO = 3.5;
	private static final int MAX_LENGTH_FOR_NORMAL_FONT = 15;
	private static final int MIN_FONT_SIZE = 9;
	
	// The amount of vertical difference in connection points to tolerate
	// before centering the edge label on one side instead of in the center.
	private static final int VERTICAL_TOLERANCE = 20; 

	private Font aFont = FONT;
	
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
		adjustLabelFont(pEdge);
		Rectangle2D labelBounds = getLabelBounds(pEdge);
		double x = labelBounds.getMinX();
		double y = labelBounds.getMinY();
		
		Paint oldFill = pGraphics.getFill();
		Font oldFont = pGraphics.getFont();
		pGraphics.translate(x, y);
		pGraphics.setFill(Color.BLACK);
		pGraphics.setFont(aFont);
		pGraphics.setTextAlign(TextAlignment.CENTER);
		pGraphics.fillText(pEdge.getMiddleLabel(), labelBounds.getWidth()/2, 0);
		pGraphics.setFill(oldFill);
		pGraphics.setFont(oldFont);
		pGraphics.translate(-x, -y);        
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
	public Dimension getLabelBounds(String pString)
	{
		if(pString == null || pString.length() == 0) 
		{
			return new Dimension(0, 0);
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

		adjustLabelFont(pEdge);
		Dimension textDimensions = getLabelBounds(pEdge.getMiddleLabel());

		int gap = 3;
		if( line.getY1() >= line.getY2() - VERTICAL_TOLERANCE && 
				line.getY1() <= line.getY2() + VERTICAL_TOLERANCE ) 
		{
			// The label is centered if the edge is (mostly) horizontal
			x -= textDimensions.getWidth() / 2;
		}
		else if( line.getY1() <= line.getY2() )
		{
			x += gap;
		}
		else
		{
			x -= textDimensions.getWidth() + gap;
		}
		
		if( line.getX1() <= line.getX2() )
		{
			y -= textDimensions.getHeight() + gap;
		}
		else
		{
			y += gap;
		}
		
		// Additional gap to make sure the labels don't overlap
		if( pEdge.getDiagram() != null && getPosition(pEdge) > 1 )
		{
			double delta = Math.abs(Math.atan2(line.getX2()-line.getX1(), line.getY2()-line.getY1()));
			delta = textDimensions.getHeight() - delta*RADIANS_TO_PIXELS;
			if( line.getX1() <= line.getX2() )
			{
				y -= delta;
			}
			else
			{
				y += delta;
			}
		}
		return new Rectangle2D(x, y, textDimensions.getWidth(), textDimensions.getHeight());
}   
	
	/*
	 * Positions the label above the self edge, centered
	 * in the middle of it.
	 * @return the bounds of the label text
	 */
	private Rectangle2D getSelfEdgeLabelBounds(StateTransitionEdge pEdge)
	{
		Line line = getConnectionPoints(pEdge);
		adjustLabelFont(pEdge);
		Dimension textDimensions = getLabelBounds(pEdge.getMiddleLabel());
		if( getPosition(pEdge) == 1 )
		{
			return new Rectangle2D(line.getX1() + SELF_EDGE_OFFSET - textDimensions.getWidth()/2,	
					line.getY1() - SELF_EDGE_OFFSET*2, textDimensions.getWidth(), textDimensions.getHeight());
		}
		else
		{
			return new Rectangle2D(line.getX1() - textDimensions.getWidth()/2,	
					line.getY1() - SELF_EDGE_OFFSET * HEIGHT_RATIO, textDimensions.getWidth(), textDimensions.getHeight());
		}
	}   
	
	private void adjustLabelFont(StateTransitionEdge pEdge)
	{
		if(pEdge.getMiddleLabel().length() > MAX_LENGTH_FOR_NORMAL_FONT)
		{
			float difference = pEdge.getMiddleLabel().length() - MAX_LENGTH_FOR_NORMAL_FONT;
			difference = difference / (2*pEdge.getMiddleLabel().length()); // damping
			double newFontSize = Math.max(MIN_FONT_SIZE, (1-difference) * FONT.getSize());
			aFont = new Font(aFont.getName(), newFontSize);
		}
		else
		{
			aFont = FONT;
		}
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
	
	private boolean isSelfEdge(Edge pEdge)
	{
		return pEdge.getStart() == pEdge.getEnd();
	}
	
	private Shape getSelfEdgeShape(Edge pEdge)
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
	private int getPosition(Edge pEdge)
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
	private Line getSelfEdgeConnectionPoints(Edge pEdge)
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
	private Line getNormalEdgeConnectionsPoints(Edge pEdge)
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
		Direction d1 = new Direction(startCenter, endCenter).turn(-turn);
		Direction d2 = new Direction(endCenter, startCenter).turn(turn);
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
