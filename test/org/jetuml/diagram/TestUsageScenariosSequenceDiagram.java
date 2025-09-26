/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2025 by McGill University.
 *     
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package org.jetuml.diagram;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.Optional;

import org.jetuml.diagram.builder.DiagramOperationProcessor;
import org.jetuml.diagram.builder.SequenceDiagramBuilder;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.ConstructorEdge;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.edges.ReturnEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.geom.Point;
import org.jetuml.rendering.SequenceDiagramRenderer;
import org.junit.jupiter.api.Test;

public class TestUsageScenariosSequenceDiagram extends AbstractTestUsageScenarios {

	private ImplicitParameterNode aParameterNode1 = new ImplicitParameterNode();
	private ImplicitParameterNode aParameterNode2 = new ImplicitParameterNode();
	private ImplicitParameterNode aParameterNode3 = new ImplicitParameterNode();
	private CallNode aCallNode1 = new CallNode();
	private CallNode aCallNode2 = new CallNode();
	private CallNode aCallNode3 = new CallNode();
	private CallNode aCallNode4 = new CallNode();
	private CallNode aCallNode5 = new CallNode();
	private CallEdge aCallEdge1 = new CallEdge();
	private CallEdge aCallEdge2 = new CallEdge();
	private CallEdge aCallEdge3 = new CallEdge();
	private CallEdge aCallEdge4 = new CallEdge();
	private ReturnEdge aReturnEdge = new ReturnEdge();
	private DiagramAccessor aDiagramAccessor = new DiagramAccessor(diagram());
	private ConstructorEdge aConstructorEdge = new ConstructorEdge();
	
	TestUsageScenariosSequenceDiagram () {
		super(new Diagram(DiagramType.SEQUENCE));
	}

	private void createSampleDiagram() {
		diagram().addRootNode(aParameterNode1);
		diagram().addRootNode(aParameterNode2);
		diagram().addRootNode(aParameterNode3);
		aParameterNode1.addChild(aCallNode1);
		aParameterNode2.addChild(aCallNode2);
		aParameterNode2.addChild(aCallNode3);
		aParameterNode3.addChild(aCallNode4);
		aParameterNode3.addChild(aCallNode5);
		aDiagramAccessor.connectAndAdd(aConstructorEdge, aCallNode1, aCallNode2);
		aDiagramAccessor.connectAndAdd(aReturnEdge, aCallNode2, aCallNode1);
		aDiagramAccessor.connectAndAdd(aCallEdge1, aCallNode2, aCallNode3);
		aDiagramAccessor.connectAndAdd(aCallEdge2, aCallNode3, aCallNode4);
		aDiagramAccessor.connectAndAdd(aCallEdge3, aCallNode2, aCallNode5);
	}

	@Test
	public void testCreateAndLinkParameterNode() {
		setProperty(aParameterNode1.properties().get(PropertyName.NAME), "client");
		setProperty(aParameterNode2.properties().get(PropertyName.NAME), "platform");

		addNode(aParameterNode1, new Point(5, 0));
		addNode(aParameterNode2, new Point(25, 0));
		assertEquals(2, numberOfRootNodes());
		assertEquals("client", aParameterNode1.getName());
		assertEquals("platform", aParameterNode2.getName());

		assertTrue(validator().isValid());
		// testing one invalid case
		addEdge(aReturnEdge, new Point(7, 0), new Point(26, 0));
		assertFalse(validator().isValid());
	}

	@Test
	public void testCreateCallNodeAndLinkParameterNode() {
		addNode(aParameterNode1, new Point(5, 0));
		addNode(aParameterNode2, new Point(125, 0));

		assertEquals(2, numberOfRootNodes());

		assertTrue(validator().isValid());

		addEdge(aReturnEdge, new Point(7, 75), new Point(26, 0));
		addEdge(aNoteEdge, new Point(7, 75), new Point(26, 0));

		addEdge(aCallEdge1, new Point(43, 85), new Point(130, 85));
		assertEquals(3, numberOfEdges());
		assertFalse(validator().isValid());
	}

