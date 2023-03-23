package org.jetuml.diagram.validator.constraints;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;

public class SemanticConstraintSet
{

  private final Set<SemanticConstraint> aSemanticConstraints = new HashSet<>();
  public SemanticConstraintSet( SemanticConstraint... pSemanticConstraints)
  {
    assert pSemanticConstraints != null;
    aSemanticConstraints.addAll(Arrays.asList(pSemanticConstraints));
  }

  public boolean satisfied(Edge pEdge, Node pStart, Node pEnd, Diagram pDiagram)
  {
    for ( SemanticConstraint semanticConstraint : aSemanticConstraints )
    {
      if ( !semanticConstraint.satisfied(pEdge, pStart, pEnd, pDiagram) ) 
      {
        return false;
      }
    }

    return true;
  }
}
