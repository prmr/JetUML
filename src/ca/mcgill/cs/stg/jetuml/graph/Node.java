/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 Cay S. Horstmann and the contributors of the 
 * JetUML project.
 *
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/

package ca.mcgill.cs.stg.jetuml.graph;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import ca.mcgill.cs.stg.jetuml.framework.Direction;
import ca.mcgill.cs.stg.jetuml.framework.Grid;

/**
  * A node in a graph.
  */
public interface Node extends GraphElement
{
	/**
     *  Draw the node.
     * @param pGraphics2D the graphics context
     */
	void draw(Graphics2D pGraphics2D);

	/**
     * Translates the node by a given amount.
     * @param pDeltaX the amount to translate in the x-direction
     * @param pDeltaY the amount to translate in the y-direction
	 */
	void translate(double pDeltaX, double pDeltaY);

	/**
     * Tests whether the node contains a point.
     * @param pPoint the point to test
     * @return true if this node contains aPoint
     */
	boolean contains(Point2D pPoint);

	/**
     * Get the best connection point to connect this node 
     * with another node. This should be a point on the boundary
     * of the shape of this node.
     * @param pDirection the direction from the center 
     * of the bounding rectangle towards the boundary 
     * @return the recommended connection point
	 */
	Point2D getConnectionPoint(Direction pDirection);

	/**
     * Get the bounding rectangle of the shape of this node.
     * @return the bounding rectangle
	 */
	Rectangle2D getBounds();

	/**
     * Adds an edge that originates at this node.
     * @param pPoint1 the point that the user selected as
     * the starting point. This may be used as a hint if 
     * edges are ordered.
     * @param pPoint2 the end point.
     * @param pEdge the edge to add
     * @return true if the edge was added
	 */
	boolean addEdge(Edge pEdge, Point2D pPoint1, Point2D pPoint2);

	/**
     * Adds a node as a child node to this node.
     * @param pNode the child node
     * @param pPoint the point at which the node is being added
     * @return true if this node accepts the given node as a child
	 */
	boolean addNode(Node pNode, Point2D pPoint);

	/**
     * Notifies this node that an edge is being removed.
     * @param pGraph the ambient graph
     * @param pEdge the edge to be removed
	 */
	void removeEdge(Graph pGraph, Edge pEdge);

	/**
     * Notifies this node that a node is being removed.
     * @param pGraph the ambient graph
     * @param pNode the node to be removed
	 */
	void removeNode(Graph pGraph, Node pNode);

	/**
     * Lays out the node and its children.
     * @param pGraph the ambient graph
     * @param pGraphics2D the graphics context
     * @param pGrid the grid to snap to
	 */
	void layout(Graph pGraph, Graphics2D pGraphics2D, Grid pGrid);

	/**
	 * @return A clone of the node.
	 */
	Node clone();
}
