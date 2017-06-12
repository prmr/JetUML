/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2017 by the contributors of the JetUML project.
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
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;

import ca.mcgill.cs.stg.jetuml.framework.ArrowHead;
import ca.mcgill.cs.stg.jetuml.framework.LineStyle;

/**
 *  Adds support for drawing edges as collection of segmented
 *  lines, and adding arrow heads and labels to the edges. Note that
 *  the keyword "obtain" is used instead of "get" to avoid setting 
 *  some useless properties that would then be detected by the 
 *  Java Beans framework, for subclasses that don't use the properties.
 */
public abstract class SegmentedLabeledEdge extends AbstractEdge
{
	private static JLabel label = new JLabel();

	/**
	 * Constructs a solid edge with no adornments and no label.
	 */
	public SegmentedLabeledEdge() {}
	
	/**
	 * @return The line style for the edge. By default
	 * this is solid
	 */
	protected LineStyle obtainLineStyle()
	{
		return LineStyle.SOLID;
	}
	
	/**
	 * @return The start arrow head for the edge. By default
	 * there is no arrow head.
	 */
	protected ArrowHead obtainStartArrowHead()
	{
		return ArrowHead.NONE;
	}
	
	/**
	 * @return The end arrow head for the edge. By default
	 * there is no arrow head.
	 */
	protected ArrowHead obtainEndArrowHead()
	{
		return ArrowHead.NONE;
	}
	
	/**
	 * @return The start label. Empty by default.
	 */
	protected String obtainStartLabel()
	{
		return "";
	}
	
	/**
	 * @return The middle label. Empty by default.
	 */
	protected String obtainMiddleLabel()
	{
		return "";
	}
	
	/**
	 * @return The end label. Empty by default.
	 */
	protected String obtainEndLabel()
	{
		return "";
	}

	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		Point2D[] points = getPoints();

		Stroke oldStroke = pGraphics2D.getStroke();
		pGraphics2D.setStroke(obtainLineStyle().getStroke());
		pGraphics2D.draw(getSegmentPath());
		pGraphics2D.setStroke(oldStroke);
		obtainStartArrowHead().draw(pGraphics2D, points[1], points[0]);
		obtainEndArrowHead().draw(pGraphics2D, points[points.length - 2], points[points.length - 1]);

