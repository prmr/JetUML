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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import com.horstmann.violet.framework.SerializableEnumeration;

/**
   This class defines arrowheads of various shapes.
*/
public class ArrowHead extends SerializableEnumeration
{
   private ArrowHead() {}
   
   /**
      Draws the arrowhead.
      @param g2 the graphics context
      @param p a point on the axis of the arrow head
      @param q the end point of the arrow head
   */
   public void draw(Graphics2D g2, Point2D p, Point2D q)
   {
      GeneralPath path = getPath(p, q);
      Color oldColor = g2.getColor();
      if (this == BLACK_DIAMOND || this == BLACK_TRIANGLE)
         g2.setColor(Color.BLACK);
      else
         g2.setColor(Color.WHITE);
      g2.fill(path);
      g2.setColor(oldColor);
      g2.draw(path);
   }

   /**
      Gets the path of the arrowhead
      @param p a point on the axis of the arrow head
      @param q the end point of the arrow head
      @return the path
   */
   public GeneralPath getPath(Point2D p, Point2D q)
   {
      GeneralPath path = new GeneralPath();
      if (this == NONE) return path;
      final double ARROW_ANGLE = Math.PI / 6;
      final double ARROW_LENGTH = 10;

      double dx = q.getX() - p.getX();
      double dy = q.getY() - p.getY();
      double angle = Math.atan2(dy, dx);
      double x1 = q.getX() 
         - ARROW_LENGTH * Math.cos(angle + ARROW_ANGLE);
      double y1 = q.getY() 
         - ARROW_LENGTH * Math.sin(angle + ARROW_ANGLE);
      double x2 = q.getX() 
         - ARROW_LENGTH * Math.cos(angle - ARROW_ANGLE);
      double y2 = q.getY() 
         - ARROW_LENGTH * Math.sin(angle - ARROW_ANGLE);

      path.moveTo((float)q.getX(), (float)q.getY());
      path.lineTo((float)x1, (float)y1);
      if (this == V)
      {
         path.moveTo((float)x2, (float)y2);
         path.lineTo((float)q.getX(), (float)q.getY());
      }
      else if (this == TRIANGLE || this == BLACK_TRIANGLE)
      {
         path.lineTo((float)x2, (float)y2);
         path.closePath();                  
      }
      else if (this == DIAMOND || this == BLACK_DIAMOND)
      {
         double x3 = x2 - ARROW_LENGTH * Math.cos(angle + ARROW_ANGLE);
         double y3 = y2 - ARROW_LENGTH * Math.sin(angle + ARROW_ANGLE);
         path.lineTo((float)x3, (float)y3);
         path.lineTo((float)x2, (float)y2);
         path.closePath();         
      }      
      return path;
   }
   
   public static final ArrowHead NONE = new ArrowHead();
   public static final ArrowHead TRIANGLE = new ArrowHead();
   public static final ArrowHead BLACK_TRIANGLE = new ArrowHead();
   public static final ArrowHead V = new ArrowHead();
   public static final ArrowHead HALF_V = new ArrowHead();
   public static final ArrowHead DIAMOND = new ArrowHead();
   public static final ArrowHead BLACK_DIAMOND = new ArrowHead();
}
