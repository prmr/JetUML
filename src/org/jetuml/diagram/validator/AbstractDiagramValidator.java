package org.jetuml.diagram.validator;

import java.util.List;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.validator.constraints.SemanticConstraintSet;

public abstract class AbstractDiagramValidator implements DiagramValidator {

  protected final Diagram aDiagram;


  /**
   * Creates a builder for the diagram wrapped by pDiagram, and an embedded renderer.
   *
   * @param pDiagram The diagram that we want to check semantic validity on
   * @pre pDiagramBuilder != null;
   */
  protected AbstractDiagramValidator( Diagram pDiagram )
  {
    assert pDiagram != null;
    aDiagram = pDiagram;
  }

  /**
   * Check whether the diagram associated with this validator.
   *
   * @return true if the diagram passed semantic validity checks.
   */
  @Override
  public boolean isDiagramValid()
  {
    return validElementName() && validNodeHierarchy() && satisfyEdgeConstraints();
  }

  protected abstract List<Class<? extends Node>> getValidNodeClasses();

  protected abstract List<Class<? extends Edge>> getValidEdgeClasses();

  private boolean validElementName()
  {
    return aDiagram.allNodes().stream().allMatch(node -> getValidNodeClasses().contains(node.getClass()))
        && aDiagram.edges().stream().allMatch(edge -> getValidEdgeClasses().contains(edge.getClass()));
  }


  /**
   * Helper method to check whether the children/parent nodes
   * hierarchy relationship holds true for class,object and sequence diagram.
   *
   * @return always true for diagrams except (class,object and sequence diagram).
   * for class,object and sequence diagram, it might fail due to invalid node hierarchy.
   */
  protected boolean validNodeHierarchy()
  {
    return true;
  }

  private boolean satisfyEdgeConstraints()
  {
    return aDiagram.edges().stream()
        .allMatch(edge ->
            getEdgeConstraints().satisfied(edge, edge.getStart(), edge.getEnd(), aDiagram));

  }

  protected abstract SemanticConstraintSet getEdgeConstraints();

  /**
   * @return The diagram wrapped by this validator.
   */
  public final Diagram diagram()
  {
    return aDiagram;
  }
}
