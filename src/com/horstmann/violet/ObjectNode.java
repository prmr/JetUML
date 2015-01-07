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
import java.util.List;

import com.horstmann.violet.framework.Direction;
import com.horstmann.violet.framework.Edge;
import com.horstmann.violet.framework.Graph;
import com.horstmann.violet.framework.Grid;
import com.horstmann.violet.framework.MultiLineString;
import com.horstmann.violet.framework.Node;
import com.horstmann.violet.framework.RectangularNode;

/**
   An object node in an object diagram.
*/
public class ObjectNode extends RectangularNode
{
   /**
      Construct an object node with a default size
   */
   public ObjectNode()
   {
      name = new MultiLineString();
      name.setUnderlined(true);
      name.setSize(MultiLineString.LARGE);
      setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
   }

   public void draw(Graphics2D g2)
   {
      super.draw(g2);
      Rectangle2D top = getTopRectangle();
      g2.draw(top);
      g2.draw(getBounds());
      name.draw(g2, top);
   }

   /**
      Returns the rectangle at the top of the object
      node.
      @return the top rectangle
   */
   public Rectangle2D getTopRectangle()
   {
      return new Rectangle2D.Double(getBounds().getX(),
         getBounds().getY(), getBounds().getWidth(), topHeight);
   }

   public boolean addEdge(Edge e, Point2D p1, Point2D p2)
   {
      return e instanceof ClassRelationshipEdge && e.getEnd() != null;
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
               DEFAULT_HEIGHT - YGAP));
      double leftWidth = 0;
      double rightWidth = 0;
      List fields = getChildren();
      double height = fields.size() == 0 ? 0 : YGAP;
      for (int i = 0; i < fields.size(); i++)
      {
         FieldNode f = (FieldNode)fields.get(i);
         f.layout(g, g2, grid);
         Rectangle2D b2 = f.getBounds();
         height += b2.getBounds().getHeight() + YGAP;   
         double axis = f.getAxisX();
         leftWidth = Math.max(leftWidth, axis);
         rightWidth = Math.max(rightWidth, b2.getWidth() - axis);
      }
      double width = 2 * Math.max(leftWidth, rightWidth) + 2 * XGAP;
      width = Math.max(width, b.getWidth());
      width = Math.max(width, DEFAULT_WIDTH);
      b = new Rectangle2D.Double(getBounds().getX(),
         getBounds().getY(), width, 
         b.getHeight() + height);
      grid.snap(b);
      setBounds(b);
      topHeight = b.getHeight() - height;
      double ytop = b.getY() + topHeight + YGAP;
      double xmid = b.getCenterX();
      for (int i = 0; i < fields.size(); i++)
      {
         FieldNode f = (FieldNode)fields.get(i);
         Rectangle2D b2 = f.getBounds();
         f.setBounds(new Rectangle2D.Double(
            xmid - f.getAxisX(), ytop,
            f.getAxisX() + rightWidth, b2.getHeight()));
         f.setBoxWidth(rightWidth);
         ytop += f.getBounds().getHeight() + YGAP;
      }
   }

   /**
      Sets the name property value.
      @param newValue the new object name
   */
   public void setName(MultiLineString n)
   {
      name = n;
   }

   /**
      Gets the name property value.
      @param the object name
   */
   public MultiLineString getName()
   {
      return name;
   }

   public Object clone()
   {
      ObjectNode cloned = (ObjectNode)super.clone();
      cloned.name = (MultiLineString)name.clone();
      return cloned;
   }

   public boolean addNode(Node n, Point2D p)
   {
      List fields = getChildren();
      if (n instanceof PointNode) return true;
      if (!(n instanceof FieldNode)) return false;
      if (fields.contains(n)) return true;
      int i = 0;
      while (i < fields.size() && ((Node)fields.get(i)).getBounds().getY() < p.getY())
         i++;
      addChild(i, n);
      return true;
   }
   /*
   public void removeNode(Graph g, Node n)
   {
      List fields = getChildren();
      if (n == this)
      {
         for (int i = fields.size() - 1; i >= 0; i--)
         {
            g.removeNode((Node)fields.get(i));
         }
      }
   }
   */
   /**
      This is a patch to ensure that object diagrams can
      be read back in correctly. 
   */
   public void addChild(Node n)
   {
      super.addChild(n);
      Rectangle2D b = getBounds();
      b.add(new Rectangle2D.Double(b.getX(), b.getY() + b.getHeight(),
               FieldNode.DEFAULT_WIDTH,
               FieldNode.DEFAULT_HEIGHT));
      setBounds(b);
   }

   private double topHeight;
   private MultiLineString name;

   private static int DEFAULT_WIDTH = 80;
   private static int DEFAULT_HEIGHT = 60;
   private static int XGAP = 5;
   private static int YGAP = 5;
}
