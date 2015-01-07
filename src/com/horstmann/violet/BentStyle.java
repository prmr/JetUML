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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import com.horstmann.violet.framework.SerializableEnumeration;

/**
   A style for a segmented line that indicates the number
   and sequence of bends.
*/
public class BentStyle extends SerializableEnumeration
{
   private BentStyle() {}
  
   /**
      Gets the points at which a line joining two rectangles
      is bent according to this bent style.
      @param start the starting rectangle
      @param end the ending rectangle
      @return an array list of points at which to bend the
      segmented line joining the two rectangles
   */
   public ArrayList getPath(Rectangle2D start, Rectangle2D end)
   {
      ArrayList r = getPath(this, start, end);
      if (r != null) return r;
      
      if (start.equals(end)) r = getSelfPath(start);
      else if (this == HVH) r = getPath(VHV, start, end);
      else if (this == VHV) r = getPath(HVH, start, end);
      else if (this == HV) r = getPath(VH, start, end);
      else if (this == VH) r = getPath(HV, start, end);
      if (r != null) return r;

      return getPath(STRAIGHT, start, end);
   }

   /**
      Gets the four connecting points at which a bent line
      connects to a rectangle.
   */
   private static Point2D[] connectionPoints(Rectangle2D r)
   {
      Point2D[] a = new Point2D[4];
      a[0] = new Point2D.Double(r.getX(), r.getCenterY());
      a[1] = new Point2D.Double(r.getMaxX(), r.getCenterY());
      a[2] = new Point2D.Double(r.getCenterX(), r.getY());
      a[3] = new Point2D.Double(r.getCenterX(), r.getMaxY());
      return a;
   }
   
   /**
      Gets the points at which a line joining two rectangles
      is bent according to a bent style.
      @param start the starting rectangle
      @param end the ending rectangle
      @return an array list of points at which to bend the
      segmented line joining the two rectangles
   */
   private static ArrayList getPath(BentStyle bent, 
      Rectangle2D s, Rectangle2D e)
   {
      ArrayList r = new ArrayList();
      if (bent == STRAIGHT)
      {
         Point2D[] a = connectionPoints(s);
         Point2D[] b = connectionPoints(e);
         Point2D p = a[0];
         Point2D q = b[0];
         double distance = p.distance(q);
         if (distance == 0) return null;
         for (int i = 0; i < a.length; i++)
            for (int j = 0; j < b.length; j++)
            {
               double d = a[i].distance(b[j]);
               if (d < distance)
               {
                  p = a[i]; q = b[j];
                  distance = d;
               }
            }
         r.add(p);
         r.add(q);
      }
      else if (bent == HV)
      {
         double x1;
         double x2 = e.getCenterX();
         double y1 = s.getCenterY();
         double y2;
         if (x2 + MIN_SEGMENT <= s.getX())
            x1 = s.getX();
         else if (x2 - MIN_SEGMENT >= s.getMaxX())
            x1 = s.getMaxX();
         else return null;
         if (y1 + MIN_SEGMENT <= e.getY())
            y2 = e.getY();
         else if (y1 - MIN_SEGMENT >= e.getMaxY())
            y2 = e.getMaxY();
         else return null;
         r.add(new Point2D.Double(x1, y1));
         r.add(new Point2D.Double(x2, y1));
         r.add(new Point2D.Double(x2, y2));
      }
      else if (bent == VH)
      {
         double x1 = s.getCenterX();
         double x2;
         double y1;
         double y2 = e.getCenterY();
         if (x1 + MIN_SEGMENT <= e.getX())
            x2 = e.getX();
         else if (x1 - MIN_SEGMENT >= e.getMaxX())
            x2 = e.getMaxX();
         else return null;
         if (y2 + MIN_SEGMENT <= s.getY())
            y1 = s.getY();
         else if (y2 - MIN_SEGMENT >= s.getMaxY())
            y1 = s.getMaxY();
         else return null;
         r.add(new Point2D.Double(x1, y1));
         r.add(new Point2D.Double(x1, y2));
         r.add(new Point2D.Double(x2, y2));
      }
      else if (bent == HVH)
      {
         double x1;
         double x2;
         double y1 = s.getCenterY();
         double y2 = e.getCenterY();
         if (s.getMaxX() + 2 * MIN_SEGMENT <= e.getX())
         {
            x1 = s.getMaxX();
            x2 = e.getX();
         }
         else if (e.getMaxX() + 2 * MIN_SEGMENT <= s.getX())
         {
            x1 = s.getX();
            x2 = e.getMaxX();
         }
         else return null;
         if (Math.abs(y1 - y2) <= MIN_SEGMENT)
         {
            r.add(new Point2D.Double(x1, y2));
            r.add(new Point2D.Double(x2, y2));
         }
         else
         {
            r.add(new Point2D.Double(x1, y1));
            r.add(new Point2D.Double((x1 + x2) / 2, y1));
            r.add(new Point2D.Double((x1 + x2) / 2, y2));
            r.add(new Point2D.Double(x2, y2));
         }
      }
      else if (bent == VHV)
      {
         double x1 = s.getCenterX();
         double x2 = e.getCenterX();
         double y1;
         double y2;
         if (s.getMaxY() + 2 * MIN_SEGMENT <= e.getY())
         {
            y1 = s.getMaxY();
            y2 = e.getY();
         }
         else if (e.getMaxY() + 2 * MIN_SEGMENT <= s.getY())
         {
            y1 = s.getY();
            y2 = e.getMaxY();

         }
         else return null;
         if (Math.abs(x1 - x2) <= MIN_SEGMENT)
         {
            r.add(new Point2D.Double(x2, y1));
            r.add(new Point2D.Double(x2, y2));
         }
         else
         {
            r.add(new Point2D.Double(x1, y1));
            r.add(new Point2D.Double(x1, (y1 + y2) / 2));
            r.add(new Point2D.Double(x2, (y1 + y2) / 2));
            r.add(new Point2D.Double(x2, y2));
         }
      }
      else return null;
      return r;
   }

   /**
      Gets the points at which a line joining two rectangles
      is bent according to a bent style.
      @param s the starting and ending rectangle
   */
   private static ArrayList getSelfPath(Rectangle2D s)
   {
      ArrayList r = new ArrayList();
      double x1 = s.getX() + s.getWidth() * 3 / 4;
      double y1 = s.getY();
      double y2 = s.getY() - SELF_HEIGHT;
      double x2 = s.getX() + s.getWidth() + SELF_WIDTH;
      double y3 = s.getY() + s.getHeight() / 4;
      double x3 = s.getX() + s.getWidth();
      r.add(new Point2D.Double(x1, y1));
      r.add(new Point2D.Double(x1, y2));
      r.add(new Point2D.Double(x2, y2));
      r.add(new Point2D.Double(x2, y3));
      r.add(new Point2D.Double(x3, y3));
      return r;
   }

   private static final int MIN_SEGMENT = 10;
   private static final int SELF_WIDTH = 30;
   private static final int SELF_HEIGHT = 25;

   public static final BentStyle STRAIGHT = new BentStyle();
   public static final BentStyle HV = new BentStyle();
   public static final BentStyle VH = new BentStyle();
   public static final BentStyle HVH = new BentStyle();
   public static final BentStyle VHV = new BentStyle();
}
