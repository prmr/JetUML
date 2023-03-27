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
import org.jetuml.rendering.nodes.CircularStateNodeRenderer;
import org.jetuml.rendering.nodes.StateNodeRenderer;

/**
 * The renderer for state diagrams.
 */
public final class StateDiagramRenderer extends AbstractDiagramRenderer
{
	/**
	 * @param pDiagram The diagram being rendered.
	 */
	public StateDiagramRenderer(Diagram pDiagram)
	{
		super(pDiagram);
		addElementRenderer(FinalStateNode.class, new CircularStateNodeRenderer(this, true));
		addElementRenderer(InitialStateNode.class, new CircularStateNodeRenderer(this, false));
		addElementRenderer(StateNode.class, new StateNodeRenderer(this));
		addElementRenderer(StateTransitionEdge.class, new StateTransitionEdgeRenderer(this));
	}
}