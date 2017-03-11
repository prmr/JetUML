/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016 by the contributors of the JetUML project.
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

package ca.mcgill.cs.stg.jetuml.graph;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;

import ca.mcgill.cs.stg.jetuml.framework.ArrowHead;
import ca.mcgill.cs.stg.jetuml.framework.Direction;

/**
 *  A curved edge for a state transition in a state diagram.
 */
public class StateTransitionEdge extends AbstractEdge
{
	private static final int DEGREES_5 = 5;
	private static final int DEGREES_10 = 10;
	private static final int DEGREES_30 = 30;
	private static final int DEGREES_60 = 60;
	private static JLabel label = new JLabel();
	private String aLabelText = "";
	   
	/**
     * Sets the label property value.
     * @param pNewValue the new value
	 */
	public void setLabel(String pNewValue)
	{
		aLabelText = pNewValue;
	}

	/**
     * Gets the label property value.
     * @return the current value
	 */
	public String getLabel()
	{
		return aLabelText;
	}

	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		pGraphics2D.draw(getShape());
		drawLabel(pGraphics2D);
		ArrowHead.V.draw(pGraphics2D, getControlPoint(), getConnectionPoints().getP2());
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
		label.paint(pGraphics2D);
		pGraphics2D.translate(-x, -y);        
	}

	/*
     *  Gets the bounds of the label text .
     * @return the bounds of the label text
     */
	private Rectangle2D getLabelBounds()
	{
		Line2D line = getConnectionPoints();
		Point2D control = getControlPoint();
		double x = control.getX() / 2 + line.getX1() / 4 + line.getX2() / 4;
		double y = control.getY() / 2 + line.getY1() / 4 + line.getY2() / 4;

		label.setText(toHtml(aLabelText));
		Dimension d = label.getPreferredSize();
		label.setBounds(0, 0, d.width, d.height);
   
		final int gap = 3;
		if (line.getY1() == line.getY2())
		{
			x -= d.getWidth() / 2;
		}
		else if (line.getY1() <= line.getY2())
		{
			x += gap;
		}
		else
		{
			x -= d.getWidth() + gap;
		}
		if (line.getX1() == line.getX2())
		{
			y += d.getHeight() / 2;
		}
		else if (line.getX1() <= line.getX2())
		{
			y -= d.getHeight() + gap;
		}
		else
		{
			y += gap;
		}
		return new Rectangle2D.Double(x, y, d.width, d.height);
   }   

	/**
     *  Gets the control point for the quadratic spline.
     * @return the control point
     */
	private Point2D getControlPoint()
	{
		Line2D line = getConnectionPoints();
		double t = Math.tan(Math.toRadians(getAngle()));
		double dx = (line.getX2() - line.getX1()) / 2;
		double dy = (line.getY2() - line.getY1()) / 2;
		return new Point2D.Double((line.getX1() + line.getX2()) / 2 + t * dy, (line.getY1() + line.getY2()) / 2 - t * dx);         
	}
   
	@Override
	protected Shape getShape()
	{
		Line2D line = getConnectionPoints();
		Point2D control = getControlPoint();
		GeneralPath p = new GeneralPath();
		p.moveTo((float)line.getX1(), (float)line.getY1());
		p.quadTo((float)control.getX(), (float)control.getY(), (float)line.getX2(), (float)line.getY2());      
		return p;
	}

	@Override
	public Rectangle2D getBounds()
	{
		Rectangle2D r = super.getBounds();
		r.add(getLabelBounds());
		return r;
	}
	
	private double getAngle()
	{
		if(getStart() == getEnd())
		{
			return DEGREES_60;
		}
		else
		{
			return DEGREES_10;
		}
	}
   
	@Override
	public Line2D getConnectionPoints()
	{
		Direction d1;
		Direction d2;

		if(getStart() == getEnd())
		{
			d1 = Direction.EAST.turn(-DEGREES_30);
			d2 = Direction.EAST.turn(DEGREES_30);
		}
		else
		{
			Rectangle2D start = getStart().getBounds();
			Rectangle2D end = getEnd().getBounds();
			Point2D startCenter = new Point2D.Double(start.getCenterX(), start.getCenterY());
			Point2D endCenter = new Point2D.Double(end.getCenterX(), end.getCenterY());
			d1 = new Direction(startCenter, endCenter).turn(-DEGREES_5);
			d2 = new Direction(endCenter, startCenter).turn(DEGREES_5);
		}
		return new Line2D.Double(getStart().getConnectionPoint(d1), getEnd().getConnectionPoint(d2));
	}
}
