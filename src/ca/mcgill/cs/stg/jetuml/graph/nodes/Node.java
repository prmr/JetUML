/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016 by the contributors of the JetUML project.
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

package ca.mcgill.cs.stg.jetuml.graph.nodes;

import java.awt.Graphics2D;

import ca.mcgill.cs.stg.jetuml.geom.Direction;
import ca.mcgill.cs.stg.jetuml.geom.Point;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.GraphElement;
import ca.mcgill.cs.stg.jetuml.graph.views.nodes.NodeView;

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
	 * @return The position of this node. Usually corresponds to the top left corner 
	 * of its bounding box.
	 */
	default Point position()
	{
		return new Point(0, 0);
	}
	
	/**
	 * Move the position of the node to pPoint.
	 * 
	 * @param pPoint The new position of the node.
	 */
	default void moveTo(Point pPoint)
	{
		// TODO make non-default once all Node classes are subclasses of AbstractNode2
	}

	/**
     * Translates the node by a given amount.
     * @param pDeltaX the amount to translate in the x-direction
     * @param pDeltaY the amount to translate in the y-direction
	 */
	void translate(int pDeltaX, int pDeltaY);

	/**
     * Tests whether the node contains a point.
     * @param pPoint the point to test
     * @return true if this node contains aPoint
     */
	boolean contains(Point pPoint);

	/**
     * Get the best connection point to connect this node 
     * with another node. This should be a point on the boundary
     * of the shape of this node.
     * @param pDirection the direction from the center 
     * of the bounding rectangle towards the boundary 
     * @return the recommended connection point
	 */
	Point getConnectionPoint(Direction pDirection);

	/**
     * Lays out the node and its children.
     * @param pGraph the ambient graph
	 */
	void layout(Graph pGraph);

	/**
	 * @return A clone of the node.
	 */
	Node clone();
	
	/**
	 * @return The view for this node. TODO remove default
	 */
	default NodeView view()
	{
		assert false;
		return null;
	}
}
