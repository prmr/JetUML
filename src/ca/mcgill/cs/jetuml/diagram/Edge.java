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

package ca.mcgill.cs.jetuml.diagram;

import ca.mcgill.cs.jetuml.views.edges.EdgeView;

/**
 * An edge in a diagram.
 */
public interface Edge extends DiagramElement
{
   /**
    * Connect this edge to two nodes.
    * @param pStart the starting node
    * @param pEnd the end node
    * @param pDiagram the graph where the two connected nodes 
    * exists. Can be null.
  	 */
  void connect(Node pStart, Node pEnd, Diagram pDiagram);

   	/**
     * @return The start node for this edge.
     */
   	Node getStart();

   	/**
     * @return The end node for this edge.
   	 */
   	Node getEnd();
   	
   	/**
   	 * @return The diagram that contains this edge.
   	 */
   	Diagram getDiagram();

   	/**
   	 * @return A clone of this edge, with shallow cloning
   	 * of the start and end nodes (i.e., the start and end 
   	 * nodes are not cloned).
   	 */
   	Edge clone();
   	
 	/**
   	 * @return The view for this edge.
   	 */
   	EdgeView view();
}

