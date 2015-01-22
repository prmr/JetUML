/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 Cay S. Horstmann and the contributors of the 
 * JetUML project.
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JLabel;

import ca.mcgill.cs.stg.jetuml.ArrowHead;
import ca.mcgill.cs.stg.jetuml.LineStyle;

/**
 *  An edge that is composed of multiple line segments.
 */
public abstract class SegmentedLineEdge extends ShapeEdge
{
	private static JLabel label = new JLabel();
	
	private LineStyle aLineStyle;
	private ArrowHead aStartArrowHead;
	private ArrowHead aEndArrowHead;
	private String aStartLabel;
	private String aMiddleLabel;
	private String aEndLabel;
	
	/**
     * Constructs an edge with no adornments.
	 */
	public SegmentedLineEdge()
	{
		aLineStyle = LineStyle.SOLID;
		aStartArrowHead = ArrowHead.NONE;
		aEndArrowHead = ArrowHead.NONE;
		aStartLabel = "";
		aMiddleLabel = "";
		aEndLabel = "";
	}

   /**
    *  Sets the line style property.
    *  @param pNewValue the new value
    */
   public void setLineStyle(LineStyle pNewValue) 
   { aLineStyle = pNewValue; }

   /**
      Gets the line style property.
      @return the line style
   */
   	public LineStyle getLineStyle() 
   	{ return aLineStyle; }

   /**
    *  Sets the start arrow head property.
    *  @param pNewValue the new value
    */
   	public void setStartArrowHead(ArrowHead pNewValue) 
   	{ aStartArrowHead = pNewValue; }

   	/**
     * Gets the start arrow head property.
     * @return the start arrow head style
   	 */
   	public ArrowHead getStartArrowHead()
   	{ return aStartArrowHead; }

   	/**
     *  Sets the end arrow head property.
     * @param pNewValue the new value
     */
   	public void setEndArrowHead(ArrowHead pNewValue) 
	{ aEndArrowHead = pNewValue; }

   	/**
     *  Gets the end arrow head property.
     * @return the end arrow head style
     */
  	public ArrowHead getEndArrowHead() 
  	{ return aEndArrowHead; }

  	/**
     * Sets the start label property.
     * @param pNewValue the new value
     */
  	public void setStartLabel(String pNewValue) 
  	{ aStartLabel = pNewValue; }

  	/**
     * Gets the start label property.
     * @return the label at the start of the edge
     */
  	public String getStartLabel() 
  	{ return aStartLabel; }

   /**
    *  Sets the middle label property.
    *  @param pNewValue the new value
    */
  	public void setMiddleLabel(String pNewValue) 
  	{ aMiddleLabel = pNewValue; }

  	/**
     * Gets the middle label property.
     * @return the label at the middle of the edge
  	 */
  	public String getMiddleLabel() 
  	{ return aMiddleLabel; }

  	/**
      Sets the end label property.
      @param pNewValue the new value
  	 */
  	public void setEndLabel(String pNewValue) 
  	{ aEndLabel = pNewValue; }

  	/**
     * Gets the end label property.
     * @return the label at the end of the edge
  	 */
  	public String getEndLabel() 
  	{ return aEndLabel; }

  	@Override
  	public void draw(Graphics2D pGraphics2D)
  	{
  		ArrayList<Point2D> points = getPoints();
      
  		Stroke oldStroke = pGraphics2D.getStroke();
  		pGraphics2D.setStroke(aLineStyle.getStroke());
  		pGraphics2D.draw(getSegmentPath());
  		pGraphics2D.setStroke(oldStroke);
  		aStartArrowHead.draw(pGraphics2D, points.get(1), points.get(0));
  		aEndArrowHead.draw(pGraphics2D, points.get(points.size() - 2), points.get(points.size() - 1));

  		drawString(pGraphics2D, points.get(1), points.get(0), aStartArrowHead, aStartLabel, false);
  		drawString(pGraphics2D, points.get(points.size() / 2 - 1), points.get(points.size() / 2), null, aMiddleLabel, true);
  		drawString(pGraphics2D, points.get(points.size() - 2), points.get(points.size() - 1), aEndArrowHead, aEndLabel, false);
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
  		label.setText("<html>" + pString + "</html>");
  		label.setFont(pGraphics2D.getFont());
  		Dimension d = label.getPreferredSize();      
  		label.setBounds(0, 0, d.width, d.height);

  		Rectangle2D b = getStringBounds(pGraphics2D, pEndPoint1, pEndPoint2, pArrowHead, pString, pCenter);
      
  		Color oldColor = pGraphics2D.getColor();
  		pGraphics2D.setColor(pGraphics2D.getBackground());
  		pGraphics2D.fill(b);
  		pGraphics2D.setColor(oldColor);
      
  		pGraphics2D.translate(b.getX(), b.getY());
  		label.paint(pGraphics2D);
  		pGraphics2D.translate(-b.getX(), -b.getY());        
  	}

