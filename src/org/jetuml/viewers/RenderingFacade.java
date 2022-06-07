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
package org.jetuml.viewers;

import java.util.Iterator;

import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.geom.Rectangle;
import org.jetuml.viewers.edges.EdgeViewerRegistry;
import org.jetuml.viewers.nodes.NodeViewerRegistry;

import javafx.scene.canvas.Canvas;

/**
 * Meant as a single access point for all services that require rendering
 * a diagram and its elements.
 */
public class RenderingFacade
{
	/**
	 * Convenience method for creating the icon for either a node
	 * or an edge.
	 * 
	 * @param pElement The element for which we want an icon
	 * @return An icon that represents this element
	 * @pre pElement != null
	 */
	public static Canvas createIcon(DiagramElement pElement)
	{
		assert pElement != null;
		if( pElement instanceof Node )
		{
			return NodeViewerRegistry.createIcon((Node)pElement);
		}
		else
		{
			assert pElement instanceof Edge;
			return EdgeViewerRegistry.createIcon((Edge)pElement);
		}
	}
	
	/**
	 * @param pElements The elements whose bounds we are interested in. 
	 * @return The rectangle that bounds all elements in pElements, excluding their parent node.
	 * @pre pElements != null
	 * @pre pElements has at least one element.
	 */
	public static Rectangle getBounds(Iterable<DiagramElement> pElements)
	{
		assert pElements != null;
		assert pElements.iterator().hasNext();
		Iterator<DiagramElement> elements = pElements.iterator();
		Rectangle bounds = DiagramViewer.getBounds(elements.next());
		while( elements.hasNext() )
		{
			bounds = bounds.add(DiagramViewer.getBounds(elements.next()));
		}
		return bounds;
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
		Rectangle bounds = DiagramViewer.getBounds(next);
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
			return pBounds.add(DiagramViewer.getBounds(pElement));
		}
	}
}
