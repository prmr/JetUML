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
package ca.mcgill.cs.jetuml.persistence;

import java.util.HashMap;
import java.util.Iterator;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Node;

/**
 * Base class for serialization and deserialization contexts. A context 
 * is a mapping between nodes and arbitrary identifiers. The only constraint
 * on identifiers is that they consistently preserve mapping between objects and
 * their identity.
 */
public abstract class AbstractContext implements Iterable<Node>
{
	protected final HashMap<Node, Integer> aNodes = new HashMap<>();
	private final Diagram aGraph;
	
	/**
	 * Initializes the context with a graph.
	 * 
	 * @param pGraph The graph that corresponds to the context.
	 * @pre pGraph != null.
	 */
	protected AbstractContext(Diagram pGraph)
	{
		assert pGraph != null;
		aGraph = pGraph;
	}
	
	/**
	 * @return The graph associated with this context. Never null.
	 */
	public Diagram getGraph()
	{
		return aGraph;
	}
	
	/**
	 * @param pNode The node to check.
	 * @return The id for the node.
	 * @pre pNode != null
	 * @pre pNode is in the map.
	 */
	public int getId(Node pNode)
	{
		assert pNode != null;
		assert aNodes.containsKey(pNode);
		return aNodes.get(pNode);
	}
	
	@Override
	public Iterator<Node> iterator()
	{
		return aNodes.keySet().iterator();
	}
}
