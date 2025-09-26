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

import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.edges.UseCaseAssociationEdge;
import org.jetuml.diagram.edges.UseCaseDependencyEdge;
import org.jetuml.diagram.edges.UseCaseGeneralizationEdge;
import org.jetuml.diagram.nodes.ActorNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.diagram.nodes.UseCaseNode;
import org.jetuml.geom.Point;
import org.junit.jupiter.api.Test;

public class TestUsageScenariosUseCaseDiagram extends AbstractTestUsageScenarios {

	private ActorNode aActorNode1 = new ActorNode();
	private ActorNode aActorNode2 = new ActorNode();
	private UseCaseNode aUseCaseNode1 = new UseCaseNode();
	private UseCaseNode aUseCaseNode2 = new UseCaseNode();
	private UseCaseAssociationEdge aAssociationEdge = new UseCaseAssociationEdge();
	private UseCaseDependencyEdge aDependencyEdge = new UseCaseDependencyEdge();
	private UseCaseGeneralizationEdge aGeneralEdge = new UseCaseGeneralizationEdge();

	TestUsageScenariosUseCaseDiagram() {
		super(new Diagram(DiagramType.USECASE));
	}
	
	@Test
	void testCreateUseCaseDiagram() {
		addNode(aActorNode1, new Point(20, 20));
		setProperty(aActorNode1.properties().get(PropertyName.NAME), "Car");
		assertEquals(1, numberOfRootNodes());
		assertEquals("Car", aActorNode1.getName());

		addNode(aUseCaseNode1, new Point(120, 80));
		setProperty(aUseCaseNode1.properties().get(PropertyName.NAME), "driving");
		assertEquals(2, numberOfRootNodes());
		assertEquals("driving", aUseCaseNode1.getName());

		addNode(aNoteNode, new Point(50, 50));
		setProperty(aNoteNode.properties().get(PropertyName.NAME), "something...\nsomething");
		assertEquals(3, numberOfRootNodes());
		assertEquals("something...\nsomething", aNoteNode.getName());
	}

	@Test
	void testGeneralEdgeCreation() {
		addNode(aActorNode1, new Point(20, 20));
		assertTrue(diagram().contains(aActorNode1));
		assertEquals(new Point(20, 20), aActorNode1.position());

		addNode(aActorNode2, new Point(250, 20));
		assertTrue(diagram().contains(aActorNode2));
		assertEquals(new Point(250, 20), aActorNode2.position());

		addNode(aUseCaseNode1, new Point(80, 20));
		assertTrue(diagram().contains(aUseCaseNode1));
		assertEquals(new Point(80, 20), aUseCaseNode1.position());

		addNode(aUseCaseNode2, new Point(140, 20));
		assertTrue(diagram().contains(aUseCaseNode2));
		assertEquals(new Point(140, 20), aUseCaseNode2.position());

		addEdge(aAssociationEdge, new Point(20, 20), new Point(250, 20)); // aActorNode1 -> aUseCaseNode2
		assertTrue(diagram().contains(aAssociationEdge));
		assertSame(aActorNode1, aAssociationEdge.start());
		assertSame(aUseCaseNode2, aAssociationEdge.end());

		addEdge(aDependencyEdge, new Point(80, 20), new Point(250, 20)); // aUseCaseNode1 -> aUseCaseNode2
		assertTrue(diagram().contains(aDependencyEdge));
		assertSame(aUseCaseNode1, aDependencyEdge.start());
		assertSame(aUseCaseNode2, aDependencyEdge.end());

		addEdge(aGeneralEdge, new Point(20, 20), new Point(140, 20)); // aActorNode1 -> aUseCaseNode2
		assertTrue(diagram().contains(aGeneralEdge));
		assertSame(aActorNode1, aGeneralEdge.start());
		assertSame(aUseCaseNode2, aGeneralEdge.end());

		UseCaseAssociationEdge useCaseAssociationEdge2 = new UseCaseAssociationEdge();
		addEdge(useCaseAssociationEdge2, new Point(80, 20), new Point(140, 20)); // aUseCaseNode1 -> aUseCaseNode2
		assertTrue(diagram().contains(useCaseAssociationEdge2));
		assertSame(aUseCaseNode1, useCaseAssociationEdge2.start());
		assertSame(aUseCaseNode2, useCaseAssociationEdge2.end());

		UseCaseDependencyEdge useCaseDependencyEdge2 = new UseCaseDependencyEdge();
		addEdge(useCaseDependencyEdge2, new Point(20, 20), new Point(250, 20)); // aActorNode1 -> aUseCaseNode2
		assertTrue(diagram().contains(useCaseDependencyEdge2));
		assertSame(aActorNode1, useCaseDependencyEdge2.start());
		assertSame(aUseCaseNode2, useCaseDependencyEdge2.end());

		assertEquals(5, numberOfEdges());

		// connect nodes with NoteEdge (invalid edges)
		assertTrue(validator().isValid());
		addEdge(new NoteEdge(), new Point(80, 20), new Point(140, 20));
		addEdge(new NoteEdge(), new Point(20, 20), new Point(250, 20));
		assertFalse(validator().isValid());
	}

