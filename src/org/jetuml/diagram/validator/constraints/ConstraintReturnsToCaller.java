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
import org.jetuml.diagram.edges.ReturnEdge;
import org.jetuml.diagram.validator.EdgeConstraint;

import java.util.List;

/**
 * A return edge must return to its caller, which must be a different node.
 */
public final class ConstraintReturnsToCaller implements EdgeConstraint
{
    @Override
    public boolean satisfied(Edge pEdge, Diagram pDiagram)
    {
        if( pEdge.getClass() != ReturnEdge.class )
        {
            return true;
        }
        List<Edge> calls = pDiagram.edgesTo(pEdge.start(), CallEdge.class);
        if(calls.size() != 1)
        {
            return false;
        }
        return pEdge.end() == calls.get(0).start() && pEdge.end().getParent() != pEdge.start().getParent();
    }
}
