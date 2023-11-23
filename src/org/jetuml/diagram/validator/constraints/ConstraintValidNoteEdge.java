package org.jetuml.diagram.validator.constraints;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.diagram.validator.EdgeConstraint;

public final class ConstraintValidNoteEdge implements EdgeConstraint {

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
        if( pEdge.getClass() != NoteEdge.class )
        {
            return true;
        }
        if( pEdge.end().getClass() == PointNode.class )
        {
            return pEdge.start().getClass() == NoteNode.class;
        }
        return pEdge.start().getClass() != PointNode.class && pEdge.start().getClass() != NoteNode.class &&
                pEdge.end().getClass() == NoteNode.class;
    }
}
