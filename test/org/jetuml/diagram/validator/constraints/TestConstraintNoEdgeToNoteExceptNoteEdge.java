/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2023 by McGill University.
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
import org.junit.jupiter.api.Test;

public class TestConstraintNoEdgeToNoteExceptNoteEdge
{
	private static final ConstraintNoEdgeToNoteExceptNoteEdge CONSTRAINT = 
			new ConstraintNoEdgeToNoteExceptNoteEdge();
	
	private final Diagram aDiagram = new Diagram(DiagramType.CLASS);
	
	@Test
	void testSatistified_True_NoteEdge()
	{
		NoteEdge edge = new NoteEdge();
		NoteNode nodeA = new NoteNode();
		PointNode nodeB = new PointNode();
		edge.connect(nodeA, nodeB);
		aDiagram.addRootNode(nodeA);
		aDiagram.addRootNode(nodeB);
		aDiagram.addEdge(edge);
		
		assertTrue(CONSTRAINT.satisfied(edge, aDiagram));
	}
	
	@Test
	void testSatistified_False_NoteAtStart()
	{
		DependencyEdge edge = new DependencyEdge();
		NoteNode nodeA = new NoteNode();
		ClassNode nodeB = new ClassNode();
		edge.connect(nodeA, nodeB);
		aDiagram.addRootNode(nodeA);
		aDiagram.addRootNode(nodeB);
		aDiagram.addEdge(edge);
		
		assertFalse(CONSTRAINT.satisfied(edge, aDiagram));
	}
	
	@Test
	void testSatistified_False_NoteAtEnd()
	{
		DependencyEdge edge = new DependencyEdge();
		NoteNode nodeA = new NoteNode();
		ClassNode nodeB = new ClassNode();
		edge.connect(nodeB, nodeA);
		aDiagram.addRootNode(nodeA);
		aDiagram.addRootNode(nodeB);
		aDiagram.addEdge(edge);
		
		assertFalse(CONSTRAINT.satisfied(edge, aDiagram));
	}
	
	@Test
	void testSatistified_False_NoteAtStartAndEnd()
	{
		DependencyEdge edge = new DependencyEdge();
		NoteNode nodeA = new NoteNode();
		NoteNode nodeB = new NoteNode();
		edge.connect(nodeB, nodeA);
		aDiagram.addRootNode(nodeA);
		aDiagram.addRootNode(nodeB);
		aDiagram.addEdge(edge);
		
		assertFalse(CONSTRAINT.satisfied(edge, aDiagram));
	}
	
	@Test
	void testSatistified_True_NoteAtNeither()
	{
		DependencyEdge edge = new DependencyEdge();
		ClassNode nodeA = new ClassNode();
		ClassNode nodeB = new ClassNode();
		edge.connect(nodeB, nodeA);
		aDiagram.addRootNode(nodeA);
		aDiagram.addRootNode(nodeB);
		aDiagram.addEdge(edge);
		
		assertTrue(CONSTRAINT.satisfied(edge, aDiagram));
	}
}
