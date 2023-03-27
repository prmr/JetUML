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

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.edges.UseCaseAssociationEdge;
import org.jetuml.diagram.edges.UseCaseDependencyEdge;
import org.jetuml.diagram.edges.UseCaseGeneralizationEdge;
import org.jetuml.diagram.nodes.ActorNode;
import org.jetuml.diagram.nodes.UseCaseNode;
import org.jetuml.rendering.edges.UseCaseAssociationEdgeRenderer;
import org.jetuml.rendering.edges.UseCaseDependencyEdgeRenderer;
import org.jetuml.rendering.edges.UseCaseGeneralizationEdgeRenderer;
import org.jetuml.rendering.nodes.ActorNodeRenderer;
import org.jetuml.rendering.nodes.UseCaseNodeRenderer;

/**
 * The renderer for use case diagrams.
 */
public final class UseCaseDiagramRenderer extends AbstractDiagramRenderer
{
	/**
	 * @param pDiagram The diagram being rendered.
	 */
	public UseCaseDiagramRenderer(Diagram pDiagram)
	{
		super(pDiagram);
		addElementRenderer(ActorNode.class, new ActorNodeRenderer(this));
		addElementRenderer(UseCaseNode.class, new UseCaseNodeRenderer(this));
		addElementRenderer(UseCaseAssociationEdge.class, new UseCaseAssociationEdgeRenderer(this));
		addElementRenderer(UseCaseGeneralizationEdge.class, new UseCaseGeneralizationEdgeRenderer(this));
		addElementRenderer(UseCaseDependencyEdge.class, new UseCaseDependencyEdgeRenderer(this));
	}
}
