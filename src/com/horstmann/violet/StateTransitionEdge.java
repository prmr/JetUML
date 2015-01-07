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

package com.horstmann.violet;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;

import com.horstmann.violet.framework.Direction;
import com.horstmann.violet.framework.ShapeEdge;


/**
   A curved edge for a state transition in a state diagram.
*/
public class StateTransitionEdge extends ShapeEdge
{
   /**
      Sets the label property value.
      @param newValue the new value
   */
   public void setLabel(String newValue)
   {
      labelText = newValue;
   }

   /**
      Gets the label property value.
      @return the current value
   */
   public String getLabel()
   {
      return labelText;
   }

   public void draw(Graphics2D g2)
   {
      g2.draw(getShape());
      drawLabel(g2);
      ArrowHead.V.draw(g2, getControlPoint(), getConnectionPoints().getP2());
   }

   /**
      Draws the label.
      @param g2 the graphics context
   */
   private void drawLabel(Graphics2D g2)
   {
      Rectangle2D labelBounds = getLabelBounds(g2);
      double x = labelBounds.getX();
      double y = labelBounds.getY();
      
      g2.translate(x, y);
      label.paint(g2);
      g2.translate(-x, -y);        
   }

   
   /**
      Gets the bounds of the label text 
      @param g2 the graphics context
      @return the bounds of the label text
   */
   private Rectangle2D getLabelBounds(Graphics2D g2)
   {
      Line2D line = getConnectionPoints();
      Point2D control = getControlPoint();
      double x = control.getX() / 2 + line.getX1() / 4 + line.getX2() / 4;
      double y = control.getY() / 2 + line.getY1() / 4 + line.getY2() / 4;

      label.setText("<html>" + labelText + "</html>");
      label.setFont(g2.getFont());
      Dimension d = label.getPreferredSize();
      label.setBounds(0, 0, d.width, d.height);
   
      final int GAP = 3;
      if (line.getY1() == line.getY2())
         x -= d.getWidth() / 2;
      else if (line.getY1() <= line.getY2())
         x += GAP;
      else
         x -= d.getWidth() + GAP;
      if (line.getX1() == line.getX2())
         y += d.getHeight() / 2;
      else if (line.getX1() <= line.getX2())
         y -= d.getHeight() + GAP;
      else
         y += GAP;
      return new Rectangle2D.Double(x, y, d.width, d.height);
   }   

   /**
      Gets the control point for the quadratic spline.
      @return the control point
   */
   private Point2D getControlPoint()
   {
      Line2D line = getConnectionPoints();
      double t = Math.tan(Math.toRadians(angle));
      double dx = (line.getX2() - line.getX1()) / 2;
      double dy = (line.getY2() - line.getY1()) / 2;
      return new Point2D.Double(
         (line.getX1() + line.getX2()) / 2 + t * dy,
         (line.getY1() + line.getY2()) / 2 - t * dx);         
   }
   
   public Shape getShape()
   {
      Line2D line = getConnectionPoints();
      Point2D control = getControlPoint();
      GeneralPath p = new GeneralPath();
      p.moveTo((float)line.getX1(), (float)line.getY1());
      p.quadTo((float)control.getX(), (float)control.getY(), (float)line.getX2(), (float)line.getY2());      
      return p;
   }

   public Rectangle2D getBounds(Graphics2D g2)
   {
      Rectangle2D r = super.getBounds(g2);
      r.add(getLabelBounds(g2));
      return r;
   }
   
   public Line2D getConnectionPoints()
   {
      Direction d1;
      Direction d2;

      if (getStart() == getEnd())
      {
         angle = 60;
         d1 = Direction.EAST.turn(-30);
         d2 = Direction.EAST.turn(30);
      }
      else
      {
         angle = 10;
         Rectangle2D start = getStart().getBounds();
         Rectangle2D end = getEnd().getBounds();
         Point2D startCenter = new Point2D.Double(
            start.getCenterX(),
            start.getCenterY());
         Point2D endCenter = new Point2D.Double(
            end.getCenterX(),
            end.getCenterY());
         d1 = new Direction(startCenter, endCenter).turn(-5);
         d2 = new Direction(endCenter, startCenter).turn(5);
      }
      Point2D p = getStart().getConnectionPoint(d1);
      Point2D q = getEnd().getConnectionPoint(d2);

      return new Line2D.Double(p, q);
   }

   private double angle;
   private String labelText = "";
   
   private static JLabel label = new JLabel();
}
