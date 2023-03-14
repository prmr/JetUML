package org.jetuml.diagram.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.nodes.FinalStateNode;
import org.jetuml.diagram.nodes.InitialStateNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestStateDiagramValidator
{

  private Diagram aDiagram;

  private StateDiagramValidator aStateDiagramValidator;
  private InitialStateNode aInitialStateNode;
  private FinalStateNode aFinalStateNode;
  private NoteNode aNoteNode;

  @BeforeEach
  public void setUp()
  {
    aDiagram = new Diagram(DiagramType.STATE);
    aNoteNode = new NoteNode();
    aInitialStateNode = new InitialStateNode();
    aFinalStateNode = new FinalStateNode();
    aStateDiagramValidator = new StateDiagramValidator(aDiagram);
  }

  @Test
  public void testValidNodeHierarchyAndValidElementName()
  {
    aDiagram.addRootNode(aInitialStateNode);
    aDiagram.addRootNode(aFinalStateNode);
    aDiagram.addRootNode(aNoteNode);
    assertTrue(aStateDiagramValidator.isDiagramValid());
  }

  @Test
  public void testValidElementName_False()
  {
    ObjectNode aObjectNode = new ObjectNode();
    aDiagram.addRootNode(aObjectNode);
    assertFalse(aStateDiagramValidator.isDiagramValid());
  }

}
