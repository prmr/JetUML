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

import java.awt.geom.Point2D;

/**
   This class describes a direction in the 2D plane. 
   A direction is a vector of length 1 with an angle between 0 
   (inclusive) and 360 degrees (exclusive). There is also
   a degenerate direction of length 0. 
*/
public class Direction
{
   /**
      Constructs a direction (normalized to length 1).
      @param dx the x-value of the direction
      @param dy the corresponding y-value of the direction
   */
   public Direction(double dx, double dy)
   {
      x = dx;
      y = dy;
      double length = Math.sqrt(x * x + y * y);
      if (length == 0) return;
      x = x / length;
      y = y / length;
   }

   /**
      Constructs a direction between two points
      @param p the starting point
      @param q the ending point
   */
   public Direction(Point2D p, Point2D q)
   {
      this(q.getX() - p.getX(),
         q.getY() - p.getY());
   }

   /**
      Turns this direction by an angle.
      @param angle the angle in degrees
   */
   public Direction turn(double angle)
   {
      double a = Math.toRadians(angle);
      return new Direction(
         x * Math.cos(a) - y * Math.sin(a),
         x * Math.sin(a) + y * Math.cos(a));
   }

   /**
      Gets the x-component of this direction
      @return the x-component (between -1 and 1)
   */
   public double getX()
   {
      return x;
   }

   /**
      Gets the y-component of this direction
      @return the y-component (between -1 and 1)
   */
   public double getY()
   {
      return y;
   }

   private double x;
   private double y;

   public static final Direction NORTH = new Direction(0, -1);
   public static final Direction SOUTH = new Direction(0, 1);
   public static final Direction EAST = new Direction(1, 0);
   public static final Direction WEST = new Direction(-1, 0);
}
