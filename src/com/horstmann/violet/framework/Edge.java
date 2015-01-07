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

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;


/**
   An edge in a graph.
*/
public interface Edge extends Serializable, Cloneable
{
   /**
      Draw the edge.
      @param g2 the graphics context
   */
   void draw(Graphics2D g2);

   /**
      Tests whether the edge contains a point.
      @param aPoint the point to test
      @return true if this edge contains aPoint
   */
   boolean contains(Point2D aPoint);

   /**
      Connect this edge to two nodes.
      @param aStart the starting node
      @param anEnd the ending node
   */
   void connect(Node aStart, Node anEnd);

   /**
      Gets the starting node.
      @return the starting node
   */
   Node getStart();

   /**
      Gets the ending node.
      @return the ending node
   */
   Node getEnd();

   /**
      Gets the points at which this edge is connected to
      its nodes.
      @return a line joining the two connection points
   */
   Line2D getConnectionPoints();

   /**
      Gets the smallest rectangle that bounds this edge.
      The bounding rectangle contains all labels.
      @return the bounding rectangle
   */
   Rectangle2D getBounds(Graphics2D g2);

   Object clone();
}

