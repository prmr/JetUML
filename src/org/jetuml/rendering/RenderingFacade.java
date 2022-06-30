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
		return aDiagramRenderers.get(pDiagram.getType()).edgeAt(pPoint);
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
		return aDiagramRenderers.get(pDiagram.getType()).getBounds();
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
