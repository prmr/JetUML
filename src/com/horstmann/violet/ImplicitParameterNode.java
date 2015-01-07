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
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.horstmann.violet.framework.Direction;
import com.horstmann.violet.framework.Edge;
import com.horstmann.violet.framework.Graph;
import com.horstmann.violet.framework.Grid;
import com.horstmann.violet.framework.MultiLineString;
import com.horstmann.violet.framework.Node;
import com.horstmann.violet.framework.RectangularNode;

/**
   An object node in a scenario diagram.
*/
public class ImplicitParameterNode extends RectangularNode
{
   /**
      Construct an object node with a default size
   */
   public ImplicitParameterNode()
   {
      name = new MultiLineString();
      name.setUnderlined(true);
      setBounds(new Rectangle2D.Double(0, 0, 
                   DEFAULT_WIDTH, DEFAULT_HEIGHT));
      topHeight = DEFAULT_TOP_HEIGHT;
   }

   public boolean contains(Point2D p)
   {
      Rectangle2D bounds = getBounds();
      return bounds.getX() <= p.getX() &&
         p.getX() <= bounds.getX() + bounds.getWidth();
   }

   public void draw(Graphics2D g2)
   {
      super.draw(g2);
      Rectangle2D top = getTopRectangle();
      g2.draw(top);
      name.draw(g2, top);
      double xmid = getBounds().getCenterX();
      Line2D line = new Line2D.Double(xmid, top.getMaxY(),
         xmid, getBounds().getMaxY());
      Stroke oldStroke = g2.getStroke();
      g2.setStroke(new BasicStroke(1.0f, 
         BasicStroke.CAP_ROUND, 
         BasicStroke.JOIN_ROUND, 
         0.0f, 
         new float[] { 5.0f, 5.0f }, 0.0f));
      g2.draw(line);
      g2.setStroke(oldStroke);
   }

   /**
      Returns the rectangle at the top of the object node.
      @return the top rectangle
   */
   public Rectangle2D getTopRectangle()
   {
      return new Rectangle2D.Double(getBounds().getX(),
         getBounds().getY(), getBounds().getWidth(), topHeight);
   }

   public Shape getShape() { return getTopRectangle(); }
   
   public boolean addEdge(Edge e, Point2D p1, Point2D p2)
   {
      return false;
   }

   public Point2D getConnectionPoint(Direction d)
   {
      if (d.getX() > 0)
         return new Point2D.Double(getBounds().getMaxX(),
            getBounds().getMinY() + topHeight / 2);
      else
         return new Point2D.Double(getBounds().getX(),
            getBounds().getMinY() + topHeight / 2);
   }

   public void layout(Graph g, Graphics2D g2, Grid grid)
   {
      Rectangle2D b = name.getBounds(g2); 
      b.add(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH,
          DEFAULT_TOP_HEIGHT));      
      Rectangle2D top = new Rectangle2D.Double(
         getBounds().getX(), getBounds().getY(),
         b.getWidth(), b.getHeight());
      grid.snap(top);
      setBounds(new Rectangle2D.Double(
         top.getX(), top.getY(), 
         top.getWidth(), getBounds().getHeight()));
      topHeight = top.getHeight();
   }

   /**
      Sets the name property value.
      @param newValue the name of this object
   */
   public void setName(MultiLineString n)
   {
      name = n;
   }

   /**
      Gets the name property value.
      @return the name of this object
   */
   public MultiLineString getName()
   {
      return name;
   }

   public Object clone()
   {
      ImplicitParameterNode cloned 
         = (ImplicitParameterNode)super.clone();
      cloned.name = (MultiLineString)name.clone();
      return cloned;
   }

   public boolean addNode(Node n, Point2D p)
   {
      return n instanceof CallNode || n instanceof PointNode;
   }

   private double topHeight;
   private MultiLineString name;

   private static int DEFAULT_TOP_HEIGHT = 60;
   private static int DEFAULT_WIDTH = 80;
   private static int DEFAULT_HEIGHT = 120;
}
