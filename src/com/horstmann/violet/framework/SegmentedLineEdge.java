/*
Violet - A program for editing UML diagrams.

Copyright (C) 2002 Cay S. Horstmann (http://horstmann.com)

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.horstmann.violet.framework;

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

import com.horstmann.violet.ArrowHead;
import com.horstmann.violet.LineStyle;

/**
   An edge that is composed of multiple line segments
*/
public abstract class SegmentedLineEdge extends ShapeEdge
{
   /**
      Costructs an edge with no adornments.
   */
   public SegmentedLineEdge()
   {
      lineStyle = LineStyle.SOLID;
      startArrowHead = ArrowHead.NONE;
      endArrowHead = ArrowHead.NONE;
      startLabel = "";
      middleLabel = "";
      endLabel = "";
   }

   /**
      Sets the line style property.
      @param newValue the new value
   */
   public void setLineStyle(LineStyle newValue) { lineStyle = newValue; }

   /**
      Gets the line style property.
      @return the line style
   */
   public LineStyle getLineStyle() { return lineStyle; }

   /**
      Sets the start arrow head property
      @param newValue the new value
   */
   public void setStartArrowHead(ArrowHead newValue) { startArrowHead = newValue; }

   /**
      Gets the start arrow head property
      @return the start arrow head style
   */
   public ArrowHead getStartArrowHead() { return startArrowHead; }

   /**
      Sets the end arrow head property
      @param newValue the new value
   */
   public void setEndArrowHead(ArrowHead newValue) { endArrowHead = newValue; }

   /**
      Gets the end arrow head property
      @return the end arrow head style
   */
   public ArrowHead getEndArrowHead() { return endArrowHead; }

   /**
      Sets the start label property
      @param newValue the new value
   */
   public void setStartLabel(String newValue) { startLabel = newValue; }

   /**
      Gets the start label property
      @return the label at the start of the edge
   */
   public String getStartLabel() { return startLabel; }

   /**
      Sets the middle label property
      @param newValue the new value
   */
   public void setMiddleLabel(String newValue) { middleLabel = newValue; }

   /**
      Gets the middle label property
      @return the label at the middle of the edge
   */
   public String getMiddleLabel() { return middleLabel; }

   /**
      Sets the end label property
      @param newValue the new value
   */
   public void setEndLabel(String newValue) { endLabel = newValue; }

   /**
      Gets the end label property
      @return the label at the end of the edge
   */
   public String getEndLabel() { return endLabel; }

   /**
      Draws the edge.
      @param g2 the graphics context
   */
   public void draw(Graphics2D g2)
   {
      ArrayList points = getPoints();
      
      Stroke oldStroke = g2.getStroke();
      g2.setStroke(lineStyle.getStroke());
      g2.draw(getSegmentPath());
      g2.setStroke(oldStroke);
      startArrowHead.draw(g2, (Point2D)points.get(1),
         (Point2D)points.get(0));
      endArrowHead.draw(g2, (Point2D)points.get(points.size() - 2),
         (Point2D)points.get(points.size() - 1));

      drawString(g2, (Point2D)points.get(1), (Point2D)points.get(0), 
            startArrowHead, startLabel, false);
      drawString(g2, (Point2D)points.get(points.size() / 2 - 1),
            (Point2D)points.get(points.size() / 2),
            null, middleLabel, true);
      drawString(g2, (Point2D)points.get(points.size() - 2),
            (Point2D)points.get(points.size() - 1), 
            endArrowHead, endLabel, false);
   }

   /**
      Draws a string.
      @param g2 the graphics context
      @param p an endpoint of the segment along which to
      draw the string
      @param q the other endpoint of the segment along which to
      draw the string
      @param s the string to draw
      @param center true if the string should be centered
      along the segment
   */
   private static void drawString(Graphics2D g2, 
      Point2D p, Point2D q, ArrowHead arrow, String s, boolean center)
   {
      if (s == null || s.length() == 0) return;
      label.setText("<html>" + s + "</html>");
      label.setFont(g2.getFont());
      Dimension d = label.getPreferredSize();      
      label.setBounds(0, 0, d.width, d.height);

      Rectangle2D b = getStringBounds(g2, p, q, arrow, s, center);
      
      Color oldColor = g2.getColor();
      g2.setColor(g2.getBackground());
      g2.fill(b);
      g2.setColor(oldColor);
      
      g2.translate(b.getX(), b.getY());
      label.paint(g2);
      g2.translate(-b.getX(), -b.getY());        
   }

   /**
      Computes the attachment point for drawing a string.
      @param g2 the graphics context
      @param p an endpoint of the segment along which to
      draw the string
      @param q the other endpoint of the segment along which to
      draw the string
      @param b the bounds of the string to draw
      @param center true if the string should be centered
      along the segment
      @return the point at which to draw the string
   */
   private static Point2D getAttachmentPoint(Graphics2D g2, 
      Point2D p, Point2D q, ArrowHead arrow, Dimension d, boolean center)
   {    
      final int GAP = 3;
      double xoff = GAP;
      double yoff = -GAP - d.getHeight();
      Point2D attach = q;
      if (center)
      {
         if (p.getX() > q.getX()) 
         { 
            return getAttachmentPoint(g2, q, p, arrow, d, center); 
         }
         attach = new Point2D.Double((p.getX() + q.getX()) / 2, 
            (p.getY() + q.getY()) / 2);
         if (p.getY() < q.getY())
            yoff =  - GAP - d.getHeight();
         else if (p.getY() == q.getY())
            xoff = -d.getWidth() / 2;
         else
            yoff = GAP;
      }
      else 
      {
         if (p.getX() < q.getX())
         {
            xoff = -GAP - d.getWidth();
         }
         if (p.getY() > q.getY())
         {
            yoff = GAP;
         }
         if (arrow != null)
         {
            Rectangle2D arrowBounds = arrow.getPath(p, q).getBounds2D();
            if (p.getX() < q.getX())
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

   /**
      Computes the extent of a string that is drawn along a line segment.
      @param g2 the graphics context
      @param p an endpoint of the segment along which to
      draw the string
      @param q the other endpoint of the segment along which to
      draw the string
      @param s the string to draw
      @param center true if the string should be centered
      along the segment
      @return the rectangle enclosing the string
   */
   private static Rectangle2D getStringBounds(Graphics2D g2, 
      Point2D p, Point2D q, ArrowHead arrow, String s, boolean center)
   {
      if (g2 == null) return new Rectangle2D.Double();
      if (s == null || s.equals("")) return new Rectangle2D.Double(q.getX(), q.getY(), 0, 0);
      label.setText("<html>" + s + "</html>");
      label.setFont(g2.getFont());
      Dimension d = label.getPreferredSize();
      Point2D a = getAttachmentPoint(g2, p, q, arrow, d, center);
      return new Rectangle2D.Double(a.getX(), a.getY(), d.getWidth(), d.getHeight());
   }

   public Rectangle2D getBounds(Graphics2D g2)
   {
      ArrayList points = getPoints();
      Rectangle2D r = super.getBounds(g2);
      r.add(getStringBounds(g2, 
               (Point2D) points.get(1), (Point2D) points.get(0), 
               startArrowHead, startLabel, false));
      r.add(getStringBounds(g2, 
               (Point2D) points.get(points.size() / 2 - 1),
               (Point2D) points.get(points.size() / 2), 
               null, middleLabel, true));
      r.add(getStringBounds(g2, 
               (Point2D) points.get(points.size() - 2),
               (Point2D) points.get(points.size() - 1), 
               endArrowHead, endLabel, false));
      return r;
   }

   public Shape getShape()
   {
      GeneralPath path = getSegmentPath();
      ArrayList points = getPoints();
      path.append(startArrowHead.getPath((Point2D)points.get(1),
            (Point2D)points.get(0)), false);
      path.append(endArrowHead.getPath((Point2D)points.get(points.size() - 2),
            (Point2D)points.get(points.size() - 1)), false);
      return path;
   }

   private GeneralPath getSegmentPath()
   {
      ArrayList points = getPoints();
      
      GeneralPath path = new GeneralPath();
      Point2D p = (Point2D) points.get(points.size() - 1);
      path.moveTo((float) p.getX(), (float) p.getY());
      for (int i = points.size() - 2; i >= 0; i--)
      {
         p = (Point2D) points.get(i);
         path.lineTo((float) p.getX(), (float) p.getY());
      }
      return path;
   }
   
   public Line2D getConnectionPoints()
   {
      ArrayList points = getPoints();
      return new Line2D.Double((Point2D) points.get(0),
         (Point2D) points.get(points.size() - 1));
   }

   /**
      Gets the corner points of this segmented line edge
      @return an array list of Point2D objects, containing
      the corner points
   */
   public abstract ArrayList getPoints();

   private LineStyle lineStyle;
   private ArrowHead startArrowHead;
   private ArrowHead endArrowHead;
   private String startLabel;
   private String middleLabel;
   private String endLabel;
   
   private static JLabel label = new JLabel();
}
