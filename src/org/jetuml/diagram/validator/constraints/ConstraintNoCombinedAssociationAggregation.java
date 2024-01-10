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
import org.jetuml.diagram.edges.AggregationEdge;
import org.jetuml.diagram.edges.AssociationEdge;
import org.jetuml.diagram.validator.EdgeConstraint;

/**
 * There can't be both an association and an aggregation edge between two
 * nodes.
 */
public final class ConstraintNoCombinedAssociationAggregation implements EdgeConstraint
{
    @Override
    public boolean satisfied(Edge pEdge, Diagram pDiagram)
    {
        return pDiagram.edges().stream()
                .filter(ConstraintNoCombinedAssociationAggregation::isAssociationOrAggregation)
                .filter(edge -> isBetweenSameNodes(edge, pEdge))
                .count() <= 1;
    }

    /*
     * Aggregation edges and association edges are in the same category
     */
    private static boolean isAssociationOrAggregation(Edge pEdge)
    {
        return pEdge.getClass() == AssociationEdge.class || pEdge.getClass() == AggregationEdge.class;
    }

    /*
     * Irrespective of direction
     */
    private static boolean isBetweenSameNodes(Edge pEdge1, Edge pEdge2)
    {
        return pEdge1.start() == pEdge2.start() && pEdge1.end() == pEdge2.end() ||
                pEdge1.start() == pEdge2.end() && pEdge1.end() == pEdge2.start();
    }
}
