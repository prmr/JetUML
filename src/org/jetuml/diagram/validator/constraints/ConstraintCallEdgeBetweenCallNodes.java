package org.jetuml.diagram.validator.constraints;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.validator.EdgeConstraint;

public final class ConstraintCallEdgeBetweenCallNodes implements EdgeConstraint {

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
        return !(pEdge instanceof CallEdge && (pEdge.start().getClass() != CallNode.class ||
                pEdge.end().getClass() != CallNode.class));
    }
}
