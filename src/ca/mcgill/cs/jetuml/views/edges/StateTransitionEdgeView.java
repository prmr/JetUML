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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;

import ca.mcgill.cs.jetuml.geom.Conversions;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Edge;
import ca.mcgill.cs.jetuml.graph.edges.StateTransitionEdge;
import ca.mcgill.cs.jetuml.views.ArrowHead;

/**
 * An edge view specialized for state transitions.
 * 
 * @author Martin P. Robillard
 */
public class StateTransitionEdgeView extends AbstractEdgeView
{
	private static final int SELF_EDGE_OFFSET = 15;
	private static final int DEGREES_5 = 5;
	private static final int DEGREES_10 = 10;
	private static final int DEGREES_20 = 20;
	private static final int DEGREES_270 = 270;
	
	private static final int RADIANS_TO_PIXELS = 10;
	private static final double HEIGHT_RATIO = 3.5;
	private static final int MAX_LENGTH_FOR_NORMAL_FONT = 15;
	private static final int MIN_FONT_SIZE = 9;
	
	// The amount of vertical difference in connection points to tolerate
	// before centering the edge label on one side instead of in the center.
	private static final int VERTICAL_TOLERANCE = 20; 

	private static final JLabel LABEL = new JLabel();
	private static final Font FONT_NORMAL = LABEL.getFont();
	
	/**
	 * @param pEdge The edge to wrap.
	 */
	public StateTransitionEdgeView(StateTransitionEdge pEdge) // should be labeled edge
	{
		super(pEdge);
	}
	
