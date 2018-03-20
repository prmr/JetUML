/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2017 by the contributors of the JetUML project.
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

import ca.mcgill.cs.jetuml.graph.Graph2;
import ca.mcgill.cs.jetuml.graph.GraphElement;

/**
 * A command that involves a single graph element.
 * 
 * @author Martin P. Robillard
 */
abstract class GraphElementRelatedCommand2 implements Command
{
	protected GraphElement aElement;
	protected Graph2 aGraph;
	
	/**
	 * Creates the command.
	 * @param pGraph The target graph.
	 * @param pElement The related element
	 */
	protected GraphElementRelatedCommand2(Graph2 pGraph, GraphElement pElement)
	{
		aGraph = pGraph;
		aElement = pElement;
	}
}