	@Test
	void testNoteEdgeCreation() {
		addNode(aActorNode1, new Point(20, 20));
		addNode(aActorNode2, new Point(250, 20));
		addNode(aUseCaseNode1, new Point(80, 20));
		addNode(aUseCaseNode2, new Point(140, 20));
		assertEquals(4, numberOfRootNodes());

		NoteNode noteNode = new NoteNode();
		addNode(noteNode, new Point(100, 100));
		assertTrue(diagram().contains(noteNode));

		NoteEdge noteEdge1 = new NoteEdge();
		NoteEdge noteEdge2 = new NoteEdge();
		NoteEdge noteEdge3 = new NoteEdge();

		assertTrue(validator().isValid());
		// adding wrong note edge between actor nodes
		addEdge(noteEdge1, new Point(20, 20), new Point(250, 20));
		assertFalse(validator().isValid());

		addEdge(noteEdge1, new Point(20, 20), new Point(100, 100));
		assertTrue(diagram().contains(noteEdge1));
		assertSame(noteEdge1.start(), aActorNode1);
		assertSame(noteEdge1.end(), noteNode);

		addEdge(noteEdge2, new Point(85, 25), new Point(110, 110));
		assertTrue(diagram().contains(noteEdge2));
		assertSame(noteEdge2.start(), aUseCaseNode1);
		assertSame(noteEdge2.end(), noteNode);

		// if begin with a NoteNode, the end point can be anywhere
		addEdge(noteEdge3, new Point(100, 100), new Point(9, 9));
		assertTrue(diagram().contains(noteEdge3));
		assertSame(noteEdge3.start(), noteNode);
		Node end = noteEdge3.end();
		assertEquals(PointNode.class, end.getClass());
		assertEquals(new Point(9, 9), end.position());
	}

	@Test
	void testIndividualNodeMovement() {
		addNode(aActorNode1, new Point(20, 20));
		addNode(aUseCaseNode1, new Point(80, 20));
		addNode(aNoteNode, new Point(100, 100));

		moveNode(aActorNode1, 3, 12);
		moveNode(aUseCaseNode1, 3, 12);
		moveNode(aNoteNode, 40, 20);

		assertEquals(new Point(23, 32), aActorNode1.position());
		assertEquals(new Point(83, 32), aUseCaseNode1.position());
		assertEquals(new Point(140, 120), aNoteNode.position());
	}

