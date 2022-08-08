/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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

package org.jetuml.diagram.builder.constraints;

import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.geom.Point;
import org.jetuml.rendering.DiagramRenderer;

/**
 * Represents a generic constraint on how an edge can be created.
 */
public interface Constraint
{
	/**
	 * @param pEdge The edge on which the constraint is applied.
	 * @param pStart The start node for the edge.
	 * @param pEnd The end node for the edge.
	 * @param pStartPoint The point on the canvas where the edge rubber band starts.
	 * @param pEndPoint The point on the canvas where the edge rubber band ends.
	 * @param pRenderer The renderer for the diagram in which the edge is to be added.
	 * @return True if this constraint is satisfied.
	 */
	boolean satisfied(Edge pEdge, Node pStart, Node pEnd, Point pStartPoint, Point pEndPoint, DiagramRenderer pRenderer);
}