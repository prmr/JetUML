package org.jetuml.diagram.validator.constraints;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.edges.StateTransitionEdge;
import org.jetuml.diagram.nodes.FinalStateNode;
import org.jetuml.diagram.nodes.InitialStateNode;
import org.jetuml.diagram.nodes.StateNode;
import org.junit.jupiter.api.Test;

public class TestStateDiagramSemanticConstraints
{
	private Diagram aDiagram = new Diagram(DiagramType.STATE);
	private StateNode aState = new StateNode();
	private InitialStateNode aInitialNode = new InitialStateNode();
	private FinalStateNode aFinalNode = new FinalStateNode();
	private StateTransitionEdge aEdge = new StateTransitionEdge();

	private void createDiagram()
	{
		aDiagram.addRootNode(aState);
		aDiagram.addRootNode(aInitialNode);
		aDiagram.addRootNode(aFinalNode);
	}

	@Test
	void testNoEdgeToInitialNodeFalse()
	{
		createDiagram();
		aEdge.connect(aState, aInitialNode);
		assertFalse(StateDiagramSemanticConstraints.noEdgeToInitialNode().satisfied(aEdge, aDiagram));
	}

	@Test
	void testNoEdgeToInitialNodeTrue()
	{
		createDiagram();
		aEdge.connect(aInitialNode, aState);
		assertTrue(StateDiagramSemanticConstraints.noEdgeToInitialNode().satisfied(aEdge, aDiagram));
	}

	@Test
	void testNoEdgeFromFinalNodeInapplicableEdge()
	{
		createDiagram();
		NoteEdge edge = new NoteEdge();
		edge.connect(aFinalNode, aState);
		assertTrue(StateDiagramSemanticConstraints.noEdgeFromFinalNode().satisfied(edge, aDiagram));
	}

	@Test
	void testNoEdgeFromFinalNodeApplicableEdgeFalse()
	{
		createDiagram();
		aEdge.connect(aFinalNode, aState);
		assertFalse(StateDiagramSemanticConstraints.noEdgeFromFinalNode().satisfied(aEdge, aDiagram));
	}

	@Test
	void testNoEdgeFromFinalNodeApplicableEdgeTrue()
	{
		createDiagram();
		aEdge.connect(aState, aState);
		assertTrue(StateDiagramSemanticConstraints.noEdgeFromFinalNode().satisfied(aEdge, aDiagram));
	}
}
