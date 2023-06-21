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

	private static final List<Class<? extends Node>> VALID_NODES = Arrays.asList(ImplicitParameterNode.class,
			CallNode.class, NoteNode.class, PointNode.class);

	private static final List<Class<? extends Edge>> VALID_EDGES = Arrays.asList(ConstructorEdge.class, CallEdge.class,
			ReturnEdge.class, NoteEdge.class);

	/**
	 * Creates a new validator for one sequence diagram.
	 *
	 * @param pDiagram The diagram to do semantic validity check on.
	 * @pre pDiagram != null && pDiagram.getType() == DiagramType.SEQUENCE
	 */
	public SequenceDiagramValidator(Diagram pDiagram)
	{
		super(pDiagram);
		assert pDiagram.getType() == DiagramType.SEQUENCE;
	}

	/**
	 * Root nodes contain no call nodes.
	 * Point nodes are connected
	 */
	@Override
	public boolean validNodeHierarchy()
	{
		return aDiagram.rootNodes().stream()
				.allMatch(node -> node.getClass() != CallNode.class) &&
				aDiagram.rootNodes().stream()
				.filter(PointNode.class::isInstance)
				.allMatch(node -> aDiagram.edgesConnectedTo(node).iterator().hasNext());
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