	@Test
	void testNodesAndEdgesMovement() {
		addNode(aActorNode1, new Point(20, 20));
		addNode(aActorNode2, new Point(250, 20));
		addNode(aUseCaseNode1, new Point(80, 20));
		addNode(aUseCaseNode2, new Point(140, 20));
		addNode(aNoteNode, new Point(100, 100));
		addEdge(aAssociationEdge, new Point(20, 20), new Point(250, 20));
		addEdge(aDependencyEdge, new Point(80, 20), new Point(250, 20));
		addEdge(aGeneralEdge, new Point(20, 20), new Point(140, 20));
		addEdge(aNoteEdge, new Point(85, 25), new Point(110, 110));

		diagram().rootNodes().forEach(node -> node.translate(26, 37));

		assertEquals(new Point(46, 57), aActorNode1.position());
		assertEquals(new Point(276, 57), aActorNode2.position());
		assertEquals(new Point(106, 57), aUseCaseNode1.position());
		assertEquals(new Point(166, 57), aUseCaseNode2.position());
		assertEquals(new Point(126, 137), aNoteNode.position());
	}

	@Test
	void testDeleteNode() {
		addNode(aActorNode1, new Point(20, 20));
		assertTrue(diagram().contains(aActorNode1));
		select(aActorNode1);
		deleteSelected();
		assertEquals(0, numberOfRootNodes());

		undo();
		assertEquals(1, numberOfRootNodes());
		assertTrue(diagram().contains(aActorNode1));

		addNode(aNoteNode, new Point(75, 75));
		assertEquals(2, numberOfRootNodes());
		assertTrue(diagram().contains(aNoteNode));
		select(aNoteNode);
		deleteSelected();
		assertEquals(1, numberOfRootNodes());
		assertTrue(diagram().contains(aActorNode1));
		assertFalse(diagram().contains(aNoteNode));
		undo();
		assertEquals(2, numberOfRootNodes());
		assertTrue(diagram().contains(aActorNode1));
		assertTrue(diagram().contains(aNoteNode));

		addNode(aUseCaseNode1, new Point(420, 420));
		assertTrue(diagram().contains(aUseCaseNode1));
		assertTrue(diagram().contains(aActorNode1));
		select(aActorNode1, aUseCaseNode1);
		deleteSelected();
		assertEquals(1, numberOfRootNodes());
		assertFalse(diagram().contains(aActorNode1));
		assertTrue(diagram().contains(aNoteNode));
		assertFalse(diagram().contains(aUseCaseNode1));

		undo();
		assertEquals(3, numberOfRootNodes());
		assertTrue(diagram().contains(aActorNode1));
		assertTrue(diagram().contains(aNoteNode));
		assertTrue(diagram().contains(aUseCaseNode1));
	}

	@Test
	void testDeleteEdge() {
		addNode(aActorNode1, new Point(20, 20));
		addNode(aActorNode2, new Point(250, 20));
		addNode(aUseCaseNode1, new Point(80, 20));
		addNode(aUseCaseNode2, new Point(140, 20));
		addNode(aNoteNode, new Point(100, 100));
		addEdge(aAssociationEdge, new Point(20, 20), new Point(250, 20));
		addEdge(aDependencyEdge, new Point(80, 20), new Point(250, 20));
		addEdge(aGeneralEdge, new Point(20, 20), new Point(140, 20));
		addEdge(aNoteEdge, new Point(85, 25), new Point(110, 110));

		select(aAssociationEdge);
		deleteSelected();
		assertEquals(3, numberOfEdges());
		assertFalse(diagram().contains(aAssociationEdge));

		select(aGeneralEdge);
		deleteSelected();
		assertEquals(2, numberOfEdges());
		assertFalse(diagram().contains(aGeneralEdge));

		undo();
		assertEquals(3, numberOfEdges());
		assertTrue(diagram().contains(aGeneralEdge));
		undo();
		assertEquals(4, numberOfEdges());
		assertTrue(diagram().contains(aAssociationEdge));
	}

