/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2022 by McGill University.
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
 ******************************************************************************/
package org.jetuml.rendering;

import java.util.Optional;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.Direction;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object responsible for computing the geometry of a diagram. This class is 
 * intended to be specialized by subclasses that correspond to different diagram
 * types. 
 * 
 * A rendering pass starts with a call to draw(...). This results in the complete
 * geometry of the diagram being computed, and some of the computations being cached.
 * Subsequent calls to query method will use the cached computations. 
 * 
 * A single instance of each specialized renderer is needed as long as the geometry
 * is recomputed with a call to draw before any querying of the diagram geometry.
 */
public interface DiagramRenderer
{
	/**
	 * Computes the geometry of the diagram and draws the diagram onto 
	 * the graphics context.
	 * 
	 * @param pGraphics The graphics context where the diagram should be drawn.
	 * @pre pGraphics != null.
	 */
	void draw(GraphicsContext pGraphics);
	
	/**
     * Draws the element.
     * @param pElement The element to draw.
     * @param pGraphics the graphics context
     * @pre pElement != null
	 */
   	void draw(DiagramElement pElement, GraphicsContext pGraphics);
	
	/**
	 * Returns the edge underneath the given point, if it exists.
	 * 
	 * @param pPoint a point
	 * @return An edge containing pPoint or Optional.empty() if no edge is under pPoint
	 * @pre pDiagram != null && pPoint != null
	 */
	Optional<Edge> edgeAt(Point pPoint);
		
	/**
     * Returns the node underneath the given point, if it exists. Always returns
     * the deepest child and the last one in a list.
     * @param pPoint A point
     * @return a node containing pPoint or Optional.empty() if no node is under pPoint
     * @pre pPoint != null.
     */
	Optional<Node> nodeAt(Point pPoint);
	
	/**
	 * Gets the smallest rectangle enclosing the diagram.
	 * 
	 * @return The bounding rectangle
	 */
	Rectangle getBounds();
	
	/**
	 * Tests whether pElement contains a point.
	 * 
	 * @param pElement The element to test
	 * @param pPoint The point to test
	 * @return true if this element contains aPoint
	 */
	boolean contains(DiagramElement pElement, Point pPoint);
	
	/**
	 * Creates the icon for the target diagram element.
	 * 
	 * @param pElement The element for which we want an icon
	 * @return An icon that represents this element
	 * @pre pElement != null
	 */
	Canvas createIcon(DiagramElement pElement);
	
	/**
	 * Draw selection handles around the element.
	 * 
	 * @param pElement The target element
	 * @param pGraphics The graphics context
	 * @pre pElement != null && pGraphics != null
	 */
	void drawSelectionHandles(DiagramElement pElement, GraphicsContext pGraphics);

	/**
	 * Gets the smallest rectangle that bounds the element. The bounding rectangle contains all labels.
	 * 
	 * @param pElement The element whose bounds we wish to compute.
	 * @return The bounding rectangle
	 * @pre pElement != null
	 */
	Rectangle getBounds(DiagramElement pElement);

	/**
	 * Gets the points at which pEdge is connected to its nodes.
	 * 
	 * @param pEdge The target edge
	 * @return a line joining the two connection points
	 * @pre pEdge != null
	 * 
	 */
	Line getConnectionPoints(Edge pEdge);
	
	/**
	 * Gets the points at which pNode is connected to its nodes.
	 * 
	 * @param pNode The target node
	 * @param pDirection The desired direction.
	 * @return A connection point on the node.
	 * @pre pNode != null && pDirection != null
	 * 
	 */
	Point getConnectionPoints(Node pNode, Direction pDirection);
	
	/**
     * Finds a node that contains the given point, if this is a node that can be 
     * selected. The difference between this method and nodeAt is that it is specialized for
     * nodes that can be selected by the users, whereas nodeAt is also used for edge creation.
     * By default, this method has the same behavior as nodeAt.
     * @param pPoint A point
     * @return a node containing pPoint or null if no nodes contain pPoint
     * @pre pDiagram != null && pPoint != null.
     */
	Optional<Node> selectableNodeAt(Point pPoint);
	
   	/**
   	 * @return The diagram wrapped by this object.
   	 */
   	Diagram diagram();
   	
   	/**
   	 * @param pClass The class to obtain a renderer for.
   	 * @return The diagram element renderer for a given diagram element class, that is managed
   	 * by this renderer.
   	 */
   	DiagramElementRenderer rendererFor(Class<? extends DiagramElement> pClass);
   	
   	/**
	 * @param pElements The elements whose bounds we are interested in. 
	 * @return A rectangle that represents the bounding box of the 
	 *     entire selection including the bounds of their parent nodes.
	 * @pre pElements != null
	 * @pre pElements has at least one element.
	 */
	Rectangle getBoundsIncludingParents(Iterable<DiagramElement> pElements);
	
   	/**
	 * @param pElements The elements whose bounds we are interested in. 
	 * @return A rectangle that represents the bounding box of the 
	 *     entire selection but exclusing the bounds of their parent nodes.
	 * @pre pElements != null
	 * @pre pElements.iterator().hasNext()
	 */
	Rectangle getBoundsNotIncludingParents(Iterable<DiagramElement> pElements);
	
	/**
	 * @param pNode The node of interest.
	 * @return The dimensions of the default version of this node, when it's just been
	 * added to a diagram.
	 */
	Dimension getDefaultDimension(Node pNode);
}
