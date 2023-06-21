package org.jetuml.diagram.validator;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.diagram.validator.constraints.EdgeSemanticConstraints;
import org.jetuml.diagram.validator.constraints.SemanticConstraintSet;
import org.jetuml.diagram.validator.constraints.SequenceDiagramSemanticConstraints;

/**
 * Validator for sequence diagrams.
 */
public class SequenceDiagramValidator extends AbstractDiagramValidator
{
	private static final SemanticConstraintSet SEMANTIC_CONSTRAINT_SET = new SemanticConstraintSet(
			EdgeSemanticConstraints.noteEdgeToPointMustStartWithNote(), 
			EdgeSemanticConstraints.noteNode(),
			EdgeSemanticConstraints.maxEdges(1), 
			EdgeSemanticConstraints.noteEdgeDoesNotConnectTwoNoteNodes(),
			SequenceDiagramSemanticConstraints.returnEdge());

	private static final Set<Class<? extends Node>> VALID_NODES_TYPES = Set.of(
			ImplicitParameterNode.class,
			CallNode.class);

	private static final List<Class<? extends Edge>> VALID_EDGES = Arrays.asList(
			ConstructorEdge.class, 
			CallEdge.class,
			ReturnEdge.class, 
			NoteEdge.class);

	/**
	 * Creates a new validator for one sequence diagram.
	 *
	 * @param pDiagram The diagram to do semantic validity check on.
	 * @pre pDiagram != null && pDiagram.getType() == DiagramType.SEQUENCE
	 */
	public SequenceDiagramValidator(Diagram pDiagram)
	{
		super(pDiagram, VALID_NODES_TYPES);
		assert pDiagram.getType() == DiagramType.SEQUENCE;
	}

	/**
	 * Root nodes contain no call nodes.
	 * Point nodes are connected
	 */
	@Override
	protected boolean hasValidNodes()
	{
		return diagram().rootNodes().stream()
				.allMatch(node -> node.getClass() != CallNode.class) &&
				diagram().rootNodes().stream()
				.filter(PointNode.class::isInstance)
				.allMatch(node -> diagram().edgesConnectedTo(node).iterator().hasNext());
	}

	@Override
	protected SemanticConstraintSet edgeConstraints()
	{
		return SEMANTIC_CONSTRAINT_SET;
	}

	@Override
	protected List<Class<? extends Edge>> validEdgesTypes()
	{
		return VALID_EDGES;
	}
}
