package org.jetuml.diagram.validator.constraints;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.ReturnEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.junit.jupiter.api.Test;

public class TestSequenceDiagramSemanticConstraints
{
	private Diagram aDiagram = new Diagram(DiagramType.SEQUENCE);
	private ImplicitParameterNode aParameter1 = new ImplicitParameterNode();
	private ImplicitParameterNode aParameter2 = new ImplicitParameterNode();
	private CallNode aCallNode1 = new CallNode();
	private CallNode aCallNode2 = new CallNode();
	private CallNode aCallNode3 = new CallNode();
	private CallEdge aCallEdge = new CallEdge();
	private ReturnEdge aReturnEdge = new ReturnEdge();

	private void createDiagram()
	{
		aDiagram.addRootNode(aParameter1);
		aDiagram.addRootNode(aParameter2);
		aParameter1.addChild(aCallNode1);
		aParameter1.addChild(aCallNode3);
		aParameter2.addChild(aCallNode2);
	}

	@Test
	void testCanCreateConstructor_StartNodeIncorrect()
	{
		assertFalse(SequenceDiagramSemanticConstraints.canCreateConstructor(new NoteNode(), aCallNode1));
	}

	@Test
	void testCanCreateConstructor_EndNodeNotAnImplicitParameterNode()
	{
		assertFalse(SequenceDiagramSemanticConstraints.canCreateConstructor(aCallNode1, aCallNode2));
	}

	@Test
	void testCanCreateConstructor_EndNodeDoesNotContainPoint()
	{
		aParameter2.addChild(aCallNode2);
		assertFalse(SequenceDiagramSemanticConstraints.canCreateConstructor(aParameter1, aParameter2));
	}

	@Test
	void testCanCreateConstructor_EndNodeContainsPointButNotChildless()
	{
		createDiagram();
		assertFalse(SequenceDiagramSemanticConstraints.canCreateConstructor(aParameter1, aParameter2));
	}

	@Test
	void testCanCreateConstructor_EndNodeContainsPointButAndChildless()
	{
		assertTrue(SequenceDiagramSemanticConstraints.canCreateConstructor(aParameter1, aParameter2));
	}

	@Test
	void testNoEdgeFromParameterTopNotParameterNode()
	{
		aCallEdge.connect(aCallNode1, aCallNode2);
		assertTrue(SequenceDiagramSemanticConstraints.noEdgesFromParameterTop().satisfied(aCallEdge, aDiagram));
	}

	@Test
	void testNoEdgeFromParameterTopParameterFalse()
	{
		aDiagram.addRootNode(aParameter1);
		aCallEdge.connect(aParameter1, aParameter1);
		assertFalse(SequenceDiagramSemanticConstraints.noEdgesFromParameterTop().satisfied(aCallEdge, aDiagram));
	}

	@Test
	void testNoEdgeFromParameterTopParameterTrue()
	{
		aDiagram.addRootNode(aParameter1);
		aCallEdge.connect(aCallNode1, aCallNode2);
		aParameter1.addChild(aCallNode1);
		aParameter1.addChild(aCallNode2);
		assertTrue(SequenceDiagramSemanticConstraints.noEdgesFromParameterTop().satisfied(aCallEdge, aDiagram));
	}

	@Test
	void testreturnEdgeNotReturnEdge()
	{
		createDiagram();
		aCallEdge.connect(aCallNode1, aCallNode2);
		assertTrue(
				SequenceDiagramSemanticConstraints.returnEdge().satisfied(aCallEdge, aDiagram));
	}

	@Test
	void testreturnEdgeIncompatibleStart()
	{
		createDiagram();
		aReturnEdge.connect(aParameter1, aCallNode2);
		assertFalse(SequenceDiagramSemanticConstraints.returnEdge().satisfied(aReturnEdge, aDiagram));
	}

	@Test
	void testreturnEdgeIncompatibleEnd()
	{
		createDiagram();
		aReturnEdge.connect(aCallNode1, aParameter2);
		assertFalse(SequenceDiagramSemanticConstraints.returnEdge().satisfied(aReturnEdge, aDiagram));
	}

