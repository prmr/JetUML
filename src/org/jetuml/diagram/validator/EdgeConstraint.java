/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2023 by McGill University.
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
package org.jetuml.diagram.validator;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;

/**
 * Constraint on how an edge is connected to nodes in a diagram.
 */
public interface EdgeConstraint 
{
	/**
	 * Determines if a constraint is satisfied.
	 * 
	 * @param pEdge The edge being validated.
	 * @param pDiagram The diagram containing the edge.
	 * @return True if the edge is satisfied.
	 * @pre pEdge != null && pDiagram != null && pDiagram.contains(pEdge)
	 * @pre pEdge.start() != null && pEdge.end() != null;
	 */
	boolean satisfied(Edge pEdge, Diagram pDiagram);
}
