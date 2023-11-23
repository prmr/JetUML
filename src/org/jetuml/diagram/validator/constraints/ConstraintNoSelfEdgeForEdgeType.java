package org.jetuml.diagram.validator.constraints;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.validator.EdgeConstraint;

public final class ConstraintNoSelfEdgeForEdgeType implements EdgeConstraint {

    private Class<? extends Edge> aEdgeType;

    public ConstraintNoSelfEdgeForEdgeType(Class<? extends Edge> pEdgeType)
    {
        aEdgeType = pEdgeType;
    }

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
        return !(pEdge.getClass() == aEdgeType && pEdge.start() == pEdge.end());
    }
}
