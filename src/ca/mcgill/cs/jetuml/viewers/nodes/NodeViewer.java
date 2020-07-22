/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.viewers.nodes;

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Abstract strategy that describes objects that can draw and
 * compute various geometric properties of nodes.
 */
public interface NodeViewer
{
	/**
     * Gets the smallest rectangle that bounds this element.
     * The bounding rectangle contains all labels.
     * @param pNode The node whose bounds we want.
     * @pre pNode != null
     * @return the bounding rectangle
   	 */
	Rectangle getBounds(Node pNode);
   	
	/**
     * Draw the element.
     * @param pNode The node to draw.
     * @param pGraphics the graphics context
     * @pre pNode != null && pGraphics != null
	 */
   	void draw(Node pNode, GraphicsContext pGraphics);
   	
   	/**
   	 * Returns an icon that represents the element.
   	 * @param pNode The node to create an icon for.
     * @return A canvas object on which the icon is painted.
     * @pre pNode != null
   	 */
   	Canvas createIcon(Node pNode);
   	
   	/**
     * Draw selection handles around the element.
     * @param pNode The node to draw selection handles around
     * @param pGraphics the graphics context
     * @pre pNode != null && pGraphics != null
	 */
   	void drawSelectionHandles(Node pNode, GraphicsContext pGraphics);
   	
   	/**
     * Tests whether the node contains a point.
     * @param pNode The node to test
     * @param pPoint the point to test
     * @return true if this element contains aPoint
     * @pre pNode != null && pPoint != null
     */
   	boolean contains(Node pNode, Point pPoint);
   	
   	/**
     * Get the best connection point to connect this node 
     * with another node. This should be a point on the boundary
     * of the shape of this node.
     * @param pNode The target node.
     * @param pDirection the direction from the center 
     *     of the bounding rectangle towards the boundary 
     * @return the recommended connection point
     * @pre pNode != null && pDirection != null
	 */
	Point getConnectionPoint(Node pNode, Direction pDirection);
}
