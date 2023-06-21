package org.jetuml.diagram.validator;

import java.util.Arrays;
import java.util.List;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.edges.UseCaseAssociationEdge;
import org.jetuml.diagram.edges.UseCaseDependencyEdge;
import org.jetuml.diagram.edges.UseCaseGeneralizationEdge;
import org.jetuml.diagram.nodes.ActorNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.diagram.nodes.UseCaseNode;
import org.jetuml.diagram.validator.constraints.EdgeSemanticConstraints;
import org.jetuml.diagram.validator.constraints.SemanticConstraintSet;

/**
 * Validator for use case diagrams.
 */
public class UseCaseDiagramValidator extends AbstractDiagramValidator
{
	private static final SemanticConstraintSet SEMANTIC_CONSTRAINT_SET = new SemanticConstraintSet(
			EdgeSemanticConstraints.noteEdgeToPointMustStartWithNote(), 
			EdgeSemanticConstraints.noteNode(),
			EdgeSemanticConstraints.maxEdges(1), 
			EdgeSemanticConstraints.noSelfEdge());

	private static final List<Class<? extends Node>> VALID_NODES = Arrays.asList(
			ActorNode.class, 
			UseCaseNode.class,
			NoteNode.class, 
			PointNode.class);

	private static final List<Class<? extends Edge>> VALID_EDGES = Arrays.asList(
			UseCaseAssociationEdge.class,
			UseCaseDependencyEdge.class, 
			UseCaseGeneralizationEdge.class, 
			NoteEdge.class);

	/**
	 * Creates a new validator for one use case diagram.
	 *
	 * @param pDiagram The diagram to do semantic validity check on.
	 * @pre pDiagram != null && pDiagram.getType() == DiagramType.USECASE
	 */

	public UseCaseDiagramValidator(Diagram pDiagram)
	{
		super(pDiagram);
		assert pDiagram.getType() == DiagramType.USECASE;
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

	@Override
	public SemanticConstraintSet getEdgeConstraints()
	{
		return SEMANTIC_CONSTRAINT_SET;
	}
}
