/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018 by the contributors of the JetUML project.
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

package ca.mcgill.cs.jetuml.diagram.builder.constraints;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.ClassDiagram;
import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;
import ca.mcgill.cs.jetuml.geom.Point;

public class TestEdgeConstraints
{
	private ClassDiagram aDiagram;
	private ClassNode aNode1;
	private ClassNode aNode2;
	private PointNode aPoint;
	private DependencyEdge aEdge1;
	private NoteEdge aNoteEdge;
	private NoteNode aNote;
	
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
		aNode1 = new ClassNode();
		aNode2 = new ClassNode();
		aNote = new NoteNode();
		aPoint = new PointNode();
		aEdge1 = new DependencyEdge();
		aNoteEdge = new NoteEdge();
	}
	
	private void createDiagram()
	{
		aNode2.moveTo(new Point(0,100));
		aNote.moveTo(new Point(100,100));
		aDiagram.addRootNode(aNode1);
		aDiagram.addRootNode(aNode2);
		aDiagram.addRootNode(aNote);
		aPoint.moveTo(new Point(200,200));
		aDiagram.addRootNode(aPoint);
	}
	
	@Test
	public void testNoteEdgeNotNoteEdge()
	{
		createDiagram();
		assertTrue(EdgeConstraints.noteEdge(aEdge1, aNode1, aNode2).satisfied());
	}
	
	@Test
	public void testNoteEdgeNodeNotePoint()
	{
		createDiagram();
		assertTrue(EdgeConstraints.noteEdge(aNoteEdge, aNote, aPoint).satisfied());
	}
	
	@Test
	public void testNoteEdgeNodeNoteNotPoint()
	{
		createDiagram();
		assertFalse(EdgeConstraints.noteEdge(aNoteEdge, aNote, aNode1).satisfied());
	}
	
	@Test
	public void testNoteEdgeNodeNoteNotePoint()
	{
		createDiagram();
		assertFalse(EdgeConstraints.noteEdge(aNoteEdge, aNode1, aPoint).satisfied());
	}
	
	@Test
	public void testNoteEdgeNodeAnyNode()
	{
		createDiagram();
		assertTrue(EdgeConstraints.noteEdge(aNoteEdge, aNode1, aNote).satisfied());
	}
	
	@Test
	public void testNoteNodeAnyAny()
	{
		createDiagram();
		assertTrue(EdgeConstraints.noteNode(aEdge1, aNode1, aNode2).satisfied());
	}
	
	@Test
	public void testNoteNodeNoteAny()
	{
		createDiagram();
		assertFalse(EdgeConstraints.noteNode(aEdge1, aNote, aNode2).satisfied());
		assertTrue(EdgeConstraints.noteNode(aNoteEdge, aNote, aNode2).satisfied());
	}
	
	@Test
	public void testNoteNodeAnyNote()
	{
		createDiagram();
		assertFalse(EdgeConstraints.noteNode(aEdge1, aNode1, aNote).satisfied());
		assertTrue(EdgeConstraints.noteNode(aNoteEdge, aNode1, aNote).satisfied());
	}
	
	@Test
	public void testNoteNodeNoteNote()
	{
		createDiagram();
		assertFalse(EdgeConstraints.noteNode(aEdge1, aNote, aNote).satisfied());
		assertTrue(EdgeConstraints.noteNode(aNoteEdge, aNote, aNote).satisfied());
	}
}