	/**
	 * Testing adding more edges to the diagram.
	 */
	@Test
	public void testAddMoreEdges() {
		ImplicitParameterNode newParaNode = new ImplicitParameterNode();
		addNode(aParameterNode1, new Point(10, 0));
		addNode(aParameterNode2, new Point(110, 0));
		addNode(newParaNode, new Point(210, 0));
		addEdge(aCallEdge1, new Point(45, 85), new Point(115, 75));
		assertEquals(1, numberOfEdges());

		builder().renderer().getBounds(); // Trigger rendering pass

		ReturnEdge returnEdge1 = new ReturnEdge();
		addEdge(returnEdge1, new Point(145, 105), new Point(45, 90));
		assertEquals(2, numberOfEdges());

		// call edge from first CallNode to third ParameterNode life line
		addEdge(new CallEdge(), new Point(45, 85), new Point(210, 75));
		assertEquals(3, numberOfEdges());
	}

	@Test
	public void testNoteNode() {
		addNode(aParameterNode1, new Point(10, 0));
		addNode(aParameterNode2, new Point(110, 0));
		addEdge(aCallEdge1, new Point(45, 85), new Point(145, 85));

		builder().renderer().getBounds(); // Trigger rendering pass

		NoteNode noteNode = new NoteNode();
		NoteEdge noteEdge1 = new NoteEdge();
		NoteEdge noteEdge2 = new NoteEdge();
		NoteEdge noteEdge3 = new NoteEdge();
		NoteEdge noteEdge4 = new NoteEdge();
		NoteEdge noteEdge5 = new NoteEdge();
		addNode(noteNode, new Point(55, 55));
		addEdge(noteEdge1, new Point(60, 60), new Point(87, 65));
		addEdge(noteEdge2, new Point(62, 68), new Point(47, 75));
		addEdge(noteEdge3, new Point(63, 69), new Point(47, 35));
		addEdge(noteEdge4, new Point(64, 70), new Point(17, 5));
		addEdge(noteEdge5, new Point(65, 60), new Point(67, 265));

		assertEquals(6, numberOfEdges());
		assertEquals(8, numberOfRootNodes());

		assertTrue(validator().isValid());
		// from ParameterNode to NoteNode (invalid)
		addEdge(new NoteEdge(), new Point(10, 10), new Point(62, 68));

		// from CallNode to NoteNode (invalid)
		addEdge(new NoteEdge(), new Point(10, 10), new Point(62, 68));
		assertFalse(validator().isValid());
		assertEquals(8, numberOfEdges());
	}

	@Test
	public void testIndividualNodeMovement() {
		addNode(aParameterNode1, new Point(10, 0));
		addNode(aParameterNode2, new Point(110, 0));
		addEdge(aCallEdge1, new Point(42, 85), new Point(142, 85));

		aParameterNode1.translate(5, 15);
		assertEquals(new Point(15, 15), aParameterNode1.position());
		aParameterNode1.translate(25, 0);
		assertEquals(new Point(40, 15), aParameterNode1.position());

		aParameterNode2.translate(105, 25);
		assertEquals(new Point(215, 25), aParameterNode2.position());
		aParameterNode2.translate(0, 15);
		assertEquals(new Point(215, 40), aParameterNode2.position());
	}

	@Test
	public void testDeleteSingleParameterNode() {
		addNode(aParameterNode1, new Point(10, 0));
		addNode(aCallNode1, new Point(15, 65));
		select(aParameterNode1);
		deleteSelected();
		assertEquals(0, numberOfRootNodes());
		undo();
		assertEquals(1, numberOfRootNodes());
	}

	@Test
	public void testDeleteSingleCallNode() {
		addNode(aParameterNode1, new Point(10, 0));
		addNode(aCallNode1, new Point(15, 75));

		select(aCallNode1);
		deleteSelected();

		assertEquals(1, numberOfRootNodes());
		assertEquals(0, aParameterNode1.getChildren().size());

		undo();
		assertEquals(1, aParameterNode1.getChildren().size());
	}

