/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2017 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Edge;
import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.graph.edges.NoteEdge;
import ca.mcgill.cs.jetuml.graph.edges.ObjectCollaborationEdge;
import ca.mcgill.cs.jetuml.graph.edges.ObjectReferenceEdge;
import ca.mcgill.cs.jetuml.graph.edges.UseCaseAssociationEdge;
import ca.mcgill.cs.jetuml.graph.edges.UseCaseDependencyEdge;
import ca.mcgill.cs.jetuml.graph.edges.UseCaseGeneralizationEdge;
import ca.mcgill.cs.jetuml.graph.nodes.ActorNode;
import ca.mcgill.cs.jetuml.graph.nodes.ChildNode;
import ca.mcgill.cs.jetuml.graph.nodes.FieldNode;
import ca.mcgill.cs.jetuml.graph.nodes.NoteNode;
import ca.mcgill.cs.jetuml.graph.nodes.ObjectNode;
import ca.mcgill.cs.jetuml.graph.nodes.PointNode;
import ca.mcgill.cs.jetuml.graph.nodes.UseCaseNode;

public class TestPersistenceService
{
	private static final String TEST_FILE_NAME = "testdata/tmp";
	
	@Test
	public void testObjectDiagram() throws Exception
	{
		Graph graph = PersistenceService.read(new FileInputStream("testdata/testPersistenceService.object.jet"));
		verifyObjectDiagram(graph);

		File tmp = new File(TEST_FILE_NAME);
		tmp.delete();
		PersistenceService.saveFile(graph, new FileOutputStream(tmp));
		graph = PersistenceService.read(new FileInputStream(tmp));
		verifyObjectDiagram(graph);
		tmp.delete();
	}
	
	@Test
	public void testUseCaseDiagram() throws Exception
	{
		Graph graph = PersistenceService.read(new FileInputStream("testdata/testPersistenceService.usecase.jet"));
		verifyUseCaseDiagram(graph);

		File tmp = new File(TEST_FILE_NAME);
		tmp.delete();
		PersistenceService.saveFile(graph, new FileOutputStream(tmp));
		graph = PersistenceService.read(new FileInputStream(tmp));
		verifyUseCaseDiagram(graph);
		tmp.delete();
	}
	
