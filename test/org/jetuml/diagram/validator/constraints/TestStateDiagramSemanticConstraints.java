package org.jetuml.diagram.validator.constraints;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.edges.StateTransitionEdge;
import org.jetuml.diagram.nodes.FinalStateNode;
import org.jetuml.diagram.nodes.InitialStateNode;
import org.jetuml.diagram.nodes.StateNode;
import org.junit.jupiter.api.Test;

public class TestStateDiagramSemanticConstraints 
{
  private Diagram aDiagram = new Diagram(DiagramType.STATE);
  private StateNode aState = new StateNode();
  private InitialStateNode aInitialNode = new InitialStateNode();
  private FinalStateNode aFinalNode = new FinalStateNode();
  private StateTransitionEdge aEdge = new StateTransitionEdge();

  private void createDiagram()
  {
    aDiagram.addRootNode(aState);
    aDiagram.addRootNode(aInitialNode);
    aDiagram.addRootNode(aFinalNode);
  }

  @Test
  void testNoEdgeToInitialNodeFalse()
  {
    createDiagram();
    assertFalse(StateDiagramSemanticConstraints.noEdgeToInitialNode().satisfied(aEdge, aState, aInitialNode, aDiagram));
  }

  @Test
  void testNoEdgeToInitialNodeTrue()
  {
    createDiagram();
    assertTrue(StateDiagramSemanticConstraints.noEdgeToInitialNode().satisfied(aEdge, aInitialNode, aState, aDiagram));
  }

  @Test
  void testNoEdgeFromFinalNodeInapplicableEdge()
  {
    createDiagram();
    assertTrue(StateDiagramSemanticConstraints.noEdgeFromFinalNode().satisfied(new NoteEdge(), aFinalNode, aState, aDiagram));
  }

  @Test
  void testNoEdgeFromFinalNodeApplicableEdgeFalse()
  {
    createDiagram();
    assertFalse(StateDiagramSemanticConstraints.noEdgeFromFinalNode().satisfied(aEdge, aFinalNode, aState, aDiagram));
  }

  @Test
  void testNoEdgeFromFinalNodeApplicableEdgeTrue()
  {
    createDiagram();
    assertTrue(StateDiagramSemanticConstraints.noEdgeFromFinalNode().satisfied(aEdge, aState, aState, aDiagram));
  }
  
}
