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
package org.jetuml.diagram.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.edges.AggregationEdge;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.PointNode;
import org.junit.jupiter.api.Test;

/**
 * Test for the rules that apply to all diagrams. These
 * rules are related to UML notes. The tests are done via 
 * a ClassDiagramValidator because we need an instance, but 
 * this class should not be used to hold test specific to class
 * diagrams.
 */
public class TestAbstractDiagramValidator
{
	private final ClassDiagramValidator aValidator =
			new ClassDiagramValidator(new Diagram(DiagramType.CLASS));
	
	private final NoteNode aNoteNode1 = new NoteNode();
	private final NoteNode aNoteNode2 = new NoteNode();
	private final ClassNode aClassNode = new ClassNode();
	private final PointNode aPointNode = new PointNode();
	private final NoteEdge aNoteEdge = new NoteEdge();
	
	private Diagram diagram()
	{
		return aValidator.diagram();
	}
	
	@Test
	void testPointNodeNotConnected()
	{
		diagram().addRootNode(new PointNode());
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testNoteEdgeFromNoteToNote()
	{
		diagram().addRootNode(aNoteNode1);
		diagram().addRootNode(aNoteNode2);
		aNoteEdge.connect(aNoteNode1, aNoteNode2);
		diagram().addEdge(aNoteEdge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testNoteEdgeFromClassToPoint()
	{
		diagram().addRootNode(aClassNode);
		diagram().addRootNode(aPointNode);
		aNoteEdge.connect(aClassNode, aPointNode);
		diagram().addEdge(aNoteEdge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testNoteEdgeFromPointToClass()
	{
		diagram().addRootNode(aClassNode);
		diagram().addRootNode(aPointNode);
		aNoteEdge.connect(aPointNode, aClassNode );
		diagram().addEdge(aNoteEdge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testNoteEdgeFromNoteToPoint()
	{
		diagram().addRootNode(aNoteNode1);
		diagram().addRootNode(aPointNode);
		aNoteEdge.connect(aNoteNode1, aPointNode );
		diagram().addEdge(aNoteEdge);
		assertTrue(aValidator.isValid());
	}
	
	@Test
	void testNoteEdgeFromClassNodeToNote()
	{
		diagram().addRootNode(aClassNode);
		diagram().addRootNode(aNoteNode1);
		aNoteEdge.connect(aClassNode, aNoteNode1 );
		diagram().addEdge(aNoteEdge);
		assertTrue(aValidator.isValid());
	}
	
	@Test
	void testNoteEdgeFromClassNodeToClass()
	{
		diagram().addRootNode(aClassNode);
		aNoteEdge.connect(aClassNode, aClassNode );
		diagram().addEdge(aNoteEdge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testDependencyEdgeToPointNode()
	{
		diagram().addRootNode(aClassNode);
		diagram().addRootNode(aPointNode);
		Edge edge = new DependencyEdge();
		edge.connect(aClassNode, aPointNode);
		diagram().addEdge(edge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testAggregationEdgeToPointNode()
	{
		diagram().addRootNode(aClassNode);
		diagram().addRootNode(aPointNode);
		Edge edge = new AggregationEdge();
		edge.connect(aClassNode, aPointNode);
		diagram().addEdge(edge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testGeneralizationEdgeToPointNode()
	{
		diagram().addRootNode(aClassNode);
		diagram().addRootNode(aPointNode);
		Edge edge = new AggregationEdge();
		edge.connect(aClassNode, aPointNode);
		diagram().addEdge(edge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testDuplicatedNoteEdge()
	{
		diagram().addRootNode(aClassNode);
		diagram().addRootNode(aNoteNode1);
		Edge edge1 = new NoteEdge();
		Edge edge2 = new NoteEdge();
		edge1.connect(aClassNode, aNoteNode1);
		edge2.connect(aClassNode, aNoteNode1);
		diagram().addEdge(edge1);
		diagram().addEdge(edge2);
	}
}
