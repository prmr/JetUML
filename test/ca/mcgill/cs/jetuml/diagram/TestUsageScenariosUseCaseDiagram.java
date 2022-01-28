/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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
package ca.mcgill.cs.jetuml.diagram;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.diagram.builder.UseCaseDiagramBuilder;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseAssociationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseDependencyEdge;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseGeneralizationEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ActorNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;
import ca.mcgill.cs.jetuml.diagram.nodes.UseCaseNode;
import ca.mcgill.cs.jetuml.geom.Point;

public class TestUsageScenariosUseCaseDiagram extends AbstractTestUsageScenarios
{
	private ActorNode aActorNode1;
	private ActorNode aActorNode2;
	private UseCaseNode aUseCaseNode1;
	private UseCaseNode aUseCaseNode2;
	private UseCaseAssociationEdge aAssociationEdge;
	private UseCaseDependencyEdge aDependencyEdge;
	private UseCaseGeneralizationEdge aGeneralEdge;
	
	@BeforeEach
	@Override
	public void setup()
	{
		super.setup();
		aDiagram = new Diagram(DiagramType.USECASE);
		aBuilder = new UseCaseDiagramBuilder(aDiagram);
		aActorNode1 = new ActorNode();
		aActorNode2 = new ActorNode();
		aUseCaseNode1 = new UseCaseNode();
		aUseCaseNode2 = new UseCaseNode();
		aAssociationEdge = new UseCaseAssociationEdge();
		aDependencyEdge = new UseCaseDependencyEdge();
		aGeneralEdge = new UseCaseGeneralizationEdge();
	}
	
