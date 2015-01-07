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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.horstmann.violet.framework.Edge;
import com.horstmann.violet.framework.Graph;
import com.horstmann.violet.framework.Grid;
import com.horstmann.violet.framework.Node;


/**
   A UML sequence diagram.
*/
public class SequenceDiagramGraph extends Graph
{
   public boolean add(Node n, Point2D p)
   {
      if (n instanceof CallNode) // must be inside an object
      {
         Collection nodes = getNodes();
         boolean inside = false;
         Iterator iter = nodes.iterator();
         while (!inside && iter.hasNext())
         {
            Node n2 = (Node)iter.next();
            if (n2 instanceof ImplicitParameterNode
               && n2.contains(p)) 
            {
               inside = true;
               ((CallNode)n).setImplicitParameter(
                  (ImplicitParameterNode)(n2));
            }
         }
         if (!inside) return false;
      }

      if (!super.add(n, p)) return false;

      return true;
   }

   public void removeEdge(Edge e)
   {
      super.removeEdge(e);
      if (e instanceof CallEdge && e.getEnd().getChildren().size() == 0)
         removeNode(e.getEnd());
   }
 
   public void layout(Graphics2D g2, Grid grid)
   {
      super.layout(g2, grid);

      ArrayList topLevelCalls = new ArrayList();
      ArrayList objects = new ArrayList();
      Collection nodes = getNodes();
      Iterator iter = nodes.iterator();
      while (iter.hasNext())
      {
         Node n = (Node)iter.next();
         
         if (n instanceof CallNode && n.getParent() == null) 
            topLevelCalls.add(n);
         else if (n instanceof ImplicitParameterNode)
            objects.add(n);      
      }

      Collection edges = getEdges();
      iter = edges.iterator();
      while (iter.hasNext())
      {
         Edge e = (Edge)iter.next();
         if (e instanceof CallEdge)
         {
            Node end = e.getEnd();
            if (end instanceof CallNode)
               ((CallNode)end).setSignaled(((CallEdge)e).isSignal());
         }
      }

      double left = 0;

      // find the max of the heights of the objects

      double top = 0;
      for (int i = 0; i < objects.size(); i++)
      {
         ImplicitParameterNode n = (ImplicitParameterNode)objects.get(i);
         n.translate(0, -n.getBounds().getY());
         top = Math.max(top, n.getTopRectangle().getHeight());
      }

      /*

      // sort topLevelCalls by y position
      Collections.sort(topLevelCalls, new
         Comparator()
         {
            public int compare(Object o1, Object o2)
            {
               CallNode c1 = (CallNode)o1;
               CallNode c2 = (CallNode)o2;
               double diff = c1.getBounds().getY()
                  - c2.getBounds().getY();
               if (diff < 0) return -1;
               if (diff > 0) return 1;
               return 0;
            }            
         });

      for (int i = 0; i < topLevelCalls.size(); i++)
      {
         CallNode call = (CallNode)topLevelCalls.get(i);
         top += CallNode.CALL_YGAP;

         call.translate(0, top - call.getBounds().getY());
         call.layout(this, g2, grid);
         top += call.getBounds().getHeight();
      }
      */

      for (int i = 0; i < topLevelCalls.size(); i++)
      {
         CallNode call = (CallNode) topLevelCalls.get(i);
         call.layout(this, g2, grid);
      }

      iter = nodes.iterator();
      while (iter.hasNext())
      {
         Node n = (Node)iter.next();
         if (n instanceof CallNode)
            top = Math.max(top, n.getBounds().getY()
               + n.getBounds().getHeight());
      }

      top += CallNode.CALL_YGAP;

      for (int i = 0; i < objects.size(); i++)
      {
         ImplicitParameterNode n = (ImplicitParameterNode) objects.get(i);
         Rectangle2D b = n.getBounds();
         n.setBounds(new Rectangle2D.Double(
            b.getX(), b.getY(), 
            b.getWidth(), top - b.getY()));         
      }
   }

   public void draw(Graphics2D g2, Grid g)
   {
      layout(g2, g);

      Collection nodes = getNodes();
      Iterator iter = nodes.iterator();
      while (iter.hasNext())
      {
         Node n = (Node) iter.next();
         if (!(n instanceof CallNode))
            n.draw(g2);
      }

      iter = nodes.iterator();
      while (iter.hasNext())
      {
         Node n = (Node) iter.next();
         if (n instanceof CallNode)
            n.draw(g2);
      }

      Collection edges = getEdges();
      iter = edges.iterator();
      while (iter.hasNext())
      {
         Edge e = (Edge) iter.next();
         e.draw(g2);
      }
   }

   public Node[] getNodePrototypes()
   {
      return NODE_PROTOTYPES;
   }

   public Edge[] getEdgePrototypes()
   {
      return EDGE_PROTOTYPES;
   }

   private static final Node[] NODE_PROTOTYPES = new Node[3];

   private static final Edge[] EDGE_PROTOTYPES = new Edge[3];

   static
   {
      NODE_PROTOTYPES[0] = new ImplicitParameterNode();
      NODE_PROTOTYPES[1] = new CallNode();
      NODE_PROTOTYPES[2] = new NoteNode();
      EDGE_PROTOTYPES[0] = new CallEdge();
      EDGE_PROTOTYPES[1] = new ReturnEdge();
      EDGE_PROTOTYPES[2] = new NoteEdge();
   }
}





