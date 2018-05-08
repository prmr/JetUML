/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2018 by the contributors of the JetUML project.
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

import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.Node;

/**
 * Stores the moving of a node.
 * @author EJBQ
 */
public class MoveCommand implements Command
{
	private Node aNode;
	private Graph aGraph;
	private int aDX;
	private int aDY;
	
	/**
	 * Creates the command.
	 * @param pGraph The panel being moved on
	 * @param pNode The node being moved
	 * @param pDX The amount moved horizontally
	 * @param pDY The amount moved vertically
	 */
	public MoveCommand(Graph pGraph, Node pNode, int pDX, int pDY)
	{
		aGraph = pGraph;
		aNode = pNode;
		aDX = pDX;
		aDY = pDY;
	}
	
	/**
	 * Undoes the command and moves the node back where it came from.
	 */
	public void undo() 
	{
		aNode.translate(-aDX, -aDY);
		aGraph.requestLayout();
	}

	/**
	 * Performs the command and moves the node.
	 */
	public void execute() 
	{
		aNode.translate(aDX, aDY);
		aGraph.requestLayout();
	}

}
