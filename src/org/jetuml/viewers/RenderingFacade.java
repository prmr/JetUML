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
}
