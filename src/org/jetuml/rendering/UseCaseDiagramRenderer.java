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
import org.jetuml.viewers.edges.UseCaseAssociationEdgeViewer;
import org.jetuml.viewers.edges.UseCaseDependencyEdgeViewer;
import org.jetuml.viewers.edges.UseCaseGeneralizationEdgeViewer;
import org.jetuml.viewers.nodes.ActorNodeViewer;
import org.jetuml.viewers.nodes.UseCaseNodeViewer;

/**
 * The renderer for use case diagrams.
 */
public final class UseCaseDiagramRenderer extends AbstractDiagramRenderer
{
	public UseCaseDiagramRenderer(Diagram pDiagram)
	{
		super(pDiagram);
		addElementRenderer(ActorNode.class, new ActorNodeViewer());
		addElementRenderer(UseCaseNode.class, new UseCaseNodeViewer());
		addElementRenderer(UseCaseAssociationEdge.class, new UseCaseAssociationEdgeViewer());
		addElementRenderer(UseCaseGeneralizationEdge.class, new UseCaseGeneralizationEdgeViewer());
		addElementRenderer(UseCaseDependencyEdge.class, new UseCaseDependencyEdgeViewer());
	}
}
