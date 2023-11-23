package org.jetuml.diagram.validator.constraints;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.validator.EdgeConstraint;
import java.util.List;

public final class ConstraintMaxOneCaller implements EdgeConstraint {

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
        return pDiagram.allNodes().stream()								// Nodes
                .filter(CallNode.class::isInstance)						// Call nodes
                .map(node -> pDiagram.edgesTo(node, CallEdge.class))	// Lists of callers to call nodes
                .mapToInt(List::size)									// Size of such lists
                .allMatch(size -> size <= 1);
    }
}