  	/**
     * Computes the attachment point for drawing a string.
     * @param pGraphics2D the graphics context
     * @param pEndPoint1 an endpoint of the segment along which to draw the string
     * @param pEndPoint2 the other endpoint of the segment along which to draw the string
     * @param b the bounds of the string to draw
     * @param pCenter true if the string should be centered along the segment
     * @return the point at which to draw the string
     */
  	private static Point2D getAttachmentPoint(Graphics2D pGraphics2D, Point2D pEndPoint1, Point2D pEndPoint2, 
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
  				return getAttachmentPoint(pGraphics2D, pEndPoint2, pEndPoint1, pArrow, pDimension, pCenter); 
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
     * @param g2 the graphics context
     * @param p an endpoint of the segment along which to draw the string
     * @param q the other endpoint of the segment along which to draw the string
     * @param s the string to draw
     * @param center true if the string should be centered along the segment
     * @return the rectangle enclosing the string
  	 */
  	private static Rectangle2D getStringBounds(Graphics2D pGraphics2D, Point2D pEndPoint1, Point2D pEndPoint2, 
  			ArrowHead pArrow, String pString, boolean pCenter)
  	{
  		if (pGraphics2D == null)
  		{
  			return new Rectangle2D.Double();
  		}
  		if (pString == null || pString.equals(""))
  		{
  			return new Rectangle2D.Double(pEndPoint2.getX(), pEndPoint2.getY(), 0, 0);
  		}
  		label.setText("<html>" + pString + "</html>");
  		label.setFont(pGraphics2D.getFont());
  		Dimension d = label.getPreferredSize();
  		Point2D a = getAttachmentPoint(pGraphics2D, pEndPoint1, pEndPoint2, pArrow, d, pCenter);
  		return new Rectangle2D.Double(a.getX(), a.getY(), d.getWidth(), d.getHeight());
  	}

  	@Override
  	public Rectangle2D getBounds(Graphics2D pGraphics2D)
  	{
  		ArrayList<Point2D> points = getPoints();
  		Rectangle2D r = super.getBounds(pGraphics2D);
  		r.add(getStringBounds(pGraphics2D, points.get(1), points.get(0), aStartArrowHead, aStartLabel, false));
  		r.add(getStringBounds(pGraphics2D, points.get(points.size() / 2 - 1), points.get(points.size() / 2), null, aMiddleLabel, true));
  		r.add(getStringBounds(pGraphics2D, points.get(points.size() - 2), points.get(points.size() - 1), aEndArrowHead, aEndLabel, false));
  		return r;
  	}
  	
  	@Override
  	public Shape getShape()
  	{
  		GeneralPath path = getSegmentPath();
  		ArrayList<Point2D> points = getPoints();
  		path.append(aStartArrowHead.getPath(points.get(1), points.get(0)), false);
  		path.append(aEndArrowHead.getPath(points.get(points.size() - 2), points.get(points.size() - 1)), false);
  		return path;
   }

  	private GeneralPath getSegmentPath()
  	{
  		ArrayList<Point2D> points = getPoints();
  		GeneralPath path = new GeneralPath();
  		Point2D p = points.get(points.size() - 1);
  		path.moveTo((float) p.getX(), (float) p.getY());
  		for(int i = points.size() - 2; i >= 0; i--)
  		{
  			p = points.get(i);
  			path.lineTo((float) p.getX(), (float) p.getY());
  		}
  		return path;
  	}
   
  	@Override
  	public Line2D getConnectionPoints()
  	{
  		ArrayList<Point2D> points = getPoints();
  		return new Line2D.Double(points.get(0), points.get(points.size() - 1));
  	}

  	/**
     * Gets the corner points of this segmented line edge.
     * @return an array list of Point2D objects, containing
     * the corner points
  	 */
  	protected abstract ArrayList<Point2D> getPoints();
}