	@Override
	public StateTransitionEdge edge() // fix when edge hierarchy final
	{
		return (StateTransitionEdge)super.edge();
	}
	
	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		pGraphics2D.draw(getShape());
		drawLabel(pGraphics2D);
		drawArrowHead(pGraphics2D);
	}
	
	private void drawArrowHead(Graphics2D pGraphics2D)
	{
		if( isSelfEdge() )
		{
			Point connectionPoint2 = getSelfEdgeConnectionPoints().getPoint2();
			if( getPosition() == 1 )
			{
				ArrowHead.V.view().draw(pGraphics2D, new Point2D.Double(connectionPoint2.getX()+SELF_EDGE_OFFSET, 
						connectionPoint2.getY()-SELF_EDGE_OFFSET/4), 
						Conversions.toPoint2D(getConnectionPoints().getPoint2()));
			}
			else
			{
				ArrowHead.V.view().draw(pGraphics2D, new Point2D.Double(connectionPoint2.getX()-SELF_EDGE_OFFSET/4, 
						connectionPoint2.getY()-SELF_EDGE_OFFSET), 
						Conversions.toPoint2D(getConnectionPoints().getPoint2()));
			}
		}
		else
		{
			ArrowHead.V.view().draw(pGraphics2D, getControlPoint(), 
					Conversions.toPoint2D(getConnectionPoints().getPoint2()));
		}
	}
	
	/*
	 *  Draws the label.
	 *  @param pGraphics2D the graphics context
	 */
	private void drawLabel(Graphics2D pGraphics2D)
	{
		Rectangle2D labelBounds = getLabelBounds();
		double x = labelBounds.getX();
		double y = labelBounds.getY();
		pGraphics2D.translate(x, y);
		adjustLabelFont();
		LABEL.paint(pGraphics2D);
		pGraphics2D.translate(-x, -y);        
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
  *  Gets the bounds of the label text .
  * @return the bounds of the label text
  */
	private Rectangle2D getNormalEdgeLabelBounds()
	{
		Line line = getConnectionPoints();
		Point2D control = getControlPoint();
		double x = control.getX() / 2 + line.getX1() / 4 + line.getX2() / 4;
		double y = control.getY() / 2 + line.getY1() / 4 + line.getY2() / 4;

		LABEL.setText(toHtml(edge().getMiddleLabel()));
		adjustLabelFont();
		Dimension dimension = LABEL.getPreferredSize();
		LABEL.setBounds(0, 0, dimension.width, dimension.height);

		int gap = 3;
		if( line.getY1() >= line.getY2() - VERTICAL_TOLERANCE && 
				line.getY1() <= line.getY2() + VERTICAL_TOLERANCE ) 
		{
			// The label is centered if the edge is (mostly) horizontal
			x -= dimension.getWidth() / 2;
		}
		else if( line.getY1() <= line.getY2() )
		{
			x += gap;
		}
		else
		{
			x -= dimension.getWidth() + gap;
		}
		
		if( line.getX1() <= line.getX2() )
		{
			y -= dimension.getHeight() + gap;
		}
		else
		{
			y += gap;
		}
		
		// Additional gap to make sure the labels don't overlap
		if( edge().getGraph() != null && getPosition() > 1 )
		{
			double delta = Math.abs(Math.atan2(line.getX2()-line.getX1(),line.getY2()-line.getY1()));
			delta = dimension.getHeight() - delta*RADIANS_TO_PIXELS;
			if( line.getX1() <= line.getX2() )
			{
				y -= delta;
			}
			else
			{
				y += delta;
			}
		}
		return new Rectangle2D.Double(x, y, dimension.width, dimension.height);
}   
	
	/*
  * Positions the label above the self edge, centered
  * in the middle of it.
  * @return the bounds of the label text
  */
	private Rectangle2D getSelfEdgeLabelBounds()
	{
		Line line = getConnectionPoints();
		LABEL.setText(toHtml(edge().getMiddleLabel()));
		adjustLabelFont();
		Dimension dimension = LABEL.getPreferredSize();
		LABEL.setBounds(0, 0, dimension.width, dimension.height);
		if( getPosition() == 1 )
		{
			return new Rectangle2D.Double(line.getX1() + SELF_EDGE_OFFSET - dimension.width/2,	
					line.getY1() - SELF_EDGE_OFFSET*2, dimension.width, dimension.height);
		}
		else
		{
			return new Rectangle2D.Double(line.getX1() - dimension.width/2,	
					line.getY1() - SELF_EDGE_OFFSET * HEIGHT_RATIO, dimension.width, dimension.height);
		}
	}   
	
	private void adjustLabelFont()
	{
		if(edge().getMiddleLabel().length() > MAX_LENGTH_FOR_NORMAL_FONT)
		{
			float difference = edge().getMiddleLabel().length() - MAX_LENGTH_FOR_NORMAL_FONT;
			difference = difference / (2*edge().getMiddleLabel().length()); // damping
			float newFontSize = Math.max(MIN_FONT_SIZE, (1-difference) * FONT_NORMAL.getSize());
			LABEL.setFont(FONT_NORMAL.deriveFont(newFontSize));
		}
		else
		{
			LABEL.setFont(FONT_NORMAL);
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
		if( getPosition() == 1 )
		{
			Line line = getSelfEdgeConnectionPoints();
			return new Arc2D.Double(line.getX1(), line.getY1()-SELF_EDGE_OFFSET, SELF_EDGE_OFFSET*2, SELF_EDGE_OFFSET*2, 
					DEGREES_270, DEGREES_270, Arc2D.OPEN);
		}
		else
		{
			Line line = getSelfEdgeConnectionPoints();
			return new Arc2D.Double(line.getX1()-SELF_EDGE_OFFSET, line.getY1()-SELF_EDGE_OFFSET*2, SELF_EDGE_OFFSET*2, SELF_EDGE_OFFSET*2, 
					 1, DEGREES_270, Arc2D.OPEN);
		}
	}
	
	/** 
	 * @return An index that represents the position in the list of
	 * edges between the same start and end nodes. 
	 * @pre getGraph() != null
	 */
	private int getPosition()
	{
		assert edge().getGraph() != null;
		int lReturn = 0;
		for( Edge edge : edge().getGraph().getEdges(edge().getStart()))
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
			Point2D.Double point1 = new Point2D.Double(edge().getStart().view().getBounds().getMaxX() - SELF_EDGE_OFFSET, 
					edge().getStart().view().getBounds().getY());
			Point2D.Double point2 = new Point2D.Double(edge().getStart().view().getBounds().getMaxX(), 
					edge().getStart().view().getBounds().getY() + SELF_EDGE_OFFSET);
			return new Line(Conversions.toPoint(point1),
					Conversions.toPoint(point2));
		}
		else
		{
			Point2D.Double point1 = new Point2D.Double(edge().getStart().view().getBounds().getX(), 
					edge().getStart().view().getBounds().getY() + SELF_EDGE_OFFSET);
			Point2D.Double point2 = new Point2D.Double(edge().getStart().view().getBounds().getX() + SELF_EDGE_OFFSET, 
					edge().getStart().view().getBounds().getY());
			return new Line(Conversions.toPoint(point1), 
					Conversions.toPoint(point2));
		}
	}
	
	private Shape getNormalEdgeShape()
	{
		Line line = getConnectionPoints();
		QuadCurve2D curve = new QuadCurve2D.Float();
		curve.setCurve(Conversions.toPoint2D(line.getPoint1()), getControlPoint(), 
				Conversions.toPoint2D(line.getPoint2()));
		return curve;
	}
	
	
	/**
     *  Gets the control point for the quadratic spline.
     * @return the control point
     */
	private Point2D getControlPoint()
	{
		Line line = getConnectionPoints();
		double tangent = Math.tan(Math.toRadians(DEGREES_10));
		if( edge().getGraph() != null && getPosition() > 1 )
		{
			tangent = Math.tan(Math.toRadians(DEGREES_20));
		}
		double dx = (line.getX2() - line.getX1()) / 2;
		double dy = (line.getY2() - line.getY1()) / 2;
		return new Point2D.Double((line.getX1() + line.getX2()) / 2 + tangent * dy, (line.getY1() + line.getY2()) / 2 - tangent * dx);         
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
		if( edge().getGraph() != null && getPosition() > 1 )
		{
			turn = DEGREES_20;
		}
		Direction d1 = new Direction(startCenter, endCenter).turn(-turn);
		Direction d2 = new Direction(endCenter, startCenter).turn(turn);
		return new Line(edge().getStart().view().getConnectionPoint(d1), edge().getEnd().view().getConnectionPoint(d2));
	}
	
}
