package org.jetuml.diagram.validator.constraints;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.edges.AggregationEdge;
import org.jetuml.diagram.edges.AssociationEdge;
import org.jetuml.diagram.validator.EdgeConstraint;

public final class ConstraintNoCombinedAssociationAggregation implements EdgeConstraint {

    /**
     * Determines if a constraint is satisfied.
     *
     * @param pEdge    The edge being validated.
     * @param pDiagram The diagram containing the edge.
     * @return True if the edge is satisfied.
     * @pre pEdge != null && pDiagram != null && pDiagram.contains(pEdge)
     * @pre pEdge.start() != null && pEdge.end() != null;
     */
    @Override
    public boolean satisfied(Edge pEdge, Diagram pDiagram) {
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
