package org.jetuml.diagram.validator.constraints;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.validator.EdgeConstraint;

public final class ConstraintMaxNumberOfEdgesOfGivenTypeBetweenNodes implements EdgeConstraint {

    private final int aMaxNumberOfEdges;

    public ConstraintMaxNumberOfEdgesOfGivenTypeBetweenNodes(int pMaxNumberOfEdges)
    {
        aMaxNumberOfEdges = pMaxNumberOfEdges;
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
