package org.jetuml.diagram.validator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.jetuml.diagram.Node;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.jetuml.diagram.nodes.PackageNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestClassDiagramValidator
{
  private Diagram aDiagram;

  private ClassDiagramValidator aClassDiagramValidator;
  private PackageNode aPackageNode1;
  private ClassNode aClassNode1;

  private NoteNode aNoteNode1;

  @BeforeEach
  public void setUp()
  {
    aDiagram = new Diagram(DiagramType.CLASS);
    aClassNode1 = new ClassNode();
    aPackageNode1 = new PackageNode();
    aNoteNode1 = new NoteNode();
    aClassDiagramValidator = new ClassDiagramValidator(aDiagram);
  }

  @Test
  public void testValidNodeHierarchy_True()
  {
    aPackageNode1.addChild(aClassNode1);
    aDiagram.addRootNode(aPackageNode1);
    assertTrue(aClassDiagramValidator.isValid());
  }


  @Test
  public void testValidNodeHierarchy_False()
  {
    try
    {
      Field aContainedNodes = PackageNode.class.getDeclaredField("aContainedNodes");
      aContainedNodes.setAccessible(true);
      ArrayList<Node> packageNodes = new ArrayList<>();
      packageNodes.add(aNoteNode1);
      aContainedNodes.set(aPackageNode1, packageNodes);
      aDiagram.addRootNode(aPackageNode1);
      assertFalse(aClassDiagramValidator.isValid());
    }

    catch (NoSuchFieldException | IllegalAccessException e)
    {
      e.printStackTrace();
    }

  }


  @Test
  public void testValidElementName_True()
  {
    aDiagram.addRootNode(aClassNode1);
    aDiagram.addRootNode(aNoteNode1);
    assertTrue(aClassDiagramValidator.isValid());
  }

  @Test
  public void testValidElementName_False()
  {
    ObjectNode aObjectNode = new ObjectNode();
    aDiagram.addRootNode(aObjectNode);
    assertFalse(aClassDiagramValidator.isValid());
  }

}
