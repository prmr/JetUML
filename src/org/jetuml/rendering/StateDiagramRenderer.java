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

import org.jetuml.annotations.Singleton;
import org.jetuml.diagram.edges.StateTransitionEdge;
import org.jetuml.diagram.nodes.FinalStateNode;
import org.jetuml.diagram.nodes.InitialStateNode;
import org.jetuml.diagram.nodes.StateNode;
import org.jetuml.viewers.edges.StateTransitionEdgeViewer;
import org.jetuml.viewers.nodes.CircularStateNodeViewer;
import org.jetuml.viewers.nodes.StateNodeViewer;

/**
 * The renderer for state diagrams.
 */
@Singleton
public final class StateDiagramRenderer extends AbstractDiagramRenderer
{
	public static final StateDiagramRenderer INSTANCE = new StateDiagramRenderer();
	
	private StateDiagramRenderer()
	{
		addElementRenderer(FinalStateNode.class, new CircularStateNodeViewer(true));
		addElementRenderer(InitialStateNode.class, new CircularStateNodeViewer(false));
		addElementRenderer(StateNode.class, new StateNodeViewer());
		addElementRenderer(StateTransitionEdge.class, new StateTransitionEdgeViewer());
	}
}