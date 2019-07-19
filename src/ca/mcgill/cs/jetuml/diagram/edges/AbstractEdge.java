/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.diagram.edges;

import ca.mcgill.cs.jetuml.diagram.AbstractDiagramElement;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.SequenceDiagram;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.views.nodes.CallNodeView;

/**
 * Groups the functionality common to all edges.
 */
public abstract class AbstractEdge extends AbstractDiagramElement implements Edge
{
	private Node aStart;
	private Node aEnd;
	private Diagram aDiagram;
	
	@Override
	public void connect(Node pStart, Node pEnd, Diagram pDiagram)
	{
		assert pStart != null && pEnd != null;
		aStart = pStart;
		aEnd = pEnd;
		aDiagram = pDiagram;
		// Special case: CallNodeViews need to have a reference to the diagram.
		if( pStart instanceof CallNode )
		{
			((CallNodeView)pStart.view()).setDiagram((SequenceDiagram) pDiagram);
		}
		if( pEnd instanceof CallNode )
		{
			((CallNodeView)pEnd.view()).setDiagram((SequenceDiagram) pDiagram);
		}
	}

	@Override
	public Node getStart()
	{
		return aStart;
	}

	@Override
	public Node getEnd()
	{
		return aEnd;
	}

	@Override
	public Diagram getDiagram()
	{
		return aDiagram;
	}

	@Override
	public AbstractEdge clone()
	{
		AbstractEdge clone = (AbstractEdge) super.clone();
		return clone;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " " + getStart() + " -> " + getEnd();
	}
}
