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
 * Represents the removal of a node from the graph.
 * 
 * @author Martin P. Robillard
 */
public class DeleteNodeCommand extends GraphElementRelatedCommand
{
	private double aX;
	private double aY;
	
	/**
	 * Creates the command.
	 * @param pGraph The graph the node was removed from.
	 * @param pNode The node removed.
	 */
	public DeleteNodeCommand(Graph pGraph, Node pNode)
	{
		super( pGraph, pNode );
		aX = pNode.getBounds().getMinX();
		aY = pNode.getBounds().getMinY();
	}
	
	/**
	 * Undoes the command and adds/deletes the node.
	 */
	public void undo() 
	{
		aGraph.addNode((Node)aElement, new Point.Double(aX, aY));
		aGraph.layout();
	}

	/**
	 * Performs the command and adds/deletes the node.
	 */
	public void execute() 
	{
		aGraph.removeNode((Node)aElement);
		aGraph.layout();
	}
}
