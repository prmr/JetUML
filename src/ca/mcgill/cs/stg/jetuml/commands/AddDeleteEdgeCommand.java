/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 by the contributors of the JetUML project.
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
package ca.mcgill.cs.stg.jetuml.commands;

import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

/**
 * Stores the addition/removal of a node from the graph.
 * @author EJBQ
 */
public class AddDeleteEdgeCommand implements Command
{
	private Edge aEdge;
	private Graph aGraph;
	private Node aP1;
	private Node aP2;
	private boolean aAdding; //true for adding, false for deleting
	
	/**
	 * Creates the command.
	 * @param pGraph The panel to add/delete the edge
	 * @param pEdge The edge to be added/deleted
	 * @param pAdding True when adding, false when deleting
	 */
	public AddDeleteEdgeCommand(Graph pGraph, Edge pEdge, boolean pAdding)
	{
		aGraph = pGraph;
		aEdge = pEdge;
		aP1 = aEdge.getStart();
		aP2 = aEdge.getEnd();
		aAdding = pAdding;
	}
	
	/**
	 * Undoes the command and adds/deletes the edge.
	 */
	public void undo() 
	{
		if(aAdding)
		{
			delete();
		}
		else
		{
			add();
		}
	}

	/**
	 * Performs the command and adds/deletes the edge.
	 */
	public void execute() 
	{
		if(aAdding)
		{
			add();
		}
		else
		{
			delete();
		}
	}

	/**
	 * Removes the node from the graph.
	 */
	private void delete() 
	{
		aGraph.removeEdge(aEdge);
		aGraph.layout();
	}
	
	/**
	 * Adds the edge to the graph at the points in its start and end node properties.
	 */
	private void add() 
	{
		// MPR Uses the decoder version of "connect" because the layout was not done 
		// at this point, so the strategy for finding nodes based on their coordinates is not robust.
//		Point.Double n1Point = new Point.Double();
//		n1Point.setLocation(aP1.getBounds().getX() + 1, aP1.getBounds().getY() + 1);
//		Point.Double n2Point = new Point.Double();
//		n2Point.setLocation(aP2.getBounds().getX() + 1, aP2.getBounds().getY() + 1);
//		aGraph.connect(aEdge, n1Point, n2Point);
		aGraph.connect(aEdge, aP1, aP2);
		aGraph.layout();
	}
	
}
