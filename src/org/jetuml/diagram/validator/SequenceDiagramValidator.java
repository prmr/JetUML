package org.jetuml.diagram.validator;

import java.util.Set;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.ConstructorEdge;
import org.jetuml.diagram.edges.ReturnEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.diagram.validator.constraints.EdgeConstraint;
import org.jetuml.diagram.validator.constraints.EdgeSemanticConstraints;
import org.jetuml.diagram.validator.constraints.SequenceDiagramSemanticConstraints;

/**
 * Validator for sequence diagrams.
 */
public class SequenceDiagramValidator extends AbstractDiagramValidator
{
	private static final Set<EdgeConstraint> CONSTRAINTS = Set.of(
			EdgeSemanticConstraints.maxEdges(1), 
			EdgeSemanticConstraints.noteEdgeDoesNotConnectTwoNoteNodes(),
			SequenceDiagramSemanticConstraints.returnEdge());

	private static final Set<Class<? extends Node>> VALID_NODE_TYPES = Set.of(
			ImplicitParameterNode.class,
			CallNode.class);

	private static final Set<Class<? extends Edge>> VALID_EDGE_TYPES = Set.of(
			ConstructorEdge.class, 
			CallEdge.class,
			ReturnEdge.class);

	/**
	 * Creates a new validator for one sequence diagram.
	 *
	 * @param pDiagram The diagram to do semantic validity check on.
	 * @pre pDiagram != null && pDiagram.getType() == DiagramType.SEQUENCE
	 */
	public SequenceDiagramValidator(Diagram pDiagram)
	{
		super(pDiagram, VALID_NODE_TYPES, VALID_EDGE_TYPES, CONSTRAINTS);
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
}
