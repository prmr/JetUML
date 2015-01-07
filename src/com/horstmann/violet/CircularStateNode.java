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

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;

import com.horstmann.violet.framework.Direction;
import com.horstmann.violet.framework.RectangularNode;

/**
   An initial or final node (bull's eye) in a state or activity diagram.
*/
public class CircularStateNode extends RectangularNode
{
   /**
      Construct a node with a default size
   */
   public CircularStateNode()
   {     
      setBounds(new Rectangle2D.Double(0, 0,
            DEFAULT_DIAMETER, DEFAULT_DIAMETER));      
   }
   
   public boolean isFinal()
   {
      return finalState; 
   }
   
   public void setFinal(boolean newValue)
   {
      finalState = newValue;
      Rectangle2D bounds = getBounds();
      double x = bounds.getX();
      double y = bounds.getY();
      
      if (finalState)
         setBounds(new Rectangle2D.Double(x - DEFAULT_GAP, y - DEFAULT_GAP,
               DEFAULT_DIAMETER + 2 * DEFAULT_GAP, 
               DEFAULT_DIAMETER + 2 * DEFAULT_GAP));
      else
         setBounds(new Rectangle2D.Double(x + DEFAULT_GAP, y + DEFAULT_GAP,
               DEFAULT_DIAMETER, DEFAULT_DIAMETER));
   }
   
   public Point2D getConnectionPoint(Direction d)
   {
      Rectangle2D bounds = getBounds();
      double a = bounds.getWidth() / 2;
      double b = bounds.getHeight() / 2;
      double x = d.getX();
      double y = d.getY();
      double cx = bounds.getCenterX();
      double cy = bounds.getCenterY();
      
      if (a != 0 && b != 0 && !(x == 0 && y == 0))
      {
         double t = Math.sqrt((x * x) / (a * a) + (y * y) / (b * b));
         return new Point2D.Double(cx + x / t, cy + y / t);
      }
      else
      {
         return new Point2D.Double(cx, cy);
      }
   }    

   public void draw(Graphics2D g2)
   {
      super.draw(g2);
      Ellipse2D circle
      = new Ellipse2D.Double(
            getBounds().getX(), getBounds().getY(),
            getBounds().getWidth(), getBounds().getHeight());
      
      if (finalState)
      {
         Rectangle2D bounds = getBounds();
         Ellipse2D inside
            = new Ellipse2D.Double(
               bounds.getX() + DEFAULT_GAP,
               bounds.getY() + DEFAULT_GAP,
               bounds.getWidth() - 2 * DEFAULT_GAP,
               bounds.getHeight() - 2 * DEFAULT_GAP);
         g2.fill(inside);
         g2.draw(circle);
      }
      else
         g2.fill(circle);      
   }
   
   public Shape getShape()
   {
      return new Ellipse2D.Double(
            getBounds().getX(), getBounds().getY(),
            getBounds().getWidth() - 1, getBounds().getHeight() - 1);
   }

   private boolean finalState; // final is a keyword
   
   private static int DEFAULT_DIAMETER = 30;
   private static int DEFAULT_GAP = 4;   
}