	@Test
	public void testDeleteParameterNodeInCallSequence() {
		ImplicitParameterNode newParameterNode = new ImplicitParameterNode();
		addNode(aParameterNode1, new Point(10, 0));
		addNode(aParameterNode2, new Point(110, 0));
		addNode(newParameterNode, new Point(210, 0));
		addEdge(aCallEdge1, new Point(45, 85), new Point(115, 85));
		ReturnEdge returnEdge1 = new ReturnEdge();

		builder().renderer().getBounds(); // Trigger rendering pass

		addEdge(returnEdge1, new Point(145, 105), new Point(45, 90));
		CallEdge callEdge2 = new CallEdge();
		addEdge(callEdge2, new Point(45, 85), new Point(210, 75));

		select(aParameterNode1);
		deleteSelected();
		assertEquals(2, numberOfRootNodes());
		assertEquals(0, newParameterNode.getChildren().size());
		assertEquals(0, aParameterNode2.getChildren().size());
		assertEquals(0, numberOfEdges());

		undo();
		assertEquals(3, numberOfRootNodes());
		assertEquals(1, newParameterNode.getChildren().size());
		assertEquals(1, aParameterNode2.getChildren().size());
		assertEquals(3, numberOfEdges());
	}

	@Test
	public void testDeleteUndoParameterWithTwoCallNodes() {
		ImplicitParameterNode newParameterNode1 = new ImplicitParameterNode();
		ImplicitParameterNode newParameterNode2 = new ImplicitParameterNode();
		newParameterNode2.translate(100, 0);
		diagram().addRootNode(newParameterNode1);
		diagram().addRootNode(newParameterNode2);

		CallNode caller = new CallNode();
		newParameterNode1.addChild(caller);

		CallNode callee1 = new CallNode();
		newParameterNode2.addChild(callee1);

		CallNode callee2 = new CallNode();
		newParameterNode2.addChild(callee2);

		CallEdge callEdge1 = new CallEdge();
		callEdge1.connect(caller, callee1);
		diagram().addEdge(callEdge1);

		CallEdge callEdge2 = new CallEdge();
		callEdge2.connect(caller, callee2);
		diagram().addEdge(callEdge2);

		select(caller);
		deleteSelected();
		assertEquals(0, newParameterNode1.getChildren().size());

		undo();
		assertEquals(1, newParameterNode1.getChildren().size());
	}

	@Test
	public void testDeleteMiddleCallNode() {
		ImplicitParameterNode newParameterNode = new ImplicitParameterNode();
		CallNode middleCallNode = new CallNode();
		aParameterNode1.translate(10, 0);
		diagram().addRootNode(aParameterNode1);
		aParameterNode2.translate(110, 0);
		diagram().addRootNode(aParameterNode2);
		newParameterNode.translate(210, 0);
		diagram().addRootNode(newParameterNode);
		aParameterNode1.addChild(aCallNode1);
		aParameterNode2.addChild(middleCallNode);
		CallNode end = new CallNode();
		newParameterNode.addChild(end);

		aCallEdge1.connect(aCallNode1, middleCallNode);
		diagram().addEdge(aCallEdge1);

		CallEdge edge = new CallEdge();
		edge.connect(middleCallNode, end);
		diagram().addEdge(edge);

		ReturnEdge redge = new ReturnEdge();
		redge.connect(middleCallNode, aCallNode1);
		diagram().addEdge(redge);

		select(middleCallNode);
		deleteSelected();

		assertEquals(0, aParameterNode1.getChildren().size());
		assertEquals(0, aParameterNode2.getChildren().size());
		assertEquals(0, newParameterNode.getChildren().size());
		assertEquals(0, numberOfEdges());

		undo();
		assertEquals(1, aParameterNode1.getChildren().size());
		assertEquals(1, aParameterNode2.getChildren().size());
		assertEquals(1, newParameterNode.getChildren().size());
		assertEquals(3, numberOfEdges());
	}

	@Test
	public void testDeleteReturnEdge() {
		CallNode middleCallNode = new CallNode();
		ReturnEdge returnEdge = new ReturnEdge();
		aParameterNode1.translate(10, 0);
		diagram().addRootNode(aParameterNode1);
		aParameterNode2.translate(110, 0);
		diagram().addRootNode(aParameterNode2);
		aParameterNode1.addChild(aCallNode1);
		aParameterNode2.addChild(middleCallNode);
		CallEdge callEdge = new CallEdge();
		callEdge.connect(aCallNode1, middleCallNode);
		diagram().addEdge(callEdge);
		returnEdge.connect(middleCallNode, aCallNode1);
		diagram().addEdge(returnEdge);

		select(returnEdge);
		deleteSelected();

		assertEquals(1, aParameterNode1.getChildren().size());
		assertEquals(1, aParameterNode2.getChildren().size());
		assertEquals(1, numberOfEdges());

		undo();
		assertEquals(2, numberOfEdges());
	}

