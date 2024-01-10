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
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.validator.EdgeConstraint;
import java.util.List;

/**
 * There can be at most one caller to a call node.
 */
public final class ConstraintMaxOneCaller implements EdgeConstraint
{
    @Override
    public boolean satisfied(Edge pEdge, Diagram pDiagram)
    {
        return pDiagram.allNodes().stream()								// Nodes
                .filter(CallNode.class::isInstance)						// Call nodes
                .map(node -> pDiagram.edgesTo(node, CallEdge.class))	// Lists of callers to call nodes
                .mapToInt(List::size)									// Size of such lists
                .allMatch(size -> size <= 1);
    }
}
