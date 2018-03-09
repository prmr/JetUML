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
package ca.mcgill.cs.jetuml.graph.edges;

import ca.mcgill.cs.jetuml.graph.AbstractGraphElement;
import ca.mcgill.cs.jetuml.graph.Edge2;
import ca.mcgill.cs.jetuml.graph.Graph2;
import ca.mcgill.cs.jetuml.graph.Node2;
import ca.mcgill.cs.jetuml.views.edges.EdgeView2;

/**
 * Abstract edge in the new hierarchy.
 * 
 * @author Martin P. Robillard
 */
public abstract class AbstractEdge2 extends AbstractGraphElement implements Edge2
{
	protected EdgeView2 aView;
	private Node2 aStart;
	private Node2 aEnd;
	private Graph2 aGraph;
	
	/**
	 * Calls an abstract delegate to generate the view for this edge.
	 */
	protected AbstractEdge2()
	{
		aView = generateView();
	}
	
	@Override
	public void connect(Node2 pStart, Node2 pEnd, Graph2 pGraph)
	{
		assert pStart != null && pEnd != null;
		aStart = pStart;
		aEnd = pEnd;
		aGraph = pGraph;		
	}

	@Override
	public Node2 getStart()
	{
		return aStart;
	}

	@Override
	public Node2 getEnd()
	{
		return aEnd;
	}

	@Override
	public Graph2 getGraph()
	{
		return aGraph;
	}

	/**
	 * Generates a view for this edge. Because of cloning, this cannot
	 * be done in the constructor, because when an edge is clone a new 
	 * wrapper view must be produced for the clone.
	 * 
	 * @return The view that wraps this edge.
	 */
	protected abstract EdgeView2 generateView();
	
	@Override
	public AbstractEdge2 clone()
	{
		AbstractEdge2 clone = (AbstractEdge2) super.clone();
		clone.aView = clone.generateView();
		return clone;
	}
	
	@Override
	public EdgeView2 view()
	{
		return aView;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " " + getStart() + " -> " + getEnd();
	}
}