	@Test
	void testGetNestingDepth() {
		createSampleDiagram();
		assertEquals(0, ((SequenceDiagramRenderer) builder().renderer()).getNestingDepth(aCallNode1));
		assertEquals(0, ((SequenceDiagramRenderer) builder().renderer()).getNestingDepth(aCallNode2));
		assertEquals(1, ((SequenceDiagramRenderer) builder().renderer()).getNestingDepth(aCallNode3));
		assertEquals(0, ((SequenceDiagramRenderer) builder().renderer()).getNestingDepth(aCallNode4));
		assertEquals(0, ((SequenceDiagramRenderer) builder().renderer()).getNestingDepth(aCallNode5));
	}

	@Test
	void testHasEntryPoint_No() {
		assertFalse(new SequenceDiagramRenderer(new Diagram(DiagramType.SEQUENCE)).hasEntryPoint());
	}

	@Test
	void testHasEntryPoint_Yes() {
		createSampleDiagram();
		assertTrue(((SequenceDiagramRenderer) builder().renderer()).hasEntryPoint());
	}

	@Test
	void testGetCallerNoCaller() {
		createSampleDiagram();
		assertFalse(((SequenceDiagramRenderer) builder().renderer()).getCaller(aCallNode1).isPresent());
	}

	@Test
	void testGetCallerSameParameter() {
		createSampleDiagram();
		assertSame(aCallNode2, ((SequenceDiagramRenderer) builder().renderer()).getCaller(aCallNode3).get());
	}

	@Test
	void testGetCallerDifferentParameter() {
		createSampleDiagram();
		assertSame(aCallNode1, ((SequenceDiagramRenderer) builder().renderer()).getCaller(aCallNode2).get());
		assertSame(aCallNode2, ((SequenceDiagramRenderer) builder().renderer()).getCaller(aCallNode3).get());
		assertSame(aCallNode3, ((SequenceDiagramRenderer) builder().renderer()).getCaller(aCallNode4).get());
		assertSame(aCallNode2, ((SequenceDiagramRenderer) builder().renderer()).getCaller(aCallNode5).get());
	}

	@Test
	void testGetEdgeStartNoteEdge() {
		createSampleDiagram();
		NoteNode noteNode = new NoteNode();
		NoteEdge noteEdge = new NoteEdge();
		aDiagramAccessor.connectAndAdd(noteEdge, aCallNode1, noteNode);
		Optional<DiagramElement> start = ((SequenceDiagramRenderer) builder().renderer())
				.getStartNodeIfExclusive(noteEdge);
		assertTrue(start.isEmpty());
	}

	@Test
	void testGetEdgeStartHasNoOtherFlows() {
		createSampleDiagram();
		Optional<DiagramElement> start = ((SequenceDiagramRenderer) builder().renderer())
				.getStartNodeIfExclusive(aConstructorEdge);
		assertTrue(start.isPresent());
		assertSame(aCallNode1, start.get());
	}

	@Test
	void testGetEdgeStartHasOtherFlowsInConstructorCall() {
		createSampleDiagram();
		CallNode callNode = new CallNode();
		aParameterNode2.addChild(callNode);
		aDiagramAccessor.connectAndAdd(aCallEdge4, aCallNode1, callNode);

		Optional<DiagramElement> start = ((SequenceDiagramRenderer) builder().renderer())
				.getStartNodeIfExclusive(aConstructorEdge);
		assertTrue(start.isPresent());
		assertSame(aCallNode1, start.get());
	}

