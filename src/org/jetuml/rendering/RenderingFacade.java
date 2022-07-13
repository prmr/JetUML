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
import java.util.Optional;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;

/**
 * Meant as a single access point for all services that require rendering
 * a diagram and its elements.
 */
public class RenderingFacade
{
	private static IdentityHashMap<DiagramType, DiagramRenderer> 
		aDiagramRenderers = new IdentityHashMap<>();
	
	private static Optional<Diagram> aActiveDiagram = Optional.empty();
	
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
	
//	/**
//	 * Gets the smallest rectangle enclosing the diagram.
//	 * 
//	 * @param pDiagram The diagram to query
//	 * @return The bounding rectangle
//	 * @pre pDiagram != null
//	 */
//	public static Rectangle getBounds(Diagram pDiagram)
//	{
//		assert pDiagram != null;
//		return aDiagramRenderers.get(pDiagram.getType()).getBounds();
//	}
	
//	/**
//	 * Gets the smallest rectangle that bounds the element. The bounding rectangle contains all labels.
//	 * 
//	 * @param pElement The element whose bounds we wish to compute.
//	 * @return The bounding rectangle
//	 * @pre pElement != null
//	 */
//	public static Rectangle getBounds(DiagramElement pElement)
//	{
//		assert pElement != null;
//		return aDiagramRenderers.get(aActiveDiagram.get().getType()).getBounds(pElement);
//	}
}
