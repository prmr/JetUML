package org.jetuml.diagram.validator.constraints;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.edges.ObjectCollaborationEdge;
import org.jetuml.diagram.nodes.ObjectNode;
import org.jetuml.diagram.validator.EdgeConstraint;

public final class ConstraintValidCollaborationEdge implements EdgeConstraint {

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
        return !(pEdge.getClass() == ObjectCollaborationEdge.class &&
                (pEdge.start().getClass() != ObjectNode.class || pEdge.end().getClass() != ObjectNode.class));
    }
}
