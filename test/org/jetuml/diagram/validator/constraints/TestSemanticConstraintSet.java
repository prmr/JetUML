package org.jetuml.diagram.validator.constraints;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.nodes.ClassNode;
import org.junit.jupiter.api.Test;

public class TestSemanticConstraintSet
{
  private DependencyEdge aEdge1 = new DependencyEdge();
  private Diagram aDiagram = new Diagram(DiagramType.CLASS);

  private SemanticConstraint createStubSemanticConstraint(boolean pReturn)
  {
    return (Edge pEdge, Node pStart, Node pEnd, Diagram pDiagram)->
    {
      return pReturn;
    };
  }

  @Test
  void testEmpty()
  {
    SemanticConstraintSet constraints = new SemanticConstraintSet();
    assertTrue(constraints.satisfied(aEdge1, new ClassNode(), new ClassNode(), aDiagram));
  }

  @Test
  void testSatisfiedAllFalse()
  {
    SemanticConstraintSet set1 = new SemanticConstraintSet(createStubSemanticConstraint(false), createStubSemanticConstraint(false), createStubSemanticConstraint(false));
    assertFalse(set1.satisfied(aEdge1, new ClassNode(), new ClassNode(), aDiagram));
  }

  @Test
  void testSatisfiedSomeFalse()
  {
    SemanticConstraintSet set1 = new SemanticConstraintSet(createStubSemanticConstraint(true), createStubSemanticConstraint(true), createStubSemanticConstraint(false));
    assertFalse(set1.satisfied(aEdge1, new ClassNode(), new ClassNode(), aDiagram));
  }

  @Test
  void testSatisfiedTrue()
  {
    SemanticConstraintSet set1 = new SemanticConstraintSet(createStubSemanticConstraint(true), createStubSemanticConstraint(true), createStubSemanticConstraint(true));
    assertTrue(set1.satisfied(aEdge1, new ClassNode(), new ClassNode(), aDiagram));
  }
  
}
