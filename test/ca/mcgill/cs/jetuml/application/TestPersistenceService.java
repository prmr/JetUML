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

import org.junit.Test;

import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Edge;
import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.graph.edges.NoteEdge;
import ca.mcgill.cs.jetuml.graph.edges.UseCaseAssociationEdge;
import ca.mcgill.cs.jetuml.graph.edges.UseCaseDependencyEdge;
import ca.mcgill.cs.jetuml.graph.edges.UseCaseGeneralizationEdge;
import ca.mcgill.cs.jetuml.graph.nodes.ActorNode;
import ca.mcgill.cs.jetuml.graph.nodes.NoteNode;
import ca.mcgill.cs.jetuml.graph.nodes.PointNode;
import ca.mcgill.cs.jetuml.graph.nodes.UseCaseNode;

public class TestPersistenceService
{
	private static final String TEST_FILE_NAME = "testdata/tmp";
	
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
}
