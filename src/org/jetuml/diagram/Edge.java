/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2025 by McGill University.
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

package org.jetuml.diagram;

/**
 * An edge in a diagram.
 */
public interface Edge extends DiagramElement
{
   /**
    * Connect this edge to two nodes.
    * @param pStart the starting node
    * @param pEnd the end node
  	*/
	void connect(Node pStart, Node pEnd);

   	/**
     * @return The start node for this edge.
     */
   	Node start();

   	/**
     * @return The end node for this edge.
   	 */
   	Node end();

   	/**
   	 * @return A clone of this edge, with shallow cloning
   	 *     of the start and end nodes (i.e., the start and end 
   	 *     nodes are not cloned).
   	 */
   	Edge clone();
}

