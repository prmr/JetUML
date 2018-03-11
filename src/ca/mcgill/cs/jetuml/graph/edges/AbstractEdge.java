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
import ca.mcgill.cs.jetuml.graph.Edge;
import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.Graph2;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.views.edges.EdgeView;
import ca.mcgill.cs.jetuml.views.edges.EdgeView2;

/**
 * Abstract edge in the new hierarchy.
 * 
 * @author Martin P. Robillard
 */
public abstract class AbstractEdge extends AbstractGraphElement implements Edge
{
	protected EdgeView aView;
	protected EdgeView2 aView2;
	private Node aStart;
	private Node aEnd;
	private Graph aGraph;
	private Graph2 aGraph2;
	
	/**
	 * Calls an abstract delegate to generate the view for this edge.
	 */
	protected AbstractEdge()
	{
		aView = generateView();
	}
	
	@Override
	public void connect(Node pStart, Node pEnd, Graph pGraph)
	{
		assert pStart != null && pEnd != null;
		aStart = pStart;
		aEnd = pEnd;
		aGraph = pGraph;		
	}
	
	@Override
	public void connect2(Node pStart, Node pEnd, Graph2 pGraph)
	{
		assert pStart != null && pEnd != null;
		aStart = pStart;
		aEnd = pEnd;
		aGraph2 = pGraph;		
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
	public Graph getGraph()
	{
		return aGraph;
	}
	
	@Override
	public Graph2 getGraph2()
	{
		return aGraph2;
	}

	/**
	 * Generates a view for this edge. Because of cloning, this cannot
	 * be done in the constructor, because when an edge is clone a new 
	 * wrapper view must be produced for the clone.
	 * 
	 * @return The view that wraps this edge.
	 */
	protected abstract EdgeView generateView();
	
	/**
	 * Generates a view for this edge. Because of cloning, this cannot
	 * be done in the constructor, because when an edge is clone a new 
	 * wrapper view must be produced for the clone.
	 * 
	 * @return The view that wraps this edge.
	 */
	protected abstract EdgeView2 generateView2();
	
	@Override
	public AbstractEdge clone()
	{
		AbstractEdge clone = (AbstractEdge) super.clone();
		clone.aView = clone.generateView();
		clone.aView2 = clone.generateView2();
		return clone;
	}
	
	@Override
	public EdgeView view()
	{
		return aView;
	}
	
	@Override
	public EdgeView2 view2()
	{
		return aView2;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + " " + getStart() + " -> " + getEnd();
	}
}
