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
 * There can't be more than a given number of edges of the same type between two nodes.
 */
public final class ConstraintMaxNumberOfEdgesOfGivenTypeBetweenNodes implements EdgeConstraint
{
    private final int aMaxNumberOfEdges;

    /**
     * @param pMaxNumberOfEdges The max number of edges of the same type 
     * allowed between two nodes
     */
    public ConstraintMaxNumberOfEdgesOfGivenTypeBetweenNodes(int pMaxNumberOfEdges)
    {
        aMaxNumberOfEdges = pMaxNumberOfEdges;
    }

    @Override
    public boolean satisfied(Edge pEdge, Diagram pDiagram)
    {
        return numberOfEdges(pEdge, pDiagram) <= aMaxNumberOfEdges;
    }

    /*
     * Returns the number of edges of type pType between pStart and pEnd
     */
    private static int numberOfEdges(Edge pEdge, Diagram pDiagram)
    {
        assert pEdge != null && pDiagram != null;
        int result = 0;
        for( Edge edge : pDiagram.edges() )
        {
            if( edge.getClass() == pEdge.getClass() && edge.start() == pEdge.start() && edge.end() == pEdge.end() )
            {
                result++;
            }
        }
        return result;
    }
}
