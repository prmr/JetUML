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
package ca.mcgill.cs.stg.jetuml.commands;

import java.awt.Point;

import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

/**
 * Stores the addition/removal of a node from the graph.
 * @author EJBQ
 */
public class AddDeleteNodeCommand implements Command
{
	private Node aNode;
	private Graph aGraph;
	private double aX;
	private double aY;
	private boolean aAdding; //true for adding, false for deleting
	
	/**
	 * Creates the command.
	 * @param pGraph The panel to add/delete the node
	 * @param pNode The node to be added/deleted
	 * @param pAdding True when adding, false when deleting
	 */
	public AddDeleteNodeCommand(Graph pGraph, Node pNode, boolean pAdding)
	{
		aGraph = pGraph;
		aNode = pNode;
		aX = aNode.getBounds().getMinX();
		aY = aNode.getBounds().getMinY();
		aAdding = pAdding;
	}
	
	/**
	 * Undoes the command and adds/deletes the node.
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
	 * Performs the command and adds/deletes the node.
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
		aGraph.removeNode(aNode);
		aGraph.layout();
	}
	
	/**
	 * Adds the edge to the graph at the point in its properties.
	 */
	private void add() 
	{
		Point.Double point = new Point.Double(aX, aY);
		aGraph.add(aNode, point);
		aGraph.layout();
	}
	
}