	private void verifyUseCaseDiagram( Graph pGraph)
	{
		Collection<Node> nodes = pGraph.getRootNodes();
		assertEquals(9, nodes.size());
		Iterator<Node> nIt = nodes.iterator();
		UseCaseNode u1 = (UseCaseNode) nIt.next();
		UseCaseNode u2 = (UseCaseNode) nIt.next();
		UseCaseNode u3 = (UseCaseNode) nIt.next();
		ActorNode a1 = (ActorNode) nIt.next();
		ActorNode a2 = (ActorNode) nIt.next();
		NoteNode n1 = (NoteNode) nIt.next();
		PointNode p1 = (PointNode) nIt.next();
		UseCaseNode u4 = (UseCaseNode) nIt.next();
		ActorNode a3 = (ActorNode) nIt.next();
		
		assertEquals(new Rectangle(440, 40, 110, 40), u1.view().getBounds());
		assertEquals("Use case 1", u1.getName().toString());
		
		assertEquals(new Rectangle(460, 130, 110, 40), u2.view().getBounds());
		assertEquals("Use case 2", u2.getName().toString());
		
		assertEquals(new Rectangle(460, 230, 110, 40), u3.view().getBounds());
		assertEquals("Use case 3", u3.getName().toString());
		
		assertEquals(new Rectangle(270, 50, 48, 64), a1.view().getBounds());
		assertEquals("Actor", a1.getName().toString());
		
		assertEquals(new Rectangle(280, 230, 48, 64), a2.view().getBounds());
		assertEquals("Actor2", a2.getName().toString());
		
		assertEquals("A note", n1.getName().getText());
		assertEquals(new Rectangle(700, 50, 60, 40), n1.view().getBounds());
		
		assertEquals(new Rectangle(567, 56, 0, 0), p1.view().getBounds());
		
		assertEquals(new Rectangle(650, 150, 110, 40), u4.view().getBounds());
		assertEquals("Use case 4", u4.getName().toString());
		
		assertEquals(new Rectangle(190, 140, 48, 64), a3.view().getBounds());
		assertEquals("Actor3", a3.getName().toString());
		
		Collection<Edge> edges = pGraph.getEdges();
		assertEquals(10, edges.size());
		Iterator<Edge> eIt = edges.iterator();
		
		NoteEdge cr1 = (NoteEdge) eIt.next();
		UseCaseGeneralizationEdge cr2 = (UseCaseGeneralizationEdge) eIt.next();
		UseCaseDependencyEdge cr3 = (UseCaseDependencyEdge) eIt.next();
		UseCaseAssociationEdge cr4 = (UseCaseAssociationEdge) eIt.next();
		UseCaseAssociationEdge cr5 = (UseCaseAssociationEdge) eIt.next();
		UseCaseAssociationEdge cr6 = (UseCaseAssociationEdge) eIt.next();
		UseCaseGeneralizationEdge cr7 = (UseCaseGeneralizationEdge) eIt.next();
		UseCaseDependencyEdge cr8 = (UseCaseDependencyEdge) eIt.next();
		UseCaseDependencyEdge cr9 = (UseCaseDependencyEdge) eIt.next();
		UseCaseAssociationEdge cr10 = (UseCaseAssociationEdge) eIt.next();	
		
		assertEquals(new Rectangle(567,56,133,11),cr1.view().getBounds());
		assertTrue(cr1.getStart() == n1);
		assertTrue(cr1.getEnd() == p1);
		
		assertEquals(new Rectangle(209,77,61,63),cr2.view().getBounds());
		assertTrue(cr2.getStart() == a3);
		assertTrue(cr2.getEnd() == a1);
		
		assertEquals(238,cr3.view().getBounds().getX());
		assertEquals(167,cr3.view().getBounds().getY());
		assertTrue(77 == cr3.view().getBounds().getWidth() || 74 == cr3.view().getBounds().getWidth() );
				
		assertEquals(90,cr3.view().getBounds().getHeight());
		assertTrue( cr3.getStart() == a3);
		assertTrue( cr3.getEnd() == a2);
		assertTrue( cr3.getType() == UseCaseDependencyEdge.Type.Extend);
		
		assertEquals(new Rectangle(318,55,122,22),cr4.view().getBounds());
		assertTrue( cr4.getStart() == a1 );
		assertTrue( cr4.getEnd() == u1 );
		
		assertEquals(new Rectangle(328,145,132,112),cr5.view().getBounds());
		assertTrue( cr5.getStart() == a2 );
		assertTrue( cr5.getEnd() == u2 );
		
		assertEquals(new Rectangle(328,245,132,12),cr6.view().getBounds());
		assertTrue( cr6.getStart() == a2 );
		assertTrue( cr6.getEnd() == u3 );
		
		assertEquals(new Rectangle(488,80,22,50),cr7.view().getBounds());
		assertTrue( cr7.getStart() == u2 );
		assertTrue( cr7.getEnd() == u1 );

		assertTrue(new Rectangle(505,170,63,60).equals(cr8.view().getBounds()) ||
				new Rectangle(505,170,62,60).equals(cr8.view().getBounds()));
		assertTrue( cr8.getStart() == u2 );
		assertTrue( cr8.getEnd() == u3 );
		assertTrue( cr8.getType() == UseCaseDependencyEdge.Type.Include);
		
		assertTrue(new Rectangle(570,136,93,32).equals(cr9.view().getBounds()) ||
				new Rectangle(570,136,96,32).equals(cr9.view().getBounds()));
		assertTrue( cr9.getStart() == u2 );
		assertTrue( cr9.getEnd() == u4 );
		assertTrue( cr9.getType() == UseCaseDependencyEdge.Type.Extend);
		
		assertEquals(new Rectangle(550,55,100,110),cr10.view().getBounds());
		assertTrue( cr10.getStart() == u1 );
		assertTrue( cr10.getEnd() == u4 );
 	}
	
