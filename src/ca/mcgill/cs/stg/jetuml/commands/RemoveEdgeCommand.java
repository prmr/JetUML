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

import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;

/**
 * Represents the removal of an edge to the graph.
 * 
 * @author Martin P. Robillard
 */
public class RemoveEdgeCommand extends GraphElementRelatedCommand
{
	/**
	 * Creates the command.
	 * @param pGraph The target graph.
	 * @param pEdge The related edge.
	 */
	public RemoveEdgeCommand(Graph pGraph, Edge pEdge)
	{
		super(pGraph, pEdge);
	}
	
	/**
	 * Undoes the command and adds/deletes the edge.
	 */
	public void undo() 
	{
		assert aElement instanceof Edge;
		aGraph.insertEdge((Edge)aElement);
	}

	/**
	 * Performs the command and adds/deletes the edge.
	 */
	public void execute() 
	{
		assert aElement instanceof Edge;
		aGraph.removeEdge((Edge)aElement);
	}
}
