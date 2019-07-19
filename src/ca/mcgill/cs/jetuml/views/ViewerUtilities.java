/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.views;

import static ca.mcgill.cs.jetuml.viewers.edges.EdgeViewerRegistry.EDGE_VIEWER_REGISTRY;

import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Convenience methods to obtain viewer services.
 */
public final class ViewerUtilities
{
	private ViewerUtilities() {}
	
	/**
	 * Draws selection handles around pElement.
	 * 
	 * @param pElement The diagram element to select.
	 * @param pContext The graphics context.
	 * @pre pElement != null && pContext != null
	 */
	public static void drawSelectionHandles(DiagramElement pElement, GraphicsContext pContext)
	{
		assert pElement != null && pContext != null;
		if( pElement instanceof Node )
		{
			((Node)pElement).view().drawSelectionHandles(pContext);
		}
		else
		{
			assert pElement instanceof Edge;
			EDGE_VIEWER_REGISTRY.viewerFor((Edge)pElement).drawSelectionHandles((Edge)pElement, pContext);
		}
	}
	
	/**
	 * Obtains the bounds for an element.
	 * 
	 * @param pElement The element whose bounds we want
	 * @return The bounds for this element.
	 * @pre pElement != null
	 */
	public static Rectangle getBounds(DiagramElement pElement)
	{
		assert pElement != null;
		if( pElement instanceof Node )
		{
			return ((Node)pElement).view().getBounds();
		}
		else
		{
			assert pElement instanceof Edge;
			return EDGE_VIEWER_REGISTRY.viewerFor((Edge)pElement).getBounds((Edge)pElement);
		}
	}
	
	/**
	 * @param pElement The element for which we want an icon
	 * @return An icon that represents this element
	 * @pre pElement != null
	 */
	public static Canvas createIcon(DiagramElement pElement)
	{
		assert pElement != null;
		if( pElement instanceof Node )
		{
			return ((Node)pElement).view().createIcon();
		}
		else
		{
			assert pElement instanceof Edge;
			return EDGE_VIEWER_REGISTRY.viewerFor((Edge)pElement).createIcon((Edge)pElement);
		}
	}
}
