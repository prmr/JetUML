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
package org.jetuml.viewers.edges;

import org.jetuml.diagram.Edge;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;

import javafx.scene.canvas.GraphicsContext;

/**
 * Abstract strategy that describes objects that can draw and
 * compute various geometric properties of edges.
 */
public interface EdgeViewer extends DiagramElementRenderer
{
	/**
     * Gets the smallest rectangle that bounds pEdge.
     * The bounding rectangle contains all labels.
     * @param pEdge The edge whose bounds we wish to compute.
     * @return the bounding rectangle
     * @pre pEdge != null
   	 */
	Rectangle getBounds(Edge pEdge);
	
	/**
     * Draws pEdge.
     * @param pEdge The edge to draw.
     * @param pGraphics the graphics context
     * @pre pEdge != null
	 */
   	void draw(Edge pEdge, GraphicsContext pGraphics);
   	
   	/**
     * Draw selection handles around pEdge.
     * @param pEdge The target edge
     * @param pGraphics the graphics context
     * @pre pEdge != null && pGraphics != null
	 */
   	void drawSelectionHandles(Edge pEdge, GraphicsContext pGraphics);
   	
   	/**
     * Tests whether pEdge contains a point.
     * @param pEdge the edge to test
     * @param pPoint the point to test
     * @return true if this element contains aPoint
     */
   	boolean contains(Edge pEdge, Point pPoint);
   	
   	/**
     * Gets the points at which pEdge is connected to
     * its nodes.
     * @param pEdge The target edge
     * @return a line joining the two connection points
     * @pre pEdge != null
     * 
     */
   	Line getConnectionPoints(Edge pEdge);
}
