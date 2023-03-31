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

package org.jetuml.diagram.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jetuml.JavaFXLoader;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.edges.UseCaseAssociationEdge;
import org.jetuml.diagram.edges.UseCaseDependencyEdge;
import org.jetuml.diagram.edges.UseCaseGeneralizationEdge;
import org.jetuml.diagram.nodes.ActorNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.UseCaseNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
	public void testConstructor()
	{
		assertEquals(DiagramType.USECASE, aBuilder.diagram().getType());
	}
}
