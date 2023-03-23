package org.jetuml.diagram.validator;

import java.util.Arrays;
import java.util.List;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.edges.ObjectCollaborationEdge;
import org.jetuml.diagram.edges.ObjectReferenceEdge;
import org.jetuml.diagram.nodes.FieldNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.diagram.validator.constraints.EdgeSemanticConstraints;
import org.jetuml.diagram.validator.constraints.ObjectDiagramSemanticConstraints;
import org.jetuml.diagram.validator.constraints.SemanticConstraintSet;

public class ObjectDiagramValidator extends AbstractDiagramValidator
{
  private static final SemanticConstraintSet
      SEMANTIC_CONSTRAINT_SET = new SemanticConstraintSet(
      EdgeSemanticConstraints.pointNode(),
      EdgeSemanticConstraints.noteEdge(),
      EdgeSemanticConstraints.noteNode(),
      EdgeSemanticConstraints.maxEdges(1),
      ObjectDiagramSemanticConstraints.collaboration(),
      ObjectDiagramSemanticConstraints.reference()
  );

  private static final List<Class<? extends Node>> VALID_NODES = Arrays.asList(
    ObjectNode.class,
    FieldNode.class,
    NoteNode.class,
    PointNode.class
  );

  private static final List<Class<? extends Edge>> VALID_EDGES = Arrays.asList(
      ObjectReferenceEdge.class,
      ObjectCollaborationEdge.class,
      NoteEdge.class
  );

  /**
   * Creates a new validator for one object diagram.
   *
   * @param pDiagram The diagram to do semantic validity check on.
   * @pre pDiagram != null && pDiagram.getType() == DiagramType.OBJECT
   */
  public ObjectDiagramValidator(Diagram pDiagram)
  {
    super( pDiagram );
    assert pDiagram.getType() == DiagramType.OBJECT;
  }

  @Override
  protected SemanticConstraintSet getEdgeConstraints()
  {
    return SEMANTIC_CONSTRAINT_SET;
  }

  /**
   * 1. All root nodes must not be FieldNode
   * 2. For ObjectNode root node, the children nodes it contains must only contain FieldNode
   */
  @Override
  protected boolean validNodeHierarchy()
  {
    return aDiagram.rootNodes().stream().anyMatch(node -> !(node instanceof FieldNode)) && 
    		aDiagram.rootNodes().stream()
        .filter(node -> node instanceof ObjectNode)
        .allMatch(objectNode -> objectNode.getChildren()
            .stream().allMatch(child -> child instanceof FieldNode));
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