	@Test
	void testreturnEdgeEndNoCaller()
	{
		createDiagram();
		aReturnEdge.connect(aCallNode3, aCallNode2);
		assertFalse(SequenceDiagramSemanticConstraints.returnEdge().satisfied(aReturnEdge, aDiagram));
	}

	@Test
	void testreturnEdgeEndNotCaller()
	{
		createDiagram();
		aCallEdge.connect(aCallNode1, aCallNode2);
		aDiagram.addEdge(aCallEdge);
		aReturnEdge.connect(aCallNode2, aCallNode3);
		assertFalse(SequenceDiagramSemanticConstraints.returnEdge().satisfied(aReturnEdge, aDiagram));
	}

	@Test
	void testreturnEdgeSelfCaller()
	{
		createDiagram();
		aCallEdge.connect(aCallNode1, aCallNode3);
		aDiagram.addEdge(aCallEdge);
		aReturnEdge.connect(aCallNode3, aCallNode1);
		assertFalse(SequenceDiagramSemanticConstraints.returnEdge().satisfied(aReturnEdge, aDiagram));
	}

	@Test
	void testreturnEdgeValid()
	{
		createDiagram();
		aCallEdge.connect(aCallNode1, aCallNode2);
		aDiagram.addEdge(aCallEdge);
		aReturnEdge.connect(aCallNode2, aCallNode1);
		assertTrue(SequenceDiagramSemanticConstraints.returnEdge().satisfied(aReturnEdge, aDiagram));
	}

	@Test
	void testCallEdgeEndNotCallEdge()
	{
		createDiagram();
		aReturnEdge.connect(aCallNode2, aCallNode1);
		assertTrue(SequenceDiagramSemanticConstraints.callEdgeEnd().satisfied(aReturnEdge, aDiagram));
	}

	@Test
	void testCallEdgeEndEndNotParameter()
	{
		createDiagram();
		aCallEdge.connect(aCallNode2, aCallNode1);
		assertTrue(SequenceDiagramSemanticConstraints.callEdgeEnd().satisfied(aCallEdge, aDiagram));
	}

	@Test
	void testCallEdgeEndEndOnLifeLine()
	{
		createDiagram();
		aCallEdge.connect(aParameter2, aCallNode1);
		assertTrue(SequenceDiagramSemanticConstraints.callEdgeEnd().satisfied(aCallEdge, aDiagram));
	}

	@Test
	void testCallEdgeEndEndOnTopRectangle()
	{
		createDiagram();
		aCallEdge.connect(aParameter2, aCallNode1);
		assertTrue(SequenceDiagramSemanticConstraints.callEdgeEnd().satisfied(aCallEdge, aDiagram));
	}

	@Test
	void testSingleEntryPointNotACallEdge()
	{
		createDiagram();
		aReturnEdge.connect(aParameter1, aParameter1);
		assertTrue(SequenceDiagramSemanticConstraints.singleEntryPoint().satisfied(aReturnEdge, aDiagram));
	}

	@Test
	void testSingleEntryPointNotStartingOnAParameterNode()
	{
		createDiagram();
		aCallEdge.connect(aCallNode1, aCallNode1);
		assertTrue(SequenceDiagramSemanticConstraints.singleEntryPoint().satisfied(aCallEdge, aDiagram));
	}

	@Test
	void testSingleEntryPointStartingOnParameterNodeNotSatisfied()
	{
		createDiagram();
		aCallEdge.connect(aParameter1, aParameter1);
		assertFalse(SequenceDiagramSemanticConstraints.singleEntryPoint().satisfied(aCallEdge, aDiagram));
	}

	@Test
	void testSingleEntryPointStartingOnParameterNodeSatisfied()
	{
		aDiagram.addRootNode(aParameter1);
		aCallEdge.connect(aParameter1, aParameter1);
		assertTrue(SequenceDiagramSemanticConstraints.singleEntryPoint().satisfied(aCallEdge, aDiagram));
	}
}
