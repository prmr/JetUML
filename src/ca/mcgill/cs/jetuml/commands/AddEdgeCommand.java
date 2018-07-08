/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.commands;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Edge;

/**
 * Represents the addition of an edge to the graph.
 */
public class AddEdgeCommand extends GraphElementRelatedCommand
{
	/**
	 * Creates the command.
	 * @param pGraph The target graph.
	 * @param pEdge The related edge.
	 */
	public AddEdgeCommand(Diagram pGraph, Edge pEdge)
	{
		super(pGraph, pEdge);
	}
	
	/**
	 * Undoes the command and adds/deletes the edge.
	 */
	public void undo() 
	{
		assert aElement instanceof Edge;
		aGraph.builder().removeEdge((Edge)aElement);
	}

	/**
	 * Performs the command and adds/deletes the edge.
	 */
	public void execute() 
	{
		assert aElement instanceof Edge;
		aGraph.insertEdge((Edge)aElement);
	}
}
