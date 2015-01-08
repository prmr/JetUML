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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.List;


/**
   A node in a graph.
*/
public interface Node extends Serializable, Cloneable
{
   /**
      Draw the node.
      @param g2 the graphics context
   */
   void draw(Graphics2D g2);

   /**
      Translates the node by a given amount
      @param dx the amount to translate in the x-direction
      @param dy the amount to translate in the y-direction
   */
   void translate(double dx, double dy);

   /**
      Tests whether the node contains a point.
      @param aPoint the point to test
      @return true if this node contains aPoint
   */
   boolean contains(Point2D aPoint);

   /**
      Get the best connection point to connect this node 
      with another node. This should be a point on the boundary
      of the shape of this node.
      @param d the direction from the center 
      of the bounding rectangle towards the boundary 
      @return the recommended connection point
   */
   Point2D getConnectionPoint(Direction d);

   /**
      Get the bounding rectangle of the shape of this node
      @return the bounding rectangle
   */
   Rectangle2D getBounds();

   /**
      Adds an edge that originates at this node.
      @param p the point that the user selected as
      the starting point. This may be used as a hint if 
      edges are ordered.
      @param e the edge to add
      @return true if the edge was added
   */
   boolean addEdge(Edge e, Point2D p1, Point2D p2);

   /**
      Adds a node as a child node to this node.
      @param n the child node
      @param p the point at which the node is being added
      @return true if this node accepts the given node as a child
   */
   boolean addNode(Node n, Point2D p);

   /**
      Notifies this node that an edge is being removed.
      @param g the ambient graph
      @param e the edge to be removed
   */
   void removeEdge(Graph g, Edge e);

   /**
      Notifies this node that a node is being removed.
      @param g the ambient graph
      @param n the node to be removed
   */
   void removeNode(Graph g, Node n);

   /**
      Lays out the node and its children.
      @param g the ambient graph
      @param g2 the graphics context
      @param grid the grid to snap to
   */
   void layout(Graph g, Graphics2D g2, Grid grid);

   /**
      Gets the parent of this node.
      @return the parent node, or null if the node
      has no parent
   */
   Node getParent();

   /**
      Sets the parent of this node.
      @param node the parent node, or null if the node
      has no parent
   */
   void setParent(Node node);

   /**
      Gets the children of this node.
      @return an unmodifiable list of the children
   */
   List getChildren();

   /**
      Adds a child node.
      @param index the position at which to add the child
      @param node the child node to add
   */
   void addChild(int index, Node node);

   /**
      Removes a child node.
      @param node the child to remove.
   */
   void removeChild(Node node);

   Object clone();
}
