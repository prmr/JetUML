package org.jetuml.diagram.validator.constraints;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.GeneralizationEdge;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.geom.Point;
import org.jetuml.rendering.DiagramRenderer;
import org.junit.jupiter.api.Test;

public class TestClassDiagramSemanticConstraints
{
  private Diagram aDiagram = new Diagram(DiagramType.CLASS);
  private ClassNode aNode1 = new ClassNode();;
  private ClassNode aNode2 = new ClassNode();;
  private DependencyEdge aEdge1 = new DependencyEdge();
  private GeneralizationEdge aGen1 = new GeneralizationEdge();

  private void createDiagram()
  {
    aNode2.moveTo(new Point(0,100));
    aDiagram.addRootNode(aNode1);
    aDiagram.addRootNode(aNode2);
  }

  @Test
  void testNoSelfGeneralizationNotAGeneralizationEdge()
  {
    createDiagram();
    assertTrue(ClassDiagramSemanticConstraints.noSelfGeneralization().satisfied(aEdge1, aNode1, new ClassNode(), aDiagram));
    assertTrue(ClassDiagramSemanticConstraints.noSelfGeneralization().satisfied(aEdge1, aNode1, aNode1, aDiagram));
  }

  @Test
  void testNoSelfGeneralizationGeneralizationEdge()
  {
    createDiagram();
    assertTrue(ClassDiagramSemanticConstraints.noSelfGeneralization().satisfied(aGen1, aNode1, aNode2, aDiagram));
    assertFalse(ClassDiagramSemanticConstraints.noSelfGeneralization().satisfied(aGen1, aNode1, aNode1, aDiagram));
  }

  @Test
  void testNoDirectCycles_NotADependency()
  {
    createDiagram();
    assertTrue(ClassDiagramSemanticConstraints.noDirectCycles(DependencyEdge.class).satisfied(new GeneralizationEdge(), aNode1, aNode2, aDiagram));
  }

  @Test
  void testNoDirectCycles_NoExistingEdge()
  {
    createDiagram();
    assertTrue(ClassDiagramSemanticConstraints.noDirectCycles(DependencyEdge.class).satisfied(aEdge1, aNode1, aNode2, aDiagram));
  }

  @Test
  void testNoDirectCycles_NoExistingDependencyEdge()
  {
    createDiagram();
    GeneralizationEdge edge = new GeneralizationEdge();
    edge.connect(aNode1, aNode2);
    aDiagram.addEdge(edge);
    assertTrue(ClassDiagramSemanticConstraints.noDirectCycles(DependencyEdge.class).satisfied(aEdge1, aNode1, aNode2, aDiagram));
  }

  @Test
  void testNoDirectCycles_False()
  {
    createDiagram();
    DependencyEdge edge = new DependencyEdge();
    edge.connect(aNode1, aNode2);
    aDiagram.addEdge(edge);
    assertFalse(ClassDiagramSemanticConstraints.noDirectCycles(DependencyEdge.class).satisfied(aEdge1, aNode2, aNode1, aDiagram));
  }





}