	@Test
	public void testCreateUseCaseDiagram()
	{
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
	public void testGeneralEdgeCreation()
	{
		addNode(aActorNode1, new Point(20, 20));
		assertTrue(aDiagram.contains(aActorNode1));
		assertEquals(new Point(20,20), aActorNode1.position());
		
		addNode(aActorNode2, new Point(250, 20));
		assertTrue(aDiagram.contains(aActorNode2));
		assertEquals(new Point(250,20), aActorNode2.position());
		
		addNode(aUseCaseNode1, new Point(80, 20));
		assertTrue(aDiagram.contains(aUseCaseNode1));
		assertEquals(new Point(80,20), aUseCaseNode1.position());
		
		addNode(aUseCaseNode2, new Point(140, 20));
		assertTrue(aDiagram.contains(aUseCaseNode2));
		assertEquals(new Point(140,20), aUseCaseNode2.position());
		
		addEdge(aAssociationEdge,  new Point(20, 20), new Point(250, 20)); // aActorNode1 -> aUseCaseNode2
		assertTrue(aDiagram.contains(aAssociationEdge));
		assertSame(aActorNode1, aAssociationEdge.getStart());
		assertSame(aUseCaseNode2, aAssociationEdge.getEnd());
		
		addEdge(aDependencyEdge,  new Point(80, 20), new Point(250, 20)); // aUseCaseNode1 -> aUseCaseNode2
		assertTrue(aDiagram.contains(aDependencyEdge));
		assertSame(aUseCaseNode1, aDependencyEdge.getStart());
		assertSame(aUseCaseNode2, aDependencyEdge.getEnd());
		
		addEdge(aGeneralEdge,  new Point(20, 20), new Point(140, 20)); // aActorNode1 -> aUseCaseNode2
		assertTrue(aDiagram.contains(aGeneralEdge));
		assertSame(aActorNode1, aGeneralEdge.getStart());
		assertSame(aUseCaseNode2, aGeneralEdge.getEnd());
		
		UseCaseAssociationEdge useCaseAssociationEdge2 = new UseCaseAssociationEdge();
		addEdge(useCaseAssociationEdge2,  new Point(80, 20), new Point(140, 20)); // aUseCaseNode1 -> aUseCaseNode2
		assertTrue(aDiagram.contains(useCaseAssociationEdge2));
		assertSame(aUseCaseNode1, useCaseAssociationEdge2.getStart());
		assertSame(aUseCaseNode2, useCaseAssociationEdge2.getEnd());
		
		UseCaseDependencyEdge useCaseDependencyEdge2 = new UseCaseDependencyEdge();
		addEdge(useCaseDependencyEdge2,  new Point(20, 20), new Point(250, 20)); // aActorNode1 -> aUseCaseNode2
		assertTrue(aDiagram.contains(useCaseDependencyEdge2));
		assertSame(aActorNode1, useCaseDependencyEdge2.getStart());
		assertSame(aUseCaseNode2, useCaseDependencyEdge2.getEnd());
		
		assertEquals(5, numberOfEdges());
		
		// connect nodes with NoteEdge (not allowed)
		assertFalse(aBuilder.canAdd(new NoteEdge(),  new Point(80, 20), new Point(140, 20)));
		assertFalse(aBuilder.canAdd(new NoteEdge(),  new Point(20, 20), new Point(250, 20)));
	}
	
	@Test
	public void testNoteEdgeCreation()
	{
		addNode(aActorNode1, new Point(20, 20));
		addNode(aActorNode2, new Point(250, 20));
		addNode(aUseCaseNode1, new Point(80, 20));
		addNode(aUseCaseNode2, new Point(140, 20));
		assertEquals(4, numberOfRootNodes());
		
		NoteNode noteNode = new NoteNode();
		addNode(noteNode, new Point(100, 100));
		assertTrue(aDiagram.contains(noteNode));
		
		NoteEdge noteEdge1 = new NoteEdge();
		NoteEdge noteEdge2 = new NoteEdge();
		NoteEdge noteEdge3 = new NoteEdge();
		
		assertFalse(aBuilder.canAdd(noteEdge1, new Point(9, 9), new Point(209, 162)));
		addEdge(noteEdge1, new Point(20, 20), new Point(100, 100));
		assertTrue(aDiagram.contains(noteEdge1));
		assertSame(noteEdge1.getStart(), aActorNode1);
		assertSame(noteEdge1.getEnd(), noteNode);

		addEdge(noteEdge2, new Point(85, 25), new Point(110, 110));
		assertTrue(aDiagram.contains(noteEdge2));
		assertSame(noteEdge2.getStart(), aUseCaseNode1);
		assertSame(noteEdge2.getEnd(), noteNode);
		
		// if begin with a NoteNode, the end point can be anywhere
		addEdge(noteEdge3, new Point(100, 100), new Point(9,9));
		assertTrue(aDiagram.contains(noteEdge3));
		assertSame(noteEdge3.getStart(), noteNode);
		Node end = noteEdge3.getEnd();
		assertEquals(PointNode.class, end.getClass());
		assertEquals(new Point(9,9), end.position());
	}
	
	@Test
	public void testIndividualNodeMovement()
	{
		addNode(aActorNode1, new Point(20, 20));
		addNode(aUseCaseNode1, new Point(80, 20));
		addNode(aNoteNode, new Point(100, 100));

		moveNode(aActorNode1, 3, 12);
		moveNode(aUseCaseNode1, 3, 12);
		moveNode(aNoteNode, 40, 20);
		
		assertEquals( new Point(23,32), aActorNode1.position());
		assertEquals( new Point(83,32), aUseCaseNode1.position());
		assertEquals( new Point(140,120), aNoteNode.position());
	}
	
	@Test
	public void testNodesAndEdgesMovement()
	{
		addNode(aActorNode1, new Point(20, 20));
		addNode(aActorNode2, new Point(250, 20));
		addNode(aUseCaseNode1, new Point(80, 20));
		addNode(aUseCaseNode2, new Point(140, 20));
		addNode(aNoteNode, new Point(100, 100));
		addEdge(aAssociationEdge,  new Point(20, 20), new Point(250, 20));
		addEdge(aDependencyEdge,  new Point(80, 20), new Point(250, 20));
		addEdge(aGeneralEdge,  new Point(20, 20), new Point(140, 20));
		addEdge(aNoteEdge, new Point(85, 25), new Point(110, 110));

		aDiagram.rootNodes().forEach(node -> node.translate(26, 37));
		
		assertEquals(new Point(46, 57), aActorNode1.position());
		assertEquals(new Point(276, 57), aActorNode2.position());
		assertEquals(new Point(106, 57), aUseCaseNode1.position());
		assertEquals(new Point(166, 57), aUseCaseNode2.position());
		assertEquals(new Point(126, 137), aNoteNode.position());
	}
	
	@Test
	public void testDeleteNode()
	{
		addNode(aActorNode1, new Point(20, 20));
		assertTrue(aDiagram.contains(aActorNode1));
		select(aActorNode1);
		deleteSelected();
		assertEquals(0, numberOfRootNodes());
		
		undo();
		assertEquals(1, numberOfRootNodes());
		assertTrue(aDiagram.contains(aActorNode1));
		
		addNode(aNoteNode, new Point(75, 75));
		assertEquals(2, numberOfRootNodes());
		assertTrue(aDiagram.contains(aNoteNode));
		select(aNoteNode);
		deleteSelected();
		assertEquals(1, numberOfRootNodes());
		assertTrue(aDiagram.contains(aActorNode1));
		assertFalse(aDiagram.contains(aNoteNode));
		undo();
		assertEquals(2, numberOfRootNodes());
		assertTrue(aDiagram.contains(aActorNode1));
		assertTrue(aDiagram.contains(aNoteNode));

		addNode(aUseCaseNode1, new Point(420, 420));
		assertTrue(aDiagram.contains(aUseCaseNode1));
		assertTrue(aDiagram.contains(aActorNode1));
		select(aActorNode1, aUseCaseNode1);
		deleteSelected();
		assertEquals(1, numberOfRootNodes());
		assertFalse(aDiagram.contains(aActorNode1));
		assertTrue(aDiagram.contains(aNoteNode));
		assertFalse(aDiagram.contains(aUseCaseNode1));

		undo();
		assertEquals(3, numberOfRootNodes());
		assertTrue(aDiagram.contains(aActorNode1));
		assertTrue(aDiagram.contains(aNoteNode));
		assertTrue(aDiagram.contains(aUseCaseNode1));
	}

	@Test
	public void testDeleteEdge()
	{
		addNode(aActorNode1, new Point(20, 20));
		addNode(aActorNode2, new Point(250, 20));
		addNode(aUseCaseNode1, new Point(80, 20));
		addNode(aUseCaseNode2, new Point(140, 20));
		addNode(aNoteNode, new Point(100, 100));
		addEdge(aAssociationEdge,  new Point(20, 20), new Point(250, 20));
		addEdge(aDependencyEdge,  new Point(80, 20), new Point(250, 20));
		addEdge(aGeneralEdge,  new Point(20, 20), new Point(140, 20));
		addEdge(aNoteEdge, new Point(85, 25), new Point(110, 110));
		
		select(aAssociationEdge);
		deleteSelected();
		assertEquals(3, numberOfEdges());
		assertFalse(aDiagram.contains(aAssociationEdge));
		
		select(aGeneralEdge);
		deleteSelected();
		assertEquals(2, numberOfEdges());
		assertFalse(aDiagram.contains(aGeneralEdge));

		undo();
		assertEquals(3, numberOfEdges());
		assertTrue(aDiagram.contains(aGeneralEdge));
		undo();
		assertEquals(4, numberOfEdges());
		assertTrue(aDiagram.contains(aAssociationEdge));
	}
	
	@Test
	public void testDeleteCombinationNodeAndEdge()
	{
		addNode(aActorNode1, new Point(20, 20));
		addNode(aActorNode2, new Point(250, 20));
		addNode(aUseCaseNode1, new Point(80, 20));
		addNode(aUseCaseNode2, new Point(140, 20));
		addNode(aNoteNode, new Point(100, 100));
		addEdge(aAssociationEdge,  new Point(20, 20), new Point(250, 20));
		addEdge(aDependencyEdge,  new Point(80, 20), new Point(250, 20));
		addEdge(aGeneralEdge,  new Point(20, 20), new Point(140, 20));
		addEdge(aNoteEdge, new Point(85, 25), new Point(110, 110));

		select(aActorNode1, aAssociationEdge, aDependencyEdge, aGeneralEdge, aNoteEdge);
		
		deleteSelected();
		
		assertEquals(4, numberOfRootNodes());
		assertFalse(aDiagram.contains(aActorNode1));
		assertEquals(0, numberOfEdges());
		
		undo();
		assertEquals(5, numberOfRootNodes());
		assertTrue(aDiagram.contains(aActorNode1));
		assertEquals(4, numberOfEdges());
		
		/* now delete aUseCaseNode2, aActorNode2 and aGeneralEdge
		 * aAssociationEdge and aDependencyEdge will also be deleted
		 * since they are connected to aActorNode2
		 */
		select(aUseCaseNode2, aActorNode2, aGeneralEdge);
		deleteSelected();
		
		assertEquals(3, numberOfRootNodes());
		assertEquals(1, numberOfEdges());
		assertFalse(aDiagram.contains(aAssociationEdge));
		assertFalse(aDiagram.contains(aDependencyEdge));
		
		undo();
		assertEquals(5, numberOfRootNodes());
		assertEquals(4, numberOfEdges());
	}
	
	@Test
	public void testCopyPasteNode()
	{
		addNode(aActorNode1, new Point(20, 20));
		addNode(aUseCaseNode1, new Point(80, 20));
		select(aActorNode1);
		
		copy();
		paste();
	
		assertEquals(3, numberOfRootNodes());
		Node newNode = getRootNode(2);
		assertTrue(newNode.getClass() == ActorNode.class);
		assertEquals(new Point(20,20), newNode.position());
	}
	
	@Test
	public void testCutNode()
	{
		addNode(aActorNode1, new Point(20, 20));
		addNode(aUseCaseNode1, new Point(80, 20));
		
		select(aUseCaseNode1);
		cut();

		assertEquals(1, numberOfRootNodes());
		
		paste();

		assertEquals(2, numberOfRootNodes());
		Node newNode = getRootNode(1);
		assertTrue(newNode.getClass() == UseCaseNode.class);
		assertEquals(new Point(80,20), newNode.position());
	}
	
	@Test
	public void testCopyNodesWithEdge()
	{
		addNode(aActorNode1, new Point(20, 20));
		addNode(aActorNode2, new Point(250, 20));
		addEdge(aAssociationEdge,  new Point(20, 20), new Point(250, 20));
		
		selectAll();
		copy();
		paste();
		
		assertEquals(4, numberOfRootNodes());
		assertEquals(2, numberOfEdges());
	}

	@Test
	public void testCutNodesWithEdge()
	{
		addNode(aActorNode1, new Point(20, 20));
		addNode(aActorNode2, new Point(250, 20));
		addNode(aUseCaseNode1, new Point(80, 20));
		addNode(aUseCaseNode2, new Point(140, 20));
		addEdge(aAssociationEdge,  new Point(20, 20), new Point(250, 20));
		addEdge(aDependencyEdge,  new Point(80, 20), new Point(250, 20));
		addEdge(aGeneralEdge,  new Point(20, 20), new Point(140, 20));
		
		select(aActorNode1, aUseCaseNode2, aGeneralEdge);
		cut();

		assertEquals(2, numberOfRootNodes());
		assertEquals(0, numberOfEdges());

		paste();
		assertEquals(4, numberOfRootNodes());
		assertEquals(1, numberOfEdges());
	}
}
