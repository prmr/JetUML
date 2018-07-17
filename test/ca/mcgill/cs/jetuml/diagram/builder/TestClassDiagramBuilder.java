/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/

package ca.mcgill.cs.jetuml.diagram.builder;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.ClassDiagram;
import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.edges.GeneralizationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.geom.Point;

public class TestClassDiagramBuilder
{
	private ClassDiagram aDiagram;
	private ClassDiagramBuilder aBuilder;
	
	/**
	 * Load JavaFX toolkit and environment.
	 */
	@BeforeClass
	@SuppressWarnings("unused")
	public static void setupClass()
	{
		JavaFXLoader loader = JavaFXLoader.instance();
	}
	
	@Before
	public void setUp()
	{
		aDiagram = new ClassDiagram();
		aBuilder = new ClassDiagramBuilder(aDiagram);
	}
	
	@Test
	public void testAddNodeSimple()
	{
		ClassNode node = new ClassNode();
		DiagramOperation operation = aBuilder.createAddNodeOperation(node, new Point(10,10), 500, 500);
		assertEquals(0, aDiagram.getRootNodes().size());
		operation.execute();
		assertEquals(1, aDiagram.getRootNodes().size());
		assertTrue(aDiagram.getRootNodes().contains(node));
		assertEquals(new Point(10,10), node.position());
		operation.undo();
		assertEquals(0, aDiagram.getRootNodes().size());
	}
	
	@Test
	public void testAddNodeReposition()
	{
		ClassNode node = new ClassNode();
		DiagramOperation operation = aBuilder.createAddNodeOperation(node, new Point(450,450), 500, 500);
		assertEquals(0, aDiagram.getRootNodes().size());
		operation.execute();
		assertEquals(1, aDiagram.getRootNodes().size());
		assertTrue(aDiagram.getRootNodes().contains(node));
		assertEquals(new Point(400,440), node.position());
		operation.undo();
		assertEquals(0, aDiagram.getRootNodes().size());
		operation.execute();
		assertEquals(1, aDiagram.getRootNodes().size());
		assertTrue(aDiagram.getRootNodes().contains(node));
		assertEquals(new Point(400,440), node.position());
		operation.undo();
		assertEquals(0, aDiagram.getRootNodes().size());
	}
	
	@Test
	public void testCanAddNode()
	{
		ClassNode node = new ClassNode();
		assertTrue(aBuilder.canAdd(node, new Point(1000,1000)));
	}
	
	@Test
	public void testCanAddEdgeNoFirstNode()
	{
		assertFalse(aBuilder.canAdd(new DependencyEdge(), new Point(10,10), new Point(20,20)));
	}
	
	@Test
	public void testCanAddEdgeFromNoteNode()
	{
		NoteNode node = new NoteNode();
		aDiagram.addRootNode(node);
		node.translate(10, 10);
		assertFalse(aBuilder.canAdd(new DependencyEdge(), new Point(15,15), new Point(100, 100)));
		assertTrue(aBuilder.canAdd(new NoteEdge(), new Point(15,15), new Point(100, 100)));
	}
	
	@Test
	public void testCanAddEdgeNoSecondNode()
	{
		ClassNode node1 = new ClassNode();
		node1.translate(10, 10);
		aDiagram.addRootNode(node1);
		assertFalse(aBuilder.canAdd(new DependencyEdge(), new Point(15,15), new Point(150, 150)));
	}
	
	@Test
	public void testCanAddEdgeAlreadyExists()
	{
		ClassNode node1 = new ClassNode();
		node1.translate(10, 10);
		ClassNode node2 = new ClassNode();
		node2.translate(200, 200);
		aDiagram.addRootNode(node1);
		aDiagram.addRootNode(node2);
		DependencyEdge edge = new DependencyEdge();
		edge.connect(node1, node2, aDiagram);
		aDiagram.addEdge(edge);
		assertFalse(aBuilder.canAdd(new DependencyEdge(), new Point(15,15), new Point(205, 205)));
	}
	
	@Test
	public void testCanAddEdgeFromNoteNodeNotNodeEdge()
	{
		NoteNode node = new NoteNode();
		aDiagram.addRootNode(node);
		assertFalse(aBuilder.canAdd(new DependencyEdge(), new Point(15,15), new Point(205, 205)));
	}
	
	@Test
	public void testCanAddEdgeToNoteNodeNotNodeEdge()
	{
		NoteNode node = new NoteNode();
		aDiagram.addRootNode(node);
		assertFalse(aBuilder.canAdd(new DependencyEdge(), new Point(205, 205), new Point(15,15)));
	}
	
	@Test
	public void testCanAddEdgeSelfGeneralization()
	{
		ClassNode node = new ClassNode();
		aDiagram.addRootNode(node);
		assertFalse(aBuilder.canAdd(new GeneralizationEdge(), new Point(15, 15), new Point(15,15)));
	}
	
	@Test
	public void testCanAddEdgeNonSelfDependency()
	{
		ClassNode node1 = new ClassNode();
		aDiagram.addRootNode(node1);
		ClassNode node2 = new ClassNode();
		node2.translate(200, 200);
		aDiagram.addRootNode(node2);
		assertTrue(aBuilder.canAdd(new DependencyEdge(), new Point(15,15), new Point(205,205)));
	}
	
	@Test
	public void testCanAddEdgeSelfDependency()
	{
		ClassNode node1 = new ClassNode();
		aDiagram.addRootNode(node1);
		assertTrue(aBuilder.canAdd(new DependencyEdge(), new Point(15,15), new Point(15,15)));
	}
}
