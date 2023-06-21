package org.jetuml.diagram.validator.constraints;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.geom.Point;
import org.junit.jupiter.api.Test;

public class TestEdgeSemanticConstraints
{
	private Diagram aDiagram = new Diagram(DiagramType.CLASS);
	private ClassNode aNode1 = new ClassNode();
	private ClassNode aNode2 = new ClassNode();
	private PointNode aPointNode = new PointNode();
	private DependencyEdge aEdge1 = new DependencyEdge();

	private NoteNode aNote = new NoteNode();

	private void createDiagram()
	{
		aNode2.moveTo(new Point(0, 100));
		aNote.moveTo(new Point(100, 100));
		aDiagram.addRootNode(aNode1);
		aDiagram.addRootNode(aNode2);
		aDiagram.addRootNode(aNote);
		aPointNode.moveTo(new Point(200, 200));
		aDiagram.addRootNode(aPointNode);
	}

	@Test
	void testMaxEdgesOne()
	{
		createDiagram();
		aEdge1.connect(aNode1, aNode2);
		assertTrue(EdgeSemanticConstraints.maxEdges(1).satisfied(aEdge1, aDiagram));
		aDiagram.addEdge(aEdge1);
		DependencyEdge edge = new DependencyEdge();
		edge.connect(aNode1, aNode2);
		assertTrue(EdgeSemanticConstraints.maxEdges(1).satisfied(edge, aDiagram));
	}

	@Test
	void testMaxEdgesTwo()
	{
		createDiagram();
		aEdge1.connect(aNode1, aNode2);
		aDiagram.addEdge(aEdge1);
		assertTrue(EdgeSemanticConstraints.maxEdges(2).satisfied(aEdge1, aDiagram));
		DependencyEdge edge = new DependencyEdge();
		edge.connect(aNode1, aNode2);
		assertTrue(EdgeSemanticConstraints.maxEdges(2).satisfied(edge, aDiagram));
		aDiagram.addEdge(edge);
		assertTrue(EdgeSemanticConstraints.maxEdges(2).satisfied(edge, aDiagram));
	}

	@Test
	void testMaxEdgesNodesMatchNoMatch()
	{
		createDiagram();
		aEdge1.connect(aNode1, aNode2);
		aDiagram.addEdge(aEdge1);
		ClassNode node3 = new ClassNode();
		DependencyEdge edge = new DependencyEdge();
		edge.connect(aNode1, node3);
		assertTrue(EdgeSemanticConstraints.maxEdges(1).satisfied(edge, aDiagram));
	}

	@Test
	void testMaxEdgesNodesNoMatchMatch()
	{
		createDiagram();
		aEdge1.connect(aNode1, aNode2);
		aDiagram.addEdge(aEdge1);
		ClassNode node3 = new ClassNode();
		DependencyEdge edge = new DependencyEdge();
		edge.connect(node3, aNode2);
		assertTrue(EdgeSemanticConstraints.maxEdges(1).satisfied(edge, aDiagram));
	}

	@Test
	void testMaxEdgesNodesNoMatchNoMatch()
	{
		createDiagram();
		aEdge1.connect(aNode1, aNode2);
		aDiagram.addEdge(aEdge1);
		ClassNode node3 = new ClassNode();
		DependencyEdge edge = new DependencyEdge();
		edge.connect(node3, new ClassNode());
		assertTrue(EdgeSemanticConstraints.maxEdges(1).satisfied(edge, aDiagram));
	}

	@Test
	void testMaxEdgesNodesDifferentEdgeType()
	{
		createDiagram();
		aEdge1.connect(aNode1, aNode2);
		aDiagram.addEdge(aEdge1);
		NoteEdge edge = new NoteEdge();
		edge.connect(aNode1, aNode2);
		assertTrue(EdgeSemanticConstraints.maxEdges(1).satisfied(edge, aDiagram));
	}

	@Test
	void testNodeSelfEdgeTrue()
	{
		createDiagram();
		aEdge1.connect(aNode1, aNode2);
		assertTrue(EdgeSemanticConstraints.noSelfEdge().satisfied(aEdge1, aDiagram));
	}

	@Test
	void testNodeSelfEdgeFalse()
	{
		createDiagram();
		aEdge1.connect(aNode1, aNode1);
		assertFalse(EdgeSemanticConstraints.noSelfEdge().satisfied(aEdge1, aDiagram));
	}
}