	@Test
	void testGetEdgeStartHasOtherFlowsBesidesConstructorCall() {
		createSampleDiagram();
		CallNode callNode = new CallNode();
		aParameterNode3.addChild(callNode);
		aDiagramAccessor.connectAndAdd(aCallEdge4, aCallNode1, callNode);

		Optional<DiagramElement> start = ((SequenceDiagramRenderer) builder().renderer())
				.getStartNodeIfExclusive(aConstructorEdge);
		assertTrue(start.isEmpty());
	}

	@Test
	void testGetEdgeStartHasOtherFlowsNestedConstructorCall() {
		createSampleDiagram();
		ImplicitParameterNode parameter = new ImplicitParameterNode();
		CallNode callNode = new CallNode();
		ConstructorEdge constructorEdge = new ConstructorEdge();
		diagram().addRootNode(parameter);
		parameter.addChild(callNode);
		aDiagramAccessor.connectAndAdd(constructorEdge, aCallNode2, callNode);

		Optional<DiagramElement> start = ((SequenceDiagramRenderer) builder().renderer())
				.getStartNodeIfExclusive(constructorEdge);
		assertTrue(start.isEmpty());
	}

	@Test
	public void testCopyPasteParameterNode() {
		diagram().addRootNode(aParameterNode1);

		select(aParameterNode1);
		copy();
		paste();

		assertEquals(2, numberOfRootNodes());
	}

	@Test
	public void testCutPasteParameterNode() {
		diagram().addRootNode(aParameterNode1);

		select(aParameterNode1);

		cut();
		assertEquals(0, numberOfRootNodes());

		paste();

		assertEquals(1, numberOfRootNodes());
	}

	@Test
	public void testCopyPasteParameterNodeWithCallNode() {
		diagram().addRootNode(aParameterNode1);
		aParameterNode1.addChild(aCallNode1);

		select(aParameterNode1);

		copy();
		paste();

		assertEquals(2, numberOfRootNodes());
		assertEquals(1, (((ImplicitParameterNode) (getRootNode(1))).getChildren().size()));
	}

	@Test
	public void testCopyPasteDifferentDiagrams() {
		diagram().addRootNode(aParameterNode1);
		diagram().addRootNode(aParameterNode2);
		aParameterNode2.translate(110, 0);

		aParameterNode1.addChild(aCallNode1);
		aParameterNode2.addChild(aCallNode2);

		aCallEdge1.connect(aCallNode1, aCallNode2);
		diagram().addEdge(aCallEdge1);

		assertSame(aCallNode1, aCallEdge1.start());
		assertSame(aCallNode2, aCallEdge1.end());

		select(aParameterNode1, aParameterNode2, aCallEdge1);
		copy();

		Diagram diagram2 = new Diagram(DiagramType.SEQUENCE);
		SequenceDiagramBuilder builder2 = new SequenceDiagramBuilder(diagram2);
		DiagramOperationProcessor processor2 = new DiagramOperationProcessor();
		processor2.executeNewOperation(builder2.createAddElementsOperation(getClipboardContent()));

		Iterator<Node> nodes = diagram2.rootNodes().iterator();
		Node node1 = nodes.next();
		CallNode callNode = (CallNode) ((ImplicitParameterNode) node1).getChildren().get(0);
		assertSame(node1, callNode.getParent());
	}

	@Test
	public void testCopyPasteSequenceDiagram() {
		diagram().addRootNode(aParameterNode1);
		aParameterNode2.translate(110, 0);
		diagram().addRootNode(aParameterNode2);

		ImplicitParameterNode newParameterNode = new ImplicitParameterNode();
		newParameterNode.translate(200, 0);
		diagram().addRootNode(newParameterNode);

		aParameterNode1.addChild(aCallNode1);
		CallNode middleCallNode = new CallNode();
		aParameterNode2.addChild(middleCallNode);
		newParameterNode.addChild(aCallNode2);

		aCallEdge1.connect(aCallNode1, middleCallNode);
		diagram().addEdge(aCallEdge1);

		aCallEdge2.connect(middleCallNode, aCallNode2);
		diagram().addEdge(aCallEdge2);

		aReturnEdge.connect(middleCallNode, aCallNode1);
		diagram().addEdge(aReturnEdge);

		selectAll();
		copy();
		paste();

		assertEquals(6, numberOfRootNodes());
		assertEquals(6, numberOfEdges());
	}
}
