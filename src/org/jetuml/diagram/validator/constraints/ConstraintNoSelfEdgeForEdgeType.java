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
package org.jetuml.diagram.validator.constraints;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.validator.EdgeConstraint;

/**
 * There can't be an edge of the given type between the same node.
 */
public final class ConstraintNoSelfEdgeForEdgeType implements EdgeConstraint
{
    private final Class<? extends Edge> aEdgeType;

    /**
     * @param pEdgeType The edge type which cannot have the same start node and end node
     */
    public ConstraintNoSelfEdgeForEdgeType(Class<? extends Edge> pEdgeType)
    {
        aEdgeType = pEdgeType;
    }

    @Override
    public boolean satisfied(Edge pEdge, Diagram pDiagram)
    {
        return !(pEdge.getClass() == aEdgeType && pEdge.start() == pEdge.end());
    }
}
