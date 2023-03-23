package org.jetuml.diagram.validator;
import java.util.Arrays;
import java.util.List;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.ConstructorEdge;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.edges.ReturnEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.diagram.validator.constraints.EdgeSemanticConstraints;
import org.jetuml.diagram.validator.constraints.SemanticConstraintSet;
import org.jetuml.diagram.validator.constraints.SequenceDiagramSemanticConstraints;

public class SequenceDiagramValidator extends AbstractDiagramValidator
{
  private static final SemanticConstraintSet
      SEMANTIC_CONSTRAINT_SET = new SemanticConstraintSet(
      EdgeSemanticConstraints.pointNode(),
      EdgeSemanticConstraints.noteEdge(),
      EdgeSemanticConstraints.noteNode(),
      EdgeSemanticConstraints.maxEdges(1),
      SequenceDiagramSemanticConstraints.noEdgesFromParameterTop(),
      SequenceDiagramSemanticConstraints.returnEdge(),
      SequenceDiagramSemanticConstraints.singleEntryPoint(),
      SequenceDiagramSemanticConstraints.callEdgeEnd()
  );

  private static final List<Class<? extends Node>> VALID_NODES = Arrays.asList(
      ImplicitParameterNode.class,
      CallNode.class,
      NoteNode.class,
      PointNode.class
  );

  private static final List<Class<? extends Edge>> VALID_EDGES = Arrays.asList(
      ConstructorEdge.class,
      CallEdge.class,
      ReturnEdge.class,
      NoteEdge.class
  );

  /**
   * Creates a new validator for one sequence diagram.
   *
   * @param pDiagram The diagram to do semantic validity check on.
   * @pre pDiagram != null && pDiagram.getType() == DiagramType.SEQUENCE
   */
  public SequenceDiagramValidator(Diagram pDiagram)
  {
    super( pDiagram );
    assert pDiagram.getType() == DiagramType.SEQUENCE;
  }

  /**
   * All children nodes of ImplicitParameterNode must be CallNode
   */
  @Override
  public boolean validNodeHierarchy()
  {
    boolean result = true;

    for (Node node : this.aDiagram.allNodes())
    {
      if (node instanceof ImplicitParameterNode container)
      {
        // if any of the child is not a valid child node, should return false
        result = container.getChildren().stream().allMatch(this::validChild);
      }
    }
    return result;
  }

  private boolean validChild(Node pPotentialChild)
  {
    return pPotentialChild instanceof CallNode;
  }

  @Override
  protected SemanticConstraintSet getEdgeConstraints()
  {
    return SEMANTIC_CONSTRAINT_SET;
  }

  @Override
  protected List<Class<? extends Node>> getValidNodeClasses() 
  {
    return VALID_NODES;
  }

  @Override
  protected List<Class<? extends Edge>> getValidEdgeClasses() 
  {
    return VALID_EDGES;
  }

}