	@Test
	void testDeleteCombinationNodeAndEdge() {
		addNode(aActorNode1, new Point(20, 20));
		addNode(aActorNode2, new Point(250, 20));
		addNode(aUseCaseNode1, new Point(80, 20));
		addNode(aUseCaseNode2, new Point(140, 20));
		addNode(aNoteNode, new Point(100, 100));
		addEdge(aAssociationEdge, new Point(20, 20), new Point(250, 20));
		addEdge(aDependencyEdge, new Point(80, 20), new Point(250, 20));
		addEdge(aGeneralEdge, new Point(20, 20), new Point(140, 20));
		addEdge(aNoteEdge, new Point(85, 25), new Point(110, 110));

		select(aActorNode1, aAssociationEdge, aDependencyEdge, aGeneralEdge, aNoteEdge);

		deleteSelected();

		assertEquals(4, numberOfRootNodes());
		assertFalse(diagram().contains(aActorNode1));
		assertEquals(0, numberOfEdges());

		undo();
		assertEquals(5, numberOfRootNodes());
		assertTrue(diagram().contains(aActorNode1));
		assertEquals(4, numberOfEdges());

		/*
		 * now delete aUseCaseNode2, aActorNode2 and aGeneralEdge aAssociationEdge and
		 * aDependencyEdge will also be deleted since they are connected to aActorNode2
		 */
		select(aUseCaseNode2, aActorNode2, aGeneralEdge);
		deleteSelected();

		assertEquals(3, numberOfRootNodes());
		assertEquals(1, numberOfEdges());
		assertFalse(diagram().contains(aAssociationEdge));
		assertFalse(diagram().contains(aDependencyEdge));

		undo();
		assertEquals(5, numberOfRootNodes());
		assertEquals(4, numberOfEdges());
	}

	@Test
	void testCopyPasteNode() {
		addNode(aActorNode1, new Point(20, 20));
		addNode(aUseCaseNode1, new Point(80, 20));
		select(aActorNode1);

		copy();
		paste();

		assertEquals(3, numberOfRootNodes());
		Node newNode = getRootNode(2);
		assertTrue(newNode.getClass() == ActorNode.class);
		assertEquals(new Point(20, 20), newNode.position());
	}

	@Test
	void testCutNode() {
		addNode(aActorNode1, new Point(20, 20));
		addNode(aUseCaseNode1, new Point(80, 20));

		select(aUseCaseNode1);
		cut();

		assertEquals(1, numberOfRootNodes());

		paste();

		assertEquals(2, numberOfRootNodes());
		Node newNode = getRootNode(1);
		assertTrue(newNode.getClass() == UseCaseNode.class);
		assertEquals(new Point(80, 20), newNode.position());
	}

	@Test
	void testCopyNodesWithEdge() {
		addNode(aActorNode1, new Point(20, 20));
		addNode(aActorNode2, new Point(250, 20));
		addEdge(aAssociationEdge, new Point(20, 20), new Point(250, 20));

		selectAll();
		copy();
		paste();

		assertEquals(4, numberOfRootNodes());
		assertEquals(2, numberOfEdges());
	}

	@Test
	void testCutNodesWithEdge() {
		addNode(aActorNode1, new Point(20, 20));
		addNode(aActorNode2, new Point(250, 20));
		addNode(aUseCaseNode1, new Point(80, 20));
		addNode(aUseCaseNode2, new Point(140, 20));
		addEdge(aAssociationEdge, new Point(20, 20), new Point(250, 20));
		addEdge(aDependencyEdge, new Point(80, 20), new Point(250, 20));
		addEdge(aGeneralEdge, new Point(20, 20), new Point(140, 20));

		select(aActorNode1, aUseCaseNode2, aGeneralEdge);
		cut();

		assertEquals(2, numberOfRootNodes());
		assertEquals(0, numberOfEdges());

		paste();
		assertEquals(4, numberOfRootNodes());
		assertEquals(1, numberOfEdges());
	}
}
