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

package ca.mcgill.cs.stg.jetuml.graph;

import java.awt.Graphics2D;

import ca.mcgill.cs.stg.jetuml.geom.Direction;
import ca.mcgill.cs.stg.jetuml.geom.Point;

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
     * @param pGraphics2D the graphics context
	 */
	void layout(Graph pGraph, Graphics2D pGraphics2D);

	/**
	 * @return A clone of the node.
	 */
	Node clone();
}
