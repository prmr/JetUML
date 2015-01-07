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
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.horstmann.violet.framework.Direction;
import com.horstmann.violet.framework.ShapeEdge;

/**
   An S- or C-shaped edge with an arrowhead.
*/
public class ObjectReferenceEdge extends ShapeEdge
{
   public void draw(Graphics2D g2)
   {
      g2.draw(getShape());
      Line2D line = getConnectionPoints();
      double x1;
      double x2 = line.getX2();
      double y = line.getY2();
      if (isSShaped())
         x1 = x2 - ENDSIZE;
      else
         x1 = x2 + ENDSIZE;
      ArrowHead.BLACK_TRIANGLE.draw(g2, new Point2D.Double(x1, y), 
         new Point2D.Double(x2, y));      
   }

   public Shape getShape()
   {
      Line2D line = getConnectionPoints();

      double y1 = line.getY1();
      double y2 = line.getY2();
      double xmid = (line.getX1() + line.getX2()) / 2;
      double ymid = (line.getY1() + line.getY2()) / 2;
      GeneralPath p = new GeneralPath();
      if (isSShaped())
      {
         double x1 = line.getX1() + ENDSIZE;
         double x2 = line.getX2() - ENDSIZE;
         
         p.moveTo((float)line.getX1(), (float)y1);
         p.lineTo((float)x1, (float)y1);
         p.quadTo((float)((x1 + xmid) / 2), (float)y1, (float)xmid, (float)ymid);
         p.quadTo((float)((x2 + xmid) / 2), (float)y2, (float)x2, (float)y2);
         p.lineTo((float)line.getX2(), (float)y2);
      }
      else // reverse C shaped
      {
         double x1 = Math.max(line.getX1(), line.getX2()) + ENDSIZE;
         double x2 = x1 + ENDSIZE;
         p.moveTo((float)line.getX1(), (float)y1);
         p.lineTo((float)x1, (float)y1);
         p.quadTo((float)x2, (float)y1, (float)x2, (float)ymid);
         p.quadTo((float)x2, (float)y2, (float)x1, (float)y2);
         p.lineTo((float)line.getX2(), (float)y2);
      }
      return p;
   }

   public Line2D getConnectionPoints()
   {
      Rectangle2D b = getEnd().getBounds();
      Point2D p = getStart().getConnectionPoint(Direction.EAST);
      if (isSShaped())
         return new Line2D.Double(p, getEnd().getConnectionPoint(Direction.WEST));
      else 
         return new Line2D.Double(p, getEnd().getConnectionPoint(Direction.EAST));
   }

   /**
      Tests whether the node should be S- or C-shaped.
      @return true if the node should be S-shaped
   */
   private boolean isSShaped()
   {
      Rectangle2D b = getEnd().getBounds();
      Point2D p = getStart().getConnectionPoint(Direction.EAST);
      return b.getX() >= p.getX() + 2 * ENDSIZE;
   }

   private static final int ENDSIZE = 10;
}
