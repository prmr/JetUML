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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import com.horstmann.violet.framework.Direction;
import com.horstmann.violet.framework.ShapeEdge;

/**
   A dotted line that connects a note to its attachment.
*/
public class NoteEdge extends ShapeEdge
{
   public void draw(Graphics2D g2)
   {
      Stroke oldStroke = g2.getStroke();
      g2.setStroke(DOTTED_STROKE);
      g2.draw(getConnectionPoints());
      g2.setStroke(oldStroke);
   }

   public Line2D getConnectionPoints()
   {
      Rectangle2D start = getStart().getBounds();
      Rectangle2D end = getEnd().getBounds();
      Direction d = new Direction(end.getCenterX() - start.getCenterX(), end.getCenterY() - start.getCenterY());

      return new Line2D.Double(getStart().getConnectionPoint(d), getEnd().getConnectionPoint(d.turn(180)));
   }

   public Shape getShape()
   {
      GeneralPath path = new GeneralPath();
      Line2D conn = getConnectionPoints();
      path.moveTo((float)conn.getX1(), (float)conn.getY1());
      path.lineTo((float)conn.getX2(), (float)conn.getY2());
      return path;
   }

   private static Stroke DOTTED_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f,new float[] { 3.0f, 3.0f }, 0.0f);
}
