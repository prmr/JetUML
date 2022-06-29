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
 *******************************************************************************/
package org.jetuml.rendering;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Optional;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Meant as a single access point for all services that require rendering
 * a diagram and its elements.
 */
public class RenderingFacade
{
	private static IdentityHashMap<DiagramType, DiagramRenderer> 
		aDiagramRenderers = new IdentityHashMap<>();
	
	private static Optional<Diagram> aActiveDiagram = Optional.empty();
	
	private static DiagramType diagramType()
	{
		return aActiveDiagram.get().getType();
	}
	
	/**
	 * Caches the diagram to be rendered. All subsequent rendering operations
	 * will be assumed to target this diagram, until the next call to prepare.
	 * 
	 * @param pDiagram The diagram to prepare for rendering.
	 */
	public static void prepareFor(Diagram pDiagram)
	{
		assert pDiagram != null;
		aActiveDiagram = Optional.of(pDiagram);
		aDiagramRenderers.put(pDiagram.getType(), DiagramType.newRendererInstanceFor(pDiagram));	
	}
	
	/**
	 * Creates the icon for the target diagram element.
	 * 
	 * @param pElement The element for which we want an icon
	 * @return An icon that represents this element
	 * @pre pElement != null
	 */
	public static Canvas createIcon(DiagramType pDiagramType, DiagramElement pElement)
	{
		assert pElement != null;
		return aDiagramRenderers.get(pDiagramType).createIcon(pElement);
	}
	
	/**
	 * @param pElements The elements whose bounds we are interested in. 
	 * @return A rectangle that represents the bounding box of the 
	 *     entire selection including the bounds of their parent nodes.
	 * @pre pElements != null
	 * @pre pElements has at least one element.
	 */
	public static Rectangle getBoundsIncludingParents(Iterable<DiagramElement> pElements)
	{
		assert pElements != null;
		assert pElements.iterator().hasNext();
		Iterator<DiagramElement> elements = pElements.iterator();
		DiagramElement next = elements.next();
		Rectangle bounds = getBounds(next);
		bounds = addBounds(bounds, next);
		while( elements.hasNext() )
		{
			bounds = addBounds(bounds, elements.next());
		}
		return bounds;
	}
	
	// Recursively enlarge the current rectangle to include the selected DiagramElements
	private static Rectangle addBounds(Rectangle pBounds, DiagramElement pElement)
	{
		if( pElement instanceof Node && ((Node) pElement).hasParent())
		{
			return addBounds(pBounds, ((Node) pElement).getParent());
		}
		else
		{
			return pBounds.add(getBounds(pElement));
		}
	}
	
	/**
	 * Tests whether pElement contains a point.
	 * 
	 * @param pElement The element to test
	 * @param pPoint The point to test
	 * @return true if this element contains aPoint
	 */
	public static boolean contains(DiagramElement pElement, Point pPoint)
	{
		assert pElement != null;
		return aDiagramRenderers.get(diagramType()).contains(pElement, pPoint);
	}
	
	/**
	 * Draw selection handles around the element.
	 * 
	 * @param pElement The target element
	 * @param pGraphics The graphics context
	 * @pre pElement != null && pGraphics != null
	 */
	public static void drawSelectionHandles(DiagramElement pElement, GraphicsContext pGraphics)
	{
		assert pElement != null && pGraphics != null;
		aDiagramRenderers.get(diagramType()).drawSelectionHandles(pElement, pGraphics);
	}
	
	/**
	 * Gets the points at which pEdge is connected to its nodes.
	 * 
	 * @param pEdge The target edge
	 * @return a line joining the two connection points
	 * @pre pEdge != null
	 * 
	 */
	public static Line getConnectionPoints(Edge pEdge)
	{
		assert pEdge != null;
		return aDiagramRenderers.get(diagramType()).getConnectionPoints(pEdge);
	}
	
	/**
	 * Draws pDiagram onto pGraphics.
	 * 
	 * @param pGraphics the graphics context where the
	 *     diagram should be drawn.
	 * @param pDiagram the diagram to draw.
	 * @pre pDiagram != null && pGraphics != null.
	 */
	public static void draw(Diagram pDiagram, GraphicsContext pGraphics)
	{
		assert pDiagram != null && pGraphics != null;
		aDiagramRenderers.get(pDiagram.getType()).draw(pDiagram, pGraphics);
	}
	
	/**
	 * Returns the edge underneath the given point, if it exists.
	 * 
	 * @param pDiagram The diagram to query
	 * @param pPoint a point
	 * @return An edge containing pPoint or Optional.empty() if no edge is under pPoint
	 * @pre pDiagram != null && pPoint != null
	 */
	public static Optional<Edge> edgeAt(Diagram pDiagram, Point pPoint)
	{
		assert pDiagram != null && pPoint != null;
		return aDiagramRenderers.get(pDiagram.getType()).edgeAt(pDiagram, pPoint);
	}
	
	/**
     * Finds a node that contains the given point. Always returns
     * the deepest child and the last one in a list.
     * @param pDiagram The diagram to query.
     * @param pPoint A point
     * @return a node containing pPoint or null if no nodes contain pPoint
     * @pre pDiagram != null && pPoint != null.
     */
	public static Optional<Node> nodeAt(Diagram pDiagram, Point pPoint)
	{
		assert pDiagram != null && pPoint != null;
		return aDiagramRenderers.get(pDiagram.getType()).nodeAt(pDiagram, pPoint);
	}
	
	/**
	 * Gets the smallest rectangle enclosing the diagram.
	 * 
	 * @param pDiagram The diagram to query
	 * @return The bounding rectangle
	 * @pre pDiagram != null
	 */
	public static Rectangle getBounds(Diagram pDiagram)
	{
		assert pDiagram != null;
		return aDiagramRenderers.get(pDiagram.getType()).getBounds(pDiagram);
	}
	
	public static Optional<Node> selectableNodeAt(Diagram pDiagram, Point pPoint)
	{
		assert pDiagram != null;
		return aDiagramRenderers.get(pDiagram.getType()).selectableNodeAt(pDiagram, pPoint);
	}
	
	/**
	 * Gets the smallest rectangle that bounds the element. The bounding rectangle contains all labels.
	 * 
	 * @param pElement The element whose bounds we wish to compute.
	 * @return The bounding rectangle
	 * @pre pElement != null
	 */
	public static Rectangle getBounds(DiagramElement pElement)
	{
		assert pElement != null;
		return aDiagramRenderers.get(aActiveDiagram.get().getType()).getBounds(pElement);
	}
}
