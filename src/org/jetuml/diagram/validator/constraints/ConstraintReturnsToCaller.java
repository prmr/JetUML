package org.jetuml.diagram.validator.constraints;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.ReturnEdge;
import org.jetuml.diagram.validator.EdgeConstraint;

import java.util.List;

public final class ConstraintReturnsToCaller implements EdgeConstraint {

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
