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
import org.jetuml.diagram.edges.StateTransitionEdge;
import org.jetuml.diagram.nodes.FinalStateNode;
import org.jetuml.diagram.nodes.InitialStateNode;
import org.jetuml.diagram.nodes.StateNode;
import org.jetuml.rendering.edges.StateTransitionEdgeRenderer;
import org.jetuml.viewers.nodes.CircularStateNodeViewer;
import org.jetuml.viewers.nodes.StateNodeViewer;

/**
 * The renderer for state diagrams.
 */
public final class StateDiagramRenderer extends AbstractDiagramRenderer
{
	public StateDiagramRenderer(Diagram pDiagram)
	{
		super(pDiagram);
		addElementRenderer(FinalStateNode.class, new CircularStateNodeViewer(this, true));
		addElementRenderer(InitialStateNode.class, new CircularStateNodeViewer(this, false));
		addElementRenderer(StateNode.class, new StateNodeViewer(this));
		addElementRenderer(StateTransitionEdge.class, new StateTransitionEdgeRenderer(this));
	}
}