package org.jetuml.diagram.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.FieldNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestObjectDiagramValidator
{
  private Diagram aDiagram;
  private ObjectDiagramValidator aObjectDiagramValidator;
  private FieldNode aFieldNode;
  private ObjectNode aObjectNode;
  private NoteNode aNoteNode1;

  //@BeforeAll
  //public static void setupClass()
  //{
  //  JavaFXLoader.load();
  //}

  @BeforeEach
  public void setUp()
  {
    aDiagram = new Diagram(DiagramType.OBJECT);
    aFieldNode = new FieldNode();
    aObjectNode = new ObjectNode();
    aNoteNode1 = new NoteNode();
    aObjectDiagramValidator = new ObjectDiagramValidator(aDiagram);
  }

  @Test
  public void testValidNodeHierarchy_True()
  {
    aDiagram.addRootNode(aObjectNode);
    aDiagram.addRootNode(aNoteNode1);
    assertTrue(aObjectDiagramValidator.isDiagramValid());
  }

  @Test
  public void testValidNodeHierarchy_False()
  {
    aDiagram.addRootNode(aFieldNode);
    assertFalse(aObjectDiagramValidator.isDiagramValid());
    try
    {
      Field aFields = ObjectNode.class.getDeclaredField("aFields");
      aFields.setAccessible(true);
      ArrayList<Node> objectChildren = new ArrayList<>();
      objectChildren.add(aNoteNode1);
      aFields.set(aObjectNode, objectChildren);
      aDiagram.addRootNode(aObjectNode);
      assertFalse(aObjectDiagramValidator.isDiagramValid());
    }
    catch (NoSuchFieldException | IllegalAccessException e)
    {
      e.printStackTrace();
    }
  }

  @Test
  public void testValidElementName_True()
  {
    aDiagram.addRootNode(aObjectNode);
    aDiagram.addRootNode(aNoteNode1);
    assertTrue(aObjectDiagramValidator.isDiagramValid());
  }

  @Test
  public void testValidElementName_False()
  {
    ClassNode aClassNode = new ClassNode();
    aDiagram.addRootNode(aClassNode);
    assertFalse(aObjectDiagramValidator.isDiagramValid());

  }
}
