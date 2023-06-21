package org.jetuml.diagram.validator;

import java.util.Arrays;
import java.util.List;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.edges.StateTransitionEdge;
import org.jetuml.diagram.nodes.FinalStateNode;
import org.jetuml.diagram.nodes.InitialStateNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.diagram.nodes.StateNode;
import org.jetuml.diagram.validator.constraints.EdgeSemanticConstraints;
import org.jetuml.diagram.validator.constraints.SemanticConstraintSet;
import org.jetuml.diagram.validator.constraints.StateDiagramSemanticConstraints;

/**
 * Validator for state diagrams.
 */
public class StateDiagramValidator extends AbstractDiagramValidator
{
	private static final SemanticConstraintSet SEMANTIC_CONSTRAINT_SET = new SemanticConstraintSet(
			EdgeSemanticConstraints.noteEdgeToPointMustStartWithNote(), 
			EdgeSemanticConstraints.noteNode(),
			EdgeSemanticConstraints.maxEdges(2), 
			StateDiagramSemanticConstraints.noEdgeFromFinalNode(),
			StateDiagramSemanticConstraints.noEdgeToInitialNode());

	private static final List<Class<? extends Node>> VALID_NODES = Arrays.asList(
			StateNode.class,
			InitialStateNode.class, 
			FinalStateNode.class, 
			NoteNode.class,
			PointNode.class);

	private static final List<Class<? extends Edge>> VALID_EDGES = Arrays.asList(
			StateTransitionEdge.class,
			NoteEdge.class);

	/**
	 * Creates a new validator for one state diagram.
	 *
	 * @param pDiagram The diagram to do semantic validity check on.
	 * @pre pDiagram != null && pDiagram.getType() == DiagramType.STATE
	 */
	public StateDiagramValidator(Diagram pDiagram)
	{
		super(pDiagram);
		assert pDiagram.getType() == DiagramType.STATE;
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
	protected SemanticConstraintSet getEdgeConstraints()
	{
		return SEMANTIC_CONSTRAINT_SET;
	}
}