		drawString(pGraphics2D, points[1], points[0], obtainStartArrowHead(), obtainStartLabel(), false);
		drawString(pGraphics2D, points[points.length / 2 - 1], points[points.length / 2], null, obtainMiddleLabel(), true);
		drawString(pGraphics2D, points[points.length - 2], points[points.length - 1], obtainEndArrowHead(), obtainEndLabel(), false);
	}

	/**
	 * Draws a string.
	 * @param pGraphics2D the graphics context
	 * @param pEndPoint1 an endpoint of the segment along which to draw the string
	 * @param pEndPoint2 the other endpoint of the segment along which to draw the string
	 * @param pString the string to draw 
	 * @param pCenter true if the string should be centered along the segment
	 */
	private static void drawString(Graphics2D pGraphics2D, Point2D pEndPoint1, Point2D pEndPoint2, 
			ArrowHead pArrowHead, String pString, boolean pCenter)
	{
		if (pString == null || pString.length() == 0)
		{
			return;
		}
		label.setText(toHtml(pString));
		label.setFont(pGraphics2D.getFont());
		Dimension dimensions = label.getPreferredSize();      
		label.setBounds(0, 0, dimensions.width, dimensions.height);

		Rectangle2D bounds = getStringBounds(pEndPoint1, pEndPoint2, pArrowHead, pString, pCenter);

		pGraphics2D.translate(bounds.getX(), bounds.getY());
		label.paint(pGraphics2D);
		pGraphics2D.translate(-bounds.getX(), -bounds.getY());        
	}

	/**
	 * Computes the attachment point for drawing a string.
	 * @param pEndPoint1 an endpoint of the segment along which to draw the string
	 * @param pEndPoint2 the other endpoint of the segment along which to draw the string
	 * @param b the bounds of the string to draw
	 * @param pCenter true if the string should be centered along the segment
	 * @return the point at which to draw the string
	 */
	private static Point2D getAttachmentPoint(Point2D pEndPoint1, Point2D pEndPoint2, 
			ArrowHead pArrow, Dimension pDimension, boolean pCenter)
	{    
		final int gap = 3;
		double xoff = gap;
		double yoff = -gap - pDimension.getHeight();
		Point2D attach = pEndPoint2;
		if (pCenter)
		{
			if (pEndPoint1.getX() > pEndPoint2.getX()) 
			{ 
				return getAttachmentPoint(pEndPoint2, pEndPoint1, pArrow, pDimension, pCenter); 
			}
			attach = new Point2D.Double((pEndPoint1.getX() + pEndPoint2.getX()) / 2, 
					(pEndPoint1.getY() + pEndPoint2.getY()) / 2);
			if (pEndPoint1.getY() < pEndPoint2.getY())
			{
				yoff =  -gap-pDimension.getHeight();
			}
			else if (pEndPoint1.getY() == pEndPoint2.getY())
			{
				xoff = -pDimension.getWidth() / 2;
			}
			else
			{
				yoff = gap;
			}	
		}
		else 
		{
			if(pEndPoint1.getX() < pEndPoint2.getX())
			{
				xoff = -gap - pDimension.getWidth();
			}
			if(pEndPoint1.getY() > pEndPoint2.getY())
			{
				yoff = gap;
			}
			if(pArrow != null)
			{
				Rectangle2D arrowBounds = pArrow.getPath(pEndPoint1, pEndPoint2).getBounds2D();
				if(pEndPoint1.getX() < pEndPoint2.getX())
				{
					xoff -= arrowBounds.getWidth();
				}
				else
				{
					xoff += arrowBounds.getWidth();
				}
			}
		}
		return new Point2D.Double(attach.getX() + xoff, attach.getY() + yoff);
	}

	/*
	 * Computes the extent of a string that is drawn along a line segment.
	 * @param p an endpoint of the segment along which to draw the string
	 * @param q the other endpoint of the segment along which to draw the string
	 * @param s the string to draw
	 * @param center true if the string should be centered along the segment
	 * @return the rectangle enclosing the string
	 */
	private static Rectangle2D getStringBounds(Point2D pEndPoint1, Point2D pEndPoint2, 
			ArrowHead pArrow, String pString, boolean pCenter)
	{
		if (pString == null || pString.equals(""))
		{
			return new Rectangle2D.Double(pEndPoint2.getX(), pEndPoint2.getY(), 0, 0);
		}
		label.setText(toHtml(pString));
		Dimension d = label.getPreferredSize();
		Point2D a = getAttachmentPoint(pEndPoint1, pEndPoint2, pArrow, d, pCenter);
		return new Rectangle2D.Double(a.getX(), a.getY(), d.getWidth(), d.getHeight());
	}

	@Override
	public Rectangle2D getBounds()
	{
		Point2D[] points = getPoints();
		Rectangle2D bounds = super.getBounds();
		bounds.add(getStringBounds(points[1], points[0], obtainStartArrowHead(), obtainStartLabel(), false));
		bounds.add(getStringBounds(points[points.length / 2 - 1], points[points.length / 2], null, obtainMiddleLabel(), true));
		bounds.add(getStringBounds(points[points.length - 2], points[points.length - 1], obtainEndArrowHead(), obtainEndLabel(), false));
		return bounds;
	}

	@Override
	protected Shape getShape()
	{
		GeneralPath path = getSegmentPath();
		Point2D[] points = getPoints();
		path.append(obtainStartArrowHead().getPath(points[1], points[0]), false);
		path.append(obtainEndArrowHead().getPath(points[points.length - 2], points[points.length - 1]), false);
		return path;
	}

	private GeneralPath getSegmentPath()
	{
		Point2D[] points = getPoints();
		GeneralPath path = new GeneralPath();
		Point2D p = points[points.length - 1];
		path.moveTo((float) p.getX(), (float) p.getY());
		for(int i = points.length - 2; i >= 0; i--)
		{
			p = points[i];
			path.lineTo((float) p.getX(), (float) p.getY());
		}
		return path;
	}

	@Override
	public Line2D getConnectionPoints()
	{
		Point2D[] points = getPoints();
		return new Line2D.Double(points[0], points[points.length - 1]);
	}

	/**
	 * Gets the corner points of this segmented line edge.
	 * @return an array list of Point2D objects, containing
	 * the corner points
	 */
	protected abstract Point2D[] getPoints();
}
