package org.jetuml.diagram.validator.constraints;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.validator.EdgeConstraint;

public final class ConstraintNoDirectCyclesForEdgeType implements EdgeConstraint {

    private Class<? extends Edge> aEdgeType;

    public ConstraintNoDirectCyclesForEdgeType(Class<? extends Edge> pEdgeType)
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
        if( pEdge.getClass() != aEdgeType || pEdge.start() == pEdge.end() )
        {
            return true;
        }

        int sameDirectionCount = 0;
        for( Edge edge : pDiagram.edgesConnectedTo(pEdge.start()) )
        {
            if( edge.getClass() == aEdgeType && edge.end() == pEdge.start() && edge.start() == pEdge.end() )
            {
                sameDirectionCount += 1;
            }
        }

        return sameDirectionCount == 0;
    }
}