	private void verifyObjectDiagram(Graph pGraph)
	{
		Collection<Node> nodes = pGraph.getRootNodes();
		assertEquals(7, nodes.size());
		
		Iterator<Node> nIt = nodes.iterator(); 
		ObjectNode type1 = (ObjectNode) nIt.next(); 
		ObjectNode blank = (ObjectNode) nIt.next(); 
		ObjectNode object2 = (ObjectNode) nIt.next(); 
		ObjectNode type3 = (ObjectNode) nIt.next();

		NoteNode note = (NoteNode) nIt.next(); 
		PointNode p1 = (PointNode) nIt.next();
		PointNode p2 = (PointNode) nIt.next();
		
		assertEquals(new Rectangle(240, 130, 80, 60), type1.view().getBounds());
		List<ChildNode> children = type1.getChildren();
		assertEquals(1, children.size());
		assertEquals(":Type1", type1.getName().toString());
		
		FieldNode name = (FieldNode) children.get(0);
		assertEquals(new Rectangle(253, 209, 60, 20), name.view().getBounds());
		assertEquals(47,name.obtainAxis());
		assertEquals("name", name.getName().toString());
		assertEquals(type1, name.getParent());
		assertEquals("", name.getValue().toString());

		assertEquals(new Rectangle(440, 290, 80, 60), blank.view().getBounds());
		children = blank.getChildren();
		assertEquals(3, children.size());
		assertEquals("", blank.getName().toString());
		FieldNode name2 = (FieldNode) children.get(0);
		FieldNode name3 = (FieldNode) children.get(1);
		FieldNode name4 = (FieldNode) children.get(2);
		
		assertEquals(new Rectangle(446, 367, 60, 20), name2.view().getBounds());
		assertEquals(54,name2.obtainAxis());
		assertEquals("name2", name2.getName().toString());
		assertEquals(blank, name2.getParent());
		assertEquals("value", name2.getValue().toString());
		
		assertEquals(new Rectangle(445, 388, 60, 20), name3.view().getBounds());
		assertEquals(54,name3.obtainAxis());
		assertEquals("name3", name3.getName().toString());
		assertEquals(blank, name3.getParent());
		assertEquals("value", name3.getValue().toString());
		
		assertEquals(new Rectangle(445, 409, 60, 20), name4.view().getBounds());
		assertEquals(54,name4.obtainAxis());
		assertEquals("name4", name4.getName().toString());
		assertEquals(blank, name4.getParent());
		assertEquals("", name4.getValue().toString());

		assertEquals(new Rectangle(540, 150, 80, 60), object2.view().getBounds());
		children = object2.getChildren();
		assertEquals(0, children.size());
		assertEquals("object2:", object2.getName().toString());
		
		assertEquals(new Rectangle(610, 300, 80, 60), type3.view().getBounds());
		children = type3.getChildren();
		assertEquals(0, children.size());
		assertEquals(":Type3", type3.getName().toString());

		assertEquals("A note", note.getName().getText());
		assertEquals(new Rectangle(280, 330, 60, 40), note.view().getBounds());
		
		assertEquals(new Rectangle(281, 216, 0, 0), p1.view().getBounds());
		
		assertEquals(new Rectangle(474, 339, 0, 0), p2.view().getBounds());
		
		Collection<Edge> edges = pGraph.getEdges();
		assertEquals(6, edges.size());
		Iterator<Edge> eIt = edges.iterator();
		
		ObjectReferenceEdge o1 = (ObjectReferenceEdge) eIt.next();
		ObjectReferenceEdge o2 = (ObjectReferenceEdge) eIt.next();
		ObjectReferenceEdge o3 = (ObjectReferenceEdge) eIt.next();
		NoteEdge ne1 = (NoteEdge) eIt.next();
		NoteEdge ne2 = (NoteEdge) eIt.next();
		ObjectCollaborationEdge cr1 = (ObjectCollaborationEdge) eIt.next();
		
		assertEquals(new Rectangle(306, 160, 34, 59), o1.view().getBounds());
		assertEquals(name, o1.getStart());
		assertEquals(type1, o1.getEnd());
		
		assertEquals(new Rectangle(306, 219, 134, 101), o2.view().getBounds());
		assertEquals(name, o2.getStart());
		assertEquals(blank, o2.getEnd());
		
		assertEquals(new Rectangle(480, 180, 60, 110), cr1.view().getBounds());
		assertEquals(object2, cr1.getEnd());
		assertEquals("", cr1.getEndLabel());
		assertEquals("e1", cr1.getMiddleLabel().toString());
		assertEquals(blank, cr1.getStart());
		assertEquals("", cr1.getStartLabel().toString());
		
		assertEquals(new Rectangle(502, 330, 108, 89), o3.view().getBounds());
		assertEquals(name4, o3.getStart());
		assertEquals(type3, o3.getEnd());
		
		assertEquals(new Rectangle(281, 216, 24, 114), ne1.view().getBounds());
		assertEquals(note, ne1.getStart());
		assertEquals(p1, ne1.getEnd());
		
		assertEquals(new Rectangle(340, 339, 134, 8), ne2.view().getBounds());
		assertEquals(note, ne2.getStart());
		assertEquals(p2, ne2.getEnd());
	}
}
