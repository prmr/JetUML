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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.horstmann.violet.framework.Graph;
import com.horstmann.violet.framework.Grid;
import com.horstmann.violet.framework.MultiLineString;
import com.horstmann.violet.framework.Node;
import com.horstmann.violet.framework.RectangularNode;

/**
   An interface node in a class diagram.
*/
public class InterfaceNode extends RectangularNode
{
   /**
      Construct an interface node with a default size and
      the text <<interface>>.
   */
   public InterfaceNode()
   {
      name = new MultiLineString();
      name.setSize(MultiLineString.LARGE);
      name.setText("\u00ABinterface\u00BB");
      methods = new MultiLineString();
      methods.setJustification(MultiLineString.LEFT);
      setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
      midHeight = DEFAULT_COMPARTMENT_HEIGHT;
      botHeight = DEFAULT_COMPARTMENT_HEIGHT;
   }

   public void draw(Graphics2D g2)
   {
      super.draw(g2);
      Rectangle2D top = new Rectangle2D.Double(getBounds().getX(),
         getBounds().getY(), getBounds().getWidth(), 
         getBounds().getHeight() - midHeight - botHeight);
      g2.draw(top);
      name.draw(g2, top);
      Rectangle2D mid = new Rectangle2D.Double(top.getX(),
         top.getMaxY(), top.getWidth(), midHeight);
      g2.draw(mid);
      Rectangle2D bot = new Rectangle2D.Double(top.getX(),
         mid.getMaxY(), top.getWidth(), botHeight);
      g2.draw(bot);
      methods.draw(g2, bot);
   }

   public void layout(Graph g, Graphics2D g2, Grid grid)
   {
      Rectangle2D min = new Rectangle2D.Double(0, 0, 
         DEFAULT_WIDTH, DEFAULT_COMPARTMENT_HEIGHT);
      Rectangle2D top = name.getBounds(g2); 
      top.add(min);
      Rectangle2D bot = methods.getBounds(g2);

      botHeight = bot.getHeight();
      if (botHeight == 0)
      {
         top.add(new Rectangle2D.Double(0, 0, 
                    DEFAULT_WIDTH, 
                    DEFAULT_HEIGHT));
         midHeight = 0;
      }
      else
      {
         bot.add(min);
         midHeight = DEFAULT_COMPARTMENT_HEIGHT;
         botHeight = bot.getHeight();
      }

      Rectangle2D b = new Rectangle2D.Double(
         getBounds().getX(), getBounds().getY(),
         Math.max(top.getWidth(), bot.getWidth()), 
         top.getHeight() + midHeight + botHeight);
      grid.snap(b);
      setBounds(b);
   }

   public boolean addNode(Node n, Point2D p)
   {
      return n instanceof PointNode;
   }

   /**
      Sets the name property value.
      @param newValue the interface name
   */
   public void setName(MultiLineString newValue)
   {
      name = newValue;
   }

   /**
      Gets the name property value.
      @return the interface name
   */
   public MultiLineString getName()
   {
      return name;
   }

   /**
      Sets the methods property value.
      @param newValue the methods of this interface
   */
   public void setMethods(MultiLineString newValue)
   {
      methods = newValue;
   }

   /**
      Gets the methods property value.
      @return the methods of this interface
   */
   public MultiLineString getMethods()
   {
      return methods;
   }

   public Object clone()
   {
      InterfaceNode cloned = (InterfaceNode)super.clone();
      cloned.name = (MultiLineString)name.clone();
      cloned.methods = (MultiLineString)methods.clone();
      return cloned;
   }

   private double midHeight;
   private double botHeight;
   private MultiLineString name;
   private MultiLineString methods;

   private static int DEFAULT_COMPARTMENT_HEIGHT = 20;
   private static int DEFAULT_WIDTH = 100;
   private static int DEFAULT_HEIGHT = 60;
}
