package org.jetuml.diagram.validator.constraints;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.nodes.FinalStateNode;
import org.jetuml.diagram.nodes.InitialStateNode;

public final class StateDiagramSemanticConstraints
{
  private StateDiagramSemanticConstraints() {}

  /*
   * No edges are allowed into an Initial Node
   */
  public static SemanticConstraint noEdgeToInitialNode()
  {
    return (Edge pEdge, Node pStart, Node pEnd, Diagram pDiagram)->
    {
      return pEnd.getClass() != InitialStateNode.class;
    };
  }

  /*
   * The only edge allowed out of a FinalNode is a NoteEdge
   */
  public static SemanticConstraint noEdgeFromFinalNode()
  {
    return (Edge pEdge, Node pStart, Node pEnd, Diagram pDiagram)->
    {
      return !(pStart.getClass() == FinalStateNode.class && pEdge.getClass() != NoteEdge.class );
    };
  }

}
