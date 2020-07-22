/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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
 *******************************************************************************/
package ca.mcgill.cs.jetuml.persistence;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Node;

/**
 * A serialization context automatically finds all the nodes
 * in a diagram, including children nodes, and creates a new map between
 * nodes and identifiers.
 */
public class SerializationContext extends AbstractContext
{
	/**
	 * Automatically creates the map between nodes in pDiagram
	 * and fresh identifiers.
	 * 
	 * @param pDiagram The diagram to load into the context.
	 * @pre pDiagram != null.
	 */
	public SerializationContext(Diagram pDiagram)
	{
		super(pDiagram);
		getAllNodes(pDiagram);
	}
	
	/**
	 * Adds a node to the context if it is not already there. Its identifier
	 * is automatically defined. If the node is already in the 
	 * context, it is not added.
	 * 
	 * @param pNode The node to add.
	 * @pre pNode != null;
	 * @pre !aNodes.containsKey(pNode)
	 */
	private void addNode(Node pNode)
	{
		assert pNode != null;
		assert !aNodes.containsKey(pNode);
		aNodes.put(pNode, aNodes.size());
	}
	
	private void getAllNodes(Diagram pDiagram)
	{
		for( Node node : pDiagram.rootNodes() )
		{
			addNode(node);
			if( node.getChildren().size() > 0 )
			{
				addChildren(node);
			}
		}
	}
	
	private void addChildren(Node pParent)
	{
		for( Node node : pParent.getChildren() )
		{
			addNode(node);
			if( node.getChildren().size() > 0  )
			{
				addChildren(node);
			}
		}
	}
}
