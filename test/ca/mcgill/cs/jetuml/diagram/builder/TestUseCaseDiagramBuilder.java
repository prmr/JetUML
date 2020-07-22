/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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

package ca.mcgill.cs.jetuml.diagram.builder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseAssociationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseDependencyEdge;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseGeneralizationEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ActorNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.UseCaseNode;
import ca.mcgill.cs.jetuml.geom.Point;

public class TestUseCaseDiagramBuilder
{
	private Diagram aDiagram;
	private UseCaseDiagramBuilder aBuilder;
	private ActorNode aActorNode1;
	private UseCaseNode aUseCaseNode1;
	private NoteNode aNoteNode;
	private UseCaseAssociationEdge aAssociationEdge;
	private UseCaseDependencyEdge aDependencyEdge;
	private UseCaseGeneralizationEdge aGeneralizationEdge;
	private NoteEdge aNoteEdge;
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setUp()
	{
		aDiagram = new Diagram(DiagramType.USECASE);
		aBuilder = new UseCaseDiagramBuilder(aDiagram);
		
		aActorNode1 = new ActorNode();
		aUseCaseNode1 = new UseCaseNode();
		aNoteNode = new NoteNode();
		
		aAssociationEdge = new UseCaseAssociationEdge();
		aDependencyEdge = new UseCaseDependencyEdge();
		aGeneralizationEdge = new UseCaseGeneralizationEdge();
		aNoteEdge = new NoteEdge();
	}
	
	@Test
	public void testCanAddEdgeSelfActor()
	{
		aDiagram.addRootNode(aActorNode1);
		assertFalse(aBuilder.canAdd(aAssociationEdge, new Point(10, 10), new Point(10, 10)));
		assertFalse(aBuilder.canAdd(aDependencyEdge, new Point(10, 10), new Point(10, 10)));
		assertFalse(aBuilder.canAdd(aGeneralizationEdge, new Point(10, 10), new Point(10, 10)));
		assertFalse(aBuilder.canAdd(aNoteEdge, new Point(10, 10), new Point(10, 10)));
	}
	
	@Test
	public void testCanAddEdgeSelfUseCase()
	{
		aDiagram.addRootNode(aUseCaseNode1);
		assertFalse(aBuilder.canAdd(aAssociationEdge, new Point(10, 10), new Point(10, 10)));
		assertFalse(aBuilder.canAdd(aDependencyEdge, new Point(10, 10), new Point(10, 10)));
		assertFalse(aBuilder.canAdd(aGeneralizationEdge, new Point(10, 10), new Point(10, 10)));
		assertFalse(aBuilder.canAdd(aNoteEdge, new Point(10, 10), new Point(10, 10)));
	}
	
	@Test
	public void testCanAddEdgeSelfNote()
	{
		aDiagram.addRootNode(aNoteNode);
		assertFalse(aBuilder.canAdd(aAssociationEdge, new Point(10, 10), new Point(10, 10)));
		assertFalse(aBuilder.canAdd(aDependencyEdge, new Point(10, 10), new Point(10, 10)));
		assertFalse(aBuilder.canAdd(aGeneralizationEdge, new Point(10, 10), new Point(10, 10)));
		assertTrue(aBuilder.canAdd(aNoteEdge, new Point(10, 10), new Point(10, 10)));
	}
	
	@Test
	public void testCanAddEdgeActorUseCase()
	{
		aDiagram.addRootNode(aActorNode1);
		aDiagram.addRootNode(aUseCaseNode1);
		aUseCaseNode1.moveTo(new Point(200,0));
		
		assertTrue(aBuilder.canAdd(aAssociationEdge, new Point(10, 10), new Point(210, 10)));
		assertTrue(aBuilder.canAdd(aDependencyEdge, new Point(10, 10), new Point(210, 10)));
		assertTrue(aBuilder.canAdd(aGeneralizationEdge, new Point(10, 10), new Point(210, 10)));
		assertFalse(aBuilder.canAdd(aNoteEdge, new Point(10, 10), new Point(210, 10)));
	}
	
	@Test
	public void testCanAddEdgeUseCaseActor()
	{
		aDiagram.addRootNode(aActorNode1);
		aDiagram.addRootNode(aUseCaseNode1);
		aActorNode1.moveTo(new Point(200,0));
		
		assertTrue(aBuilder.canAdd(aAssociationEdge, new Point(10, 10), new Point(210, 10)));
		assertTrue(aBuilder.canAdd(aDependencyEdge, new Point(10, 10), new Point(210, 10)));
		assertTrue(aBuilder.canAdd(aGeneralizationEdge, new Point(10, 10), new Point(210, 10)));
		assertFalse(aBuilder.canAdd(aNoteEdge, new Point(10, 10), new Point(210, 10)));
	}
	
	@Test
	public void testCanAddEdgeActorNote()
	{
		aDiagram.addRootNode(aActorNode1);
		aDiagram.addRootNode(aNoteNode);
		aNoteNode.moveTo(new Point(200,0));
		
		assertFalse(aBuilder.canAdd(aAssociationEdge, new Point(10, 10), new Point(210, 10)));
		assertFalse(aBuilder.canAdd(aDependencyEdge, new Point(10, 10), new Point(210, 10)));
		assertFalse(aBuilder.canAdd(aGeneralizationEdge, new Point(10, 10), new Point(210, 10)));
		assertTrue(aBuilder.canAdd(aNoteEdge, new Point(10, 10), new Point(210, 10)));
	}
	
	@Test
	public void testCanAddEdgeUseCaseNote()
	{
		aDiagram.addRootNode(aUseCaseNode1);
		aDiagram.addRootNode(aNoteNode);
		aNoteNode.moveTo(new Point(200,0));
		
		assertFalse(aBuilder.canAdd(aAssociationEdge, new Point(10, 10), new Point(210, 10)));
		assertFalse(aBuilder.canAdd(aDependencyEdge, new Point(10, 10), new Point(210, 10)));
		assertFalse(aBuilder.canAdd(aGeneralizationEdge, new Point(10, 10), new Point(210, 10)));
		assertTrue(aBuilder.canAdd(aNoteEdge, new Point(10, 10), new Point(210, 10)));
	}
	
	@Test
	public void testCanAddEdgeNoteActor()
	{
		aDiagram.addRootNode(aActorNode1);
		aDiagram.addRootNode(aNoteNode);
		aActorNode1.moveTo(new Point(200,0));
		
		assertFalse(aBuilder.canAdd(aAssociationEdge, new Point(10, 10), new Point(210, 10)));
		assertFalse(aBuilder.canAdd(aDependencyEdge, new Point(10, 10), new Point(210, 10)));
		assertFalse(aBuilder.canAdd(aGeneralizationEdge, new Point(10, 10), new Point(210, 10)));
		assertTrue(aBuilder.canAdd(aNoteEdge, new Point(10, 10), new Point(210, 10)));
	}
	
	@Test
	public void testCanAddEdgeNoteUseCase()
	{
		aDiagram.addRootNode(aUseCaseNode1);
		aDiagram.addRootNode(aNoteNode);
		aUseCaseNode1.moveTo(new Point(200,0));
		
		assertFalse(aBuilder.canAdd(aAssociationEdge, new Point(10, 10), new Point(210, 10)));
		assertFalse(aBuilder.canAdd(aDependencyEdge, new Point(10, 10), new Point(210, 10)));
		assertFalse(aBuilder.canAdd(aGeneralizationEdge, new Point(10, 10), new Point(210, 10)));
		assertTrue(aBuilder.canAdd(aNoteEdge, new Point(10, 10), new Point(210, 10)));
	}
}
