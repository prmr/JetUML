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
import ca.mcgill.cs.jetuml.views.edges.EdgeView;
import ca.mcgill.cs.jetuml.views.nodes.CallNodeView;

/**
 * Abstract edge in the new hierarchy.
 */
public abstract class AbstractEdge extends AbstractDiagramElement implements Edge
{
	protected EdgeView aView;
	private Node aStart;
	private Node aEnd;
	private Diagram aDiagram;
	
	/**
	 * Calls an abstract delegate to generate the view for this edge.
	 */
	protected AbstractEdge()
	{
		aView = generateView();
	}
	
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

	/**
	 * Generates a view for this edge. Because of cloning, this cannot
	 * be done in the constructor, because when an edge is clone a new 
	 * wrapper view must be produced for the clone.
	 * 
	 * @return The view that wraps this edge.
	 */
	protected abstract EdgeView generateView();
	
	@Override
	public AbstractEdge clone()
	{
		AbstractEdge clone = (AbstractEdge) super.clone();
		clone.aView = clone.generateView();
		return clone;
	}
	
	@Override
	public EdgeView view()
	{
		return aView;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " " + getStart() + " -> " + getEnd();
	}
}
