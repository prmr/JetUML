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
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import com.horstmann.violet.framework.Graph;
import com.horstmann.violet.framework.Grid;
import com.horstmann.violet.framework.MultiLineString;
import com.horstmann.violet.framework.RectangularNode;


/**
   A node in a state diagram.
*/
public class StateNode extends RectangularNode
{
   /**
      Construct a state node with a default size
   */
   public StateNode()
   {
      name = new MultiLineString();
      setBounds(new Rectangle2D.Double(0, 0, 
         DEFAULT_WIDTH, DEFAULT_HEIGHT));
   }

   public void draw(Graphics2D g2)
   {
      super.draw(g2);
      g2.draw(getShape());
      name.draw(g2, getBounds());
   }
   
   public Shape getShape()
   {       
      return new RoundRectangle2D.Double(getBounds().getX(),
         getBounds().getY(), getBounds().getWidth(), 
         getBounds().getHeight(), ARC_SIZE, ARC_SIZE);
   }

   public void layout(Graph g, Graphics2D g2, Grid grid)
   {
      Rectangle2D b = name.getBounds(g2);
      b = new Rectangle2D.Double(getBounds().getX(), 
         getBounds().getY(),
         Math.max(b.getWidth(), DEFAULT_WIDTH),
         Math.max(b.getHeight(), DEFAULT_HEIGHT));

      grid.snap(b);
      setBounds(b);
   }

   /**
      Sets the name property value.
      @param newValue the new state name
   */
   public void setName(MultiLineString newValue)
   {
      name = newValue;
   }

   /**
      Gets the name property value.
      @param the state name
   */
   public MultiLineString getName()
   {
      return name;
   }

   public Object clone()
   {
      StateNode cloned = (StateNode)super.clone();
      cloned.name = (MultiLineString)name.clone();
      return cloned;
   }

   private MultiLineString name;

   private static int ARC_SIZE = 20;
   private static int DEFAULT_WIDTH = 80;
   private static int DEFAULT_HEIGHT = 60;
}
