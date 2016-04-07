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

import java.awt.geom.Point2D;

import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;

/**
 * Represents a command that involves a node.
 * 
 * @author Martin P. Robillard
 */
abstract class NodeRelatedCommand extends GraphElementRelatedCommand
{
	protected Point2D aPosition;
	
	/**
	 * Creates the command.
	 * @param pGraph The graph the node was added to.
	 * @param pNode The node added.
	 */
	protected NodeRelatedCommand(Graph pGraph, Node pNode)
	{
		super(pGraph, pNode);
		aPosition = new Point2D.Double(pNode.getBounds().getMinX(), pNode.getBounds().getMinY());
	}
	
	protected Node getNode()
	{
		return (Node) aElement;
	}
}
