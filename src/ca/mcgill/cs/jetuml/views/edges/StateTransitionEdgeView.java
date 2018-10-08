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
import ca.mcgill.cs.jetuml.diagram.edges.StateTransitionEdge;
import ca.mcgill.cs.jetuml.geom.Conversions;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.ArrowHead;
import ca.mcgill.cs.jetuml.views.LineStyle;
import ca.mcgill.cs.jetuml.views.ToolGraphics;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
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
public final class StateTransitionEdgeView extends AbstractEdgeView
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
	private String aLabel;
	
	/**
	 * @param pEdge The edge to wrap.
	 */
	public StateTransitionEdgeView(StateTransitionEdge pEdge)
	{
		super(pEdge);
		aLabel = ((StateTransitionEdge) edge()).getMiddleLabel();
	}

	@Override
	public void draw(GraphicsContext pGraphics)
	{
		if(isSelfEdge())
		{
			pGraphics.setStroke(Color.BLACK);
			drawSelfEdge(pGraphics);
		}
		else 
		{
			ToolGraphics.strokeSharpPath(pGraphics, (Path) getShape(), LineStyle.SOLID);
		}
		drawLabel(pGraphics);
		drawArrowHead(pGraphics);
	}
	
	private void drawArrowHead(GraphicsContext pGraphics)
	{
		if( isSelfEdge() )
		{
			Point connectionPoint2 = getSelfEdgeConnectionPoints().getPoint2();
			if( getPosition() == 1 )
			{
				ArrowHead.V.view().draw(pGraphics, new Point2D(connectionPoint2.getX()+SELF_EDGE_OFFSET, 
						connectionPoint2.getY()-SELF_EDGE_OFFSET/4), Conversions.toPoint2D(getConnectionPoints().getPoint2()));
			}
			else
			{
				ArrowHead.V.view().draw(pGraphics, new Point2D(connectionPoint2.getX()-SELF_EDGE_OFFSET/4, 
						connectionPoint2.getY()-SELF_EDGE_OFFSET), Conversions.toPoint2D(getConnectionPoints().getPoint2()));
			}
		}
		else
		{
			ArrowHead.V.view().draw(pGraphics, getControlPoint(), Conversions.toPoint2D(getConnectionPoints().getPoint2()));
		}
	}
	
	/*
	 *  Draws the label.
	 *  @param pGraphics2D the graphics context
	 */
	private void drawLabel(GraphicsContext pGraphics)
	{
		aLabel = ((StateTransitionEdge) edge()).getMiddleLabel();
		adjustLabelFont();
		Rectangle2D labelBounds = getLabelBounds();
		double x = labelBounds.getMinX();
		double y = labelBounds.getMinY();
		
		Paint oldFill = pGraphics.getFill();
		Font oldFont = pGraphics.getFont();
		pGraphics.translate(x, y);
		pGraphics.setFill(Color.BLACK);
		pGraphics.setFont(aFont);
		pGraphics.setTextAlign(TextAlignment.CENTER);
		pGraphics.fillText(aLabel, labelBounds.getWidth()/2, 0);
		pGraphics.setFill(oldFill);
		pGraphics.setFont(oldFont);
		pGraphics.translate(-x, -y);        
	}
	
	private void drawSelfEdge(GraphicsContext pGraphics)
	{
		Arc arc = (Arc) getShape();
		double width = pGraphics.getLineWidth();
		pGraphics.setLineWidth(LINE_WIDTH);
		pGraphics.strokeArc(arc.getCenterX(), arc.getCenterY(), arc.getRadiusX(), arc.getRadiusY(), arc.getStartAngle(), 
				arc.getLength(), arc.getType());
		pGraphics.setLineWidth(width);
	}
	
	private Rectangle2D getLabelBounds()
	{
		if( isSelfEdge() )
		{
			return getSelfEdgeLabelBounds();
		}
		else
		{
			return getNormalEdgeLabelBounds();
		}
	}
	
	/*
	 * Gets the bounds of the label text.
	 * @return the bounds of the label text
	 */
	private Rectangle2D getNormalEdgeLabelBounds()
	{
		Line line = getConnectionPoints();
		Point2D control = getControlPoint();
		double x = control.getX() / 2 + line.getX1() / 4 + line.getX2() / 4;
		double y = control.getY() / 2 + line.getY1() / 4 + line.getY2() / 4;

		adjustLabelFont();
		Rectangle bounds = getLabelBounds(aLabel);

		int gap = 3;
		if( line.getY1() >= line.getY2() - VERTICAL_TOLERANCE && 
				line.getY1() <= line.getY2() + VERTICAL_TOLERANCE ) 
		{
			// The label is centered if the edge is (mostly) horizontal
			x -= bounds.getWidth() / 2;
		}
		else if( line.getY1() <= line.getY2() )
		{
			x += gap;
		}
		else
		{
			x -= bounds.getWidth() + gap;
		}
		
		if( line.getX1() <= line.getX2() )
		{
			y -= bounds.getHeight() + gap;
		}
		else
		{
			y += gap;
		}
		
		// Additional gap to make sure the labels don't overlap
		if( edge().getDiagram() != null && getPosition() > 1 )
		{
			double delta = Math.abs(Math.atan2(line.getX2()-line.getX1(), line.getY2()-line.getY1()));
			delta = bounds.getHeight() - delta*RADIANS_TO_PIXELS;
			if( line.getX1() <= line.getX2() )
			{
				y -= delta;
			}
			else
			{
				y += delta;
			}
		}
		return new Rectangle2D(x, y, bounds.getWidth(), bounds.getHeight());
}   
	
	/*
	 * Positions the label above the self edge, centered
	 * in the middle of it.
	 * @return the bounds of the label text
	 */
	private Rectangle2D getSelfEdgeLabelBounds()
	{
		Line line = getConnectionPoints();
		adjustLabelFont();
		Rectangle dimension = getLabelBounds(aLabel);
		if( getPosition() == 1 )
		{
			return new Rectangle2D(line.getX1() + SELF_EDGE_OFFSET - dimension.getWidth()/2,	
					line.getY1() - SELF_EDGE_OFFSET*2, dimension.getWidth(), dimension.getHeight());
		}
		else
		{
			return new Rectangle2D(line.getX1() - dimension.getWidth()/2,	
					line.getY1() - SELF_EDGE_OFFSET * HEIGHT_RATIO, dimension.getWidth(), dimension.getHeight());
		}
	}   
	
	/**
     * Gets the bounding rectangle for pString.
     * @param pString The input string. Cannot be null.
     * @return the bounding rectangle (with top left corner (0,0))
	 */
	public Rectangle getLabelBounds(String pString)
	{
		if(pString == null || pString.length() == 0) 
		{
			return new Rectangle(0, 0, 0, 0);
		}
		
		Bounds bounds = textBounds(pString);
		int width = (int) Math.round(bounds.getWidth());
		int height = (int) Math.round(bounds.getHeight());
		return new Rectangle(0, 0, width, height);
	}
	
	private void adjustLabelFont()
	{
		if(((StateTransitionEdge) edge()).getMiddleLabel().length() > MAX_LENGTH_FOR_NORMAL_FONT)
		{
			float difference = ((StateTransitionEdge) edge()).getMiddleLabel().length() - MAX_LENGTH_FOR_NORMAL_FONT;
			difference = difference / (2*((StateTransitionEdge) edge()).getMiddleLabel().length()); // damping
			double newFontSize = Math.max(MIN_FONT_SIZE, (1-difference) * FONT.getSize());
			aFont = new Font(aFont.getName(), newFontSize);
		}
		else
		{
			aFont = FONT;
		}
	}

	@Override
	protected Shape getShape()
	{
		if( isSelfEdge() )
		{
			return getSelfEdgeShape();
		}
		else
		{
			return getNormalEdgeShape();
		}
	}
	
	private boolean isSelfEdge()
	{
		return edge().getStart() == edge().getEnd();
	}
	
	private Shape getSelfEdgeShape()
	{
		Line line = getSelfEdgeConnectionPoints();
		Arc arc = new Arc();
		arc.setRadiusX(SELF_EDGE_OFFSET*2);
		arc.setRadiusY(SELF_EDGE_OFFSET*2);
		arc.setLength(DEGREES_270);
		arc.setType(ArcType.OPEN);
		if( getPosition() == 1 )
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
	public boolean contains(Point pPoint)
	{
		boolean result = super.contains(pPoint);
		if (getShape() instanceof Arc)
		{
			Arc arc = (Arc) getShape();
			arc.setRadiusX(arc.getRadiusX() + 2 * MAX_DISTANCE);
			arc.setRadiusY(arc.getRadiusY() + 2 * MAX_DISTANCE);
			result = arc.contains(pPoint.getX(), pPoint.getY());
		}
		return result;
	}
	
	/** 
	 * @return An index that represents the position in the list of
	 * edges between the same start and end nodes. 
	 * @pre getGraph() != null
	 */
	private int getPosition()
	{
		assert edge().getDiagram() != null;
		int lReturn = 0;
		for( Edge edge : edge().getDiagram().edgesConnectedTo(edge().getStart()))
		{
			if( edge.getStart() == edge().getStart() && edge.getEnd() == edge().getEnd())
			{
				lReturn++;
			}
			if( edge == edge() )
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
	private Line getSelfEdgeConnectionPoints()
	{
		if( getPosition() == 1 )
		{
			Point2D point1 = new Point2D(edge().getStart().view().getBounds().getMaxX() - SELF_EDGE_OFFSET, 
					edge().getStart().view().getBounds().getY());
			Point2D point2 = new Point2D(edge().getStart().view().getBounds().getMaxX(), 
					edge().getStart().view().getBounds().getY() + SELF_EDGE_OFFSET);
			return new Line(Conversions.toPoint(point1), Conversions.toPoint(point2));
		}
		else
		{
			Point2D point1 = new Point2D(edge().getStart().view().getBounds().getX(), 
					edge().getStart().view().getBounds().getY() + SELF_EDGE_OFFSET);
			Point2D point2 = new Point2D(edge().getStart().view().getBounds().getX() + SELF_EDGE_OFFSET, 
					edge().getStart().view().getBounds().getY());
			return new Line(Conversions.toPoint(point1), Conversions.toPoint(point2));
		}
	}
	
	private Shape getNormalEdgeShape()
	{
		Line line = getConnectionPoints();
		Path path = new Path();
		MoveTo moveTo = new MoveTo(line.getPoint1().getX(), line.getPoint1().getY());
		QuadCurveTo curveTo = new QuadCurveTo(getControlPoint().getX(), getControlPoint().getY(), line.getPoint2().getX(), line.getPoint2().getY());
		path.getElements().addAll(moveTo, curveTo);
		return path;
	}
	
	
	/**
     *  Gets the control point for the quadratic spline.
     * @return the control point
     */
	private Point2D getControlPoint()
	{
		Line line = getConnectionPoints();
		double tangent = Math.tan(Math.toRadians(DEGREES_10));
		if( edge().getDiagram() != null && getPosition() > 1 )
		{
			tangent = Math.tan(Math.toRadians(DEGREES_20));
		}
		double dx = (line.getX2() - line.getX1()) / 2;
		double dy = (line.getY2() - line.getY1()) / 2;
		return new Point2D((line.getX1() + line.getX2()) / 2 + tangent * dy, (line.getY1() + line.getY2()) / 2 - tangent * dx);         
	}
	
	@Override
	public Rectangle getBounds()
	{
		return super.getBounds().add(Conversions.toRectangle(getLabelBounds()));
	}
	
	@Override
	public Line getConnectionPoints()
	{
		if(isSelfEdge())
		{
			return getSelfEdgeConnectionPoints();
		}
		else
		{
			return getNormalEdgeConnectionsPoints();
		}
	}
	
	/*
	 * The connection points are a slight offset from the center.
	 * @return
	 */
	private Line getNormalEdgeConnectionsPoints()
	{
		Rectangle start = edge().getStart().view().getBounds();
		Rectangle end = edge().getEnd().view().getBounds();
		Point startCenter = start.getCenter();
		Point endCenter = end.getCenter();
		int turn = DEGREES_5;
		if( edge().getDiagram() != null && getPosition() > 1 )
		{
			turn = DEGREES_20;
		}
		Direction d1 = new Direction(startCenter, endCenter).turn(-turn);
		Direction d2 = new Direction(endCenter, startCenter).turn(turn);
		return new Line(edge().getStart().view().getConnectionPoint(d1), edge().getEnd().view().getConnectionPoint(d2));
	}
}
