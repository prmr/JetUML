package org.jetuml.diagram.validator.constraints;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.PointNode;



public final class EdgeSemanticConstraints
{
  private EdgeSemanticConstraints() {}

  /**
   * An ending point node can only exist if it's connected to note edge
   */
  public static SemanticConstraint pointNode()
  {
    return (Edge pEdge, Node pStart, Node pEnd, Diagram pDiagram) ->
        !(pEnd.getClass() == PointNode.class && pEdge.getClass() != NoteEdge.class);

  }
  /*
   * A note edge can only be added between:
   * - Any node and a note node.
   * - A note node and a point node.
   */
  public static SemanticConstraint noteEdge()
  {
    return (Edge pEdge, Node pStart, Node pEnd, Diagram pDiagram) ->
        !( pEdge.getClass() == NoteEdge.class &&
            !((pStart.getClass() == NoteNode.class && pEnd.getClass() == PointNode.class) ||
                (pEnd.getClass() == NoteNode.class)));
  }


  /*
   * An edge can only be added to or from a note node if it is a note edge
   */
  public static SemanticConstraint noteNode()
  {
    return (Edge pEdge, Node pStart, Node pEnd, Diagram pDiagram)->
    {
      if( pStart.getClass() == NoteNode.class || pEnd.getClass() == NoteNode.class )
      {
        return pEdge.getClass() == NoteEdge.class;
      }
      return true;
    };
  }

  /*
   * Only pNumber of edges of the same type are allowed in one direction between two nodes
   */
  public static SemanticConstraint maxEdges(int pNumber)
  {
    assert pNumber > 0;
    return (Edge pEdge, Node pStart, Node pEnd, Diagram pDiagram)->
    {
      // TODO: Why pNumber needs to -1 in here?
      //  -> diff between "make it happen & undo" and "prevent it from happening"
      return numberOfEdges(pEdge.getClass(), pStart, pEnd, pDiagram) <= pNumber;
    };
  }

  /*
   * Self-edges are not allowed.
   */
  public static SemanticConstraint noSelfEdge()
  {
    return (Edge pEdge, Node pStart, Node pEnd, Diagram pDiagram)-> { return pStart != pEnd; };
  }


  /*
   * Returns the number of edges of type pType between pStart and pEnd
   */
  private static int numberOfEdges(Class<? extends Edge> pType, Node pStart, Node pEnd, Diagram pDiagram)
  {
    assert pType != null && pStart != null && pEnd != null && pDiagram != null;
    int result = 0;
    for(Edge edge : pDiagram.edges())
    {
      if(edge.getClass() == pType && edge.getStart() == pStart && edge.getEnd() == pEnd)
      {
        result++;
      }
    }
    return result;
  }
}
