/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016 by the contributors of the JetUML project.
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
package ca.mcgill.cs.stg.jetuml.framework;

import ca.mcgill.cs.stg.jetuml.commands.AddDeleteEdgeCommand;
import ca.mcgill.cs.stg.jetuml.commands.AddDeleteNodeCommand;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

/**
 * @author EJBQ
 *
 */
public class GraphModificationListener 
{
	private UndoManager aUndoManager;

	/**
	 * Creates a new GraphModificationListener with an UndoManager.
	 * @param pUndo the UndoManager to be accessed
	 */
	public GraphModificationListener(UndoManager pUndo)
	{
		aUndoManager = pUndo;
	}
	
	/**
	 * Creates a new GraphModificationListener with an UndoManager.
	 */
	public GraphModificationListener()
	{
		aUndoManager = new UndoManager();
	}

	/**
	 * Keeps track of the addition of a node.
	 * @param pGraph The Panel to add the node to
	 * @param pNode The node to be added
	 */
	public void nodeAdded(Graph pGraph, Node pNode)
	{
		AddDeleteNodeCommand ac = new AddDeleteNodeCommand(pGraph, pNode, true);
		aUndoManager.add(ac);
	}

	/**
	 * Keeps track of the removal of a node.
	 * @param pGraph The Panel to remove the node from
	 * @param pNode The node to be removed
	 */
	public void nodeRemoved(Graph pGraph, Node pNode)
	{
		AddDeleteNodeCommand dc = new AddDeleteNodeCommand(pGraph, pNode, false);
		aUndoManager.add(dc);
	}

	/**
	 * Tracks the addition of an edge to the graph.
	 * @param pGraph The panel to be edited
	 * @param pEdge The edge being added
	 */
	public void edgeAdded(Graph pGraph, Edge pEdge)
	{
		AddDeleteEdgeCommand ac = new AddDeleteEdgeCommand(pGraph, pEdge, true);
		aUndoManager.add(ac);
	}

	/**
	 * Tracks the removal of an edge to the graph.
	 * @param pGraph The panel to be edited
	 * @param pEdge The edge being removed
	 */
	public void edgeRemoved(Graph pGraph, Edge pEdge)
	{
		AddDeleteEdgeCommand dc = new AddDeleteEdgeCommand(pGraph, pEdge, false);
		aUndoManager.add(dc);
	}

	/**
	 * Collects all coming calls into single undo - redo command.
	 */
	public void startCompoundListening() 
	{
		aUndoManager.startTracking();
	}
	
	/**
	 * Ends collecting all coming calls into single undo - redo command.
	 */
	public void endCompoundListening() 
	{
		aUndoManager.endTracking();
	}
}
