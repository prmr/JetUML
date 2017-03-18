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
package ca.mcgill.cs.stg.jetuml.framework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.graph.ActorNode;
import ca.mcgill.cs.stg.jetuml.graph.CallEdge;
import ca.mcgill.cs.stg.jetuml.graph.CallNode;
import ca.mcgill.cs.stg.jetuml.graph.ChildNode;
import ca.mcgill.cs.stg.jetuml.graph.CircularStateNode;
import ca.mcgill.cs.stg.jetuml.graph.ClassNode;
import ca.mcgill.cs.stg.jetuml.graph.ClassRelationshipEdge;
import ca.mcgill.cs.stg.jetuml.graph.DependencyEdge;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.FieldNode;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.ImplicitParameterNode;
import ca.mcgill.cs.stg.jetuml.graph.InterfaceNode;
import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.graph.NoteEdge;
import ca.mcgill.cs.stg.jetuml.graph.NoteNode;
import ca.mcgill.cs.stg.jetuml.graph.ObjectCollaborationEdge;
import ca.mcgill.cs.stg.jetuml.graph.ObjectNode;
import ca.mcgill.cs.stg.jetuml.graph.ObjectReferenceEdge;
import ca.mcgill.cs.stg.jetuml.graph.PackageNode;
import ca.mcgill.cs.stg.jetuml.graph.PointNode;
import ca.mcgill.cs.stg.jetuml.graph.ReturnEdge;
import ca.mcgill.cs.stg.jetuml.graph.StateNode;
import ca.mcgill.cs.stg.jetuml.graph.StateTransitionEdge;
import ca.mcgill.cs.stg.jetuml.graph.UseCaseAssociationEdge;
import ca.mcgill.cs.stg.jetuml.graph.UseCaseDependencyEdge;
import ca.mcgill.cs.stg.jetuml.graph.UseCaseGeneralizationEdge;
import ca.mcgill.cs.stg.jetuml.graph.UseCaseNode;

public class TestPersistenceService
{
	private static final String TEST_FILE_NAME = "testdata/tmp";
	
	@Test
	public void testClassDiagram() throws Exception
	{
		Graph graph = PersistenceService.read(new FileInputStream("testdata/testPersistenceService.class.jet"));
		verifyClassDiagram(graph);
		
		File tmp = new File(TEST_FILE_NAME);
		tmp.delete();
		PersistenceService.saveFile(graph, new FileOutputStream(tmp));
		PersistenceService.saveFile(graph, new FileOutputStream(tmp));
		graph = PersistenceService.read(new FileInputStream(tmp));
		verifyClassDiagram(graph);
		tmp.delete();
	}
	
	@Test
	public void testClassDiagramContainment() throws Exception
	{
		Graph graph = PersistenceService.read(new FileInputStream("testdata/testPersistenceService2.class.jet"));
		verifyClassDiagram2(graph);
		
		File tmp = new File(TEST_FILE_NAME);
		tmp.delete();
		PersistenceService.saveFile(graph, new FileOutputStream(tmp));
		graph = PersistenceService.read(new FileInputStream(tmp));
		verifyClassDiagram2(graph);
		tmp.delete();
	}
	
	@Test
	public void testSequenceDiagram() throws Exception
	{
		Graph graph = PersistenceService.read(new FileInputStream("testdata/testPersistenceService.sequence.jet"));
		verifySequenceDiagram(graph);
		
		File tmp = new File(TEST_FILE_NAME);
		tmp.delete();
		PersistenceService.saveFile(graph, new FileOutputStream(tmp));
		graph = PersistenceService.read(new FileInputStream(tmp));
		verifySequenceDiagram(graph);
		tmp.delete();
	}
	
	@Test
	public void testStateDiagram() throws Exception
	{
		Graph graph = PersistenceService.read(new FileInputStream("testdata/testPersistenceService.state.jet"));
		verifyStateDiagram(graph);

		File tmp = new File(TEST_FILE_NAME);
		tmp.delete();
		PersistenceService.saveFile(graph, new FileOutputStream(tmp));
		graph = PersistenceService.read(new FileInputStream(tmp));
		verifyStateDiagram(graph);
		tmp.delete();
	}
	
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
		
		assertEquals(new Rectangle2D.Double(440, 40, 120, 40), u1.getBounds());
		assertEquals("Use case 1", u1.getName().toString());
		
		assertEquals(new Rectangle2D.Double(460, 130, 120, 40), u2.getBounds());
		assertEquals("Use case 2", u2.getName().toString());
		
		assertEquals(new Rectangle2D.Double(460, 230, 120, 40), u3.getBounds());
		assertEquals("Use case 3", u3.getName().toString());
		
		assertEquals(new Rectangle2D.Double(270, 50, 60, 80), a1.getBounds());
		assertEquals("Actor", a1.getName().toString());
		
		assertEquals(new Rectangle2D.Double(280, 230, 60, 80), a2.getBounds());
		assertEquals("Actor2", a2.getName().toString());
		
		assertEquals("A note", n1.getText().getText());
		assertEquals(new Rectangle2D.Double(700, 50, 60, 40), n1.getBounds());
		
		assertEquals(new Rectangle2D.Double(567, 56, 0.01, 0.01), p1.getBounds());
		
		assertEquals(new Rectangle2D.Double(650, 150, 120, 40), u4.getBounds());
		assertEquals("Use case 4", u4.getName().toString());
		
		assertEquals(new Rectangle2D.Double(190, 140, 60, 80), a3.getBounds());
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
		
		assertEquals(new Rectangle2D.Double(567,56,133,12),cr1.getBounds());
		assertTrue(cr1.getStart() == n1);
		assertTrue(cr1.getEnd() == p1);
		
		assertEquals(new Rectangle2D.Double(214,84,56,56),cr2.getBounds());
		assertTrue(cr2.getStart() == a3);
		assertTrue(cr2.getEnd() == a1);
		
		assertEquals(250,cr3.getBounds().getX(),0.1);
		assertEquals(174,cr3.getBounds().getY(),0.1);
		assertEquals(71,cr3.getBounds().getWidth(),3.0);
		assertEquals(91,cr3.getBounds().getHeight(),0.1);
		assertTrue( cr3.getStart() == a3);
		assertTrue( cr3.getEnd() == a2);
		assertTrue( cr3.getType() == UseCaseDependencyEdge.Type.Extend);
		
		assertEquals(new Rectangle2D.Double(330,54,110,31),cr4.getBounds());
		assertTrue( cr4.getStart() == a1 );
		assertTrue( cr4.getEnd() == u1 );
		
		assertEquals(new Rectangle2D.Double(340,144,120,121),cr5.getBounds());
		assertTrue( cr5.getStart() == a2 );
		assertTrue( cr5.getEnd() == u2 );
		
		assertEquals(new Rectangle2D.Double(340,244,120,21),cr6.getBounds());
		assertTrue( cr6.getStart() == a2 );
		assertTrue( cr6.getEnd() == u3 );
		
		assertEquals(new Rectangle2D.Double(493,80,22,50),cr7.getBounds());
		assertTrue( cr7.getStart() == u2 );
		assertTrue( cr7.getEnd() == u1 );

		assertTrue(new Rectangle2D.Double(509,170,63,60).equals(cr8.getBounds()) ||
				new Rectangle2D.Double(509,170,64,60).equals(cr8.getBounds()));
		assertTrue( cr8.getStart() == u2 );
		assertTrue( cr8.getEnd() == u3 );
		assertTrue( cr8.getType() == UseCaseDependencyEdge.Type.Include);
		
		assertTrue(new Rectangle2D.Double(580,135,91,32).equals(cr9.getBounds()) ||
				new Rectangle2D.Double(580,135,88,32).equals(cr9.getBounds()));
		assertTrue( cr9.getStart() == u2 );
		assertTrue( cr9.getEnd() == u4 );
		assertTrue( cr9.getType() == UseCaseDependencyEdge.Type.Extend);
		
		assertEquals(new Rectangle2D.Double(560,54,90,111),cr10.getBounds());
		assertTrue( cr10.getStart() == u1 );
		assertTrue( cr10.getEnd() == u4 );
 	}
	
	private void verifyClassDiagram2(Graph pGraph)
	{
		Collection<Node> nodes = pGraph.getRootNodes();
		assertEquals(4, nodes.size());
		Iterator<Node> nIterator = nodes.iterator();
		PackageNode p1 = (PackageNode) nIterator.next();
		PackageNode p2 = (PackageNode) nIterator.next();
		PackageNode p3 = (PackageNode) nIterator.next();
		
		assertEquals(new Rectangle2D.Double(315, 235, 110, 90), p1.getBounds());
		assertEquals("p1", p1.getName().toString());
		
		List<ChildNode> children = p1.getChildren();
		assertEquals(1, children.size());
		ClassNode c1 = (ClassNode) children.get(0);
		assertEquals(new Rectangle2D.Double(320, 260, 100, 60), c1.getBounds());
		assertEquals(p1, c1.getParent());
		assertEquals("C1", c1.getName().toString());

		assertEquals("p2", p2.getName().toString());
		assertEquals(new Rectangle2D.Double(477, 130, 100, 80), p2.getBounds());
		children = p2.getChildren();
		assertEquals(0, children.size());

		assertEquals("p3", p3.getName().toString());
		assertEquals(new Rectangle2D.Double(630, 280, 290, 120), p3.getBounds());
		children = p3.getChildren();
		assertEquals(1,children.size());
		PackageNode p4 = (PackageNode) children.get(0);
		assertEquals("p4", p4.getName().toString());
		assertEquals(new Rectangle2D.Double(635, 305, 280, 90), p4.getBounds());
		
		children = p4.getChildren();
		assertEquals(2,children.size());
		InterfaceNode i1 = (InterfaceNode) children.get(0);
		assertEquals(new Rectangle2D.Double(640, 330, 100, 60), i1.getBounds());
		ClassNode c2 = (ClassNode) children.get(1);
		assertEquals(new Rectangle2D.Double(810, 330, 100, 60), c2.getBounds());
		assertEquals("C2", c2.getName().toString());
		
		NoteNode n1 = (NoteNode) nIterator.next();
		assertEquals(new Rectangle2D.Double(490, 160, 60, 40), n1.getBounds());
		assertEquals("n1", n1.getText().toString());

		Collection<Edge> edges = pGraph.getEdges();
		assertEquals(3, edges.size());
		Iterator<Edge> eIterator = edges.iterator();
		
		DependencyEdge e1 = (DependencyEdge) eIterator.next();
		DependencyEdge e2 = (DependencyEdge) eIterator.next();
		DependencyEdge e3 = (DependencyEdge) eIterator.next();
		
		assertEquals("e1", e1.getMiddleLabel().toString());
		assertEquals("e2", e2.getMiddleLabel().toString());
		assertEquals("e3", e3.getMiddleLabel().toString());
		
		assertEquals( c1, e1.getStart());
		assertEquals( i1, e1.getEnd());
		
		assertEquals( c2, e2.getStart());
		assertEquals( i1, e2.getEnd());
		
		assertEquals( p3, e3.getStart());
		assertEquals( p2, e3.getEnd());
		

	}
	
	private void verifyClassDiagram(Graph pGraph)
	{
		Collection<Node> nodes = pGraph.getRootNodes();
		
		assertEquals(7, nodes.size());
		Iterator<Node> nIterator = nodes.iterator();
		
		ClassNode node1 = (ClassNode) nIterator.next();
		InterfaceNode node2 = (InterfaceNode) nIterator.next();
		ClassNode node3 = (ClassNode) nIterator.next();
		ClassNode node4 = (ClassNode) nIterator.next();
		PackageNode node6 = (PackageNode) nIterator.next();
		NoteNode node5 = (NoteNode) nIterator.next();
		PointNode node8 = (PointNode) nIterator.next();
		
		assertEquals("", node1.getAttributes().getText());
		assertEquals("", node1.getMethods().getText());
		assertEquals("Class1", node1.getName().getText());
		assertNull(node1.getParent());
		assertEquals(new Rectangle2D.Double(460, 370, 100, 60), node1.getBounds());
		
		assertEquals("", node2.getMethods().getText());
		assertEquals("\u00ABinterface\u00BB\n", node2.getName().getText());
		assertNull(node2.getParent());
		assertEquals(new Rectangle2D.Double(460, 250, 100, 60), node2.getBounds());
		
		assertEquals("foo", node3.getAttributes().getText());
		assertEquals("bar", node3.getMethods().getText());
		assertEquals("Class2", node3.getName().getText());
		assertNull(node3.getParent());
		assertEquals(new Rectangle2D.Double(460, 520, 100, 60), node3.getBounds());
		
		assertEquals("", node4.getAttributes().getText());
		assertEquals("", node4.getMethods().getText());
		assertEquals("Class3", node4.getName().getText());
		assertNull(node4.getParent());
		assertEquals(new Rectangle2D.Double(630, 370, 100, 60), node4.getBounds());
		
		assertEquals("A note", node5.getText().getText());
		assertEquals(new Rectangle2D.Double(700, 530, 60, 40), node5.getBounds());
		
		List<ChildNode> children = node6.getChildren();
		assertEquals(1, children.size());
		ClassNode node7 = (ClassNode) children.get(0);
		assertEquals("", node6.getContents().getText());
		assertEquals("Package", node6.getName());
		assertNull(node6.getParent());
		assertEquals(new Rectangle2D.Double(275, 345, 110, 90), node6.getBounds());

		assertEquals("", node7.getAttributes().getText());
		assertEquals("", node7.getMethods().getText());
		assertEquals("Class", node7.getName().getText());
		assertEquals(node6,node7.getParent());
		assertEquals(new Rectangle2D.Double(280, 370, 100, 60), node7.getBounds());
		
		assertEquals(new Rectangle2D.Double(694, 409, 0.01, 0.01), node8.getBounds());
		
		Collection<Edge> edges = pGraph.getEdges();
		assertEquals(6, edges.size());
		Iterator<Edge> eIterator = edges.iterator();
		
		NoteEdge edge5 = (NoteEdge) eIterator.next();
		assertEquals(new Rectangle2D.Double(694, 409, 31, 121), edge5.getBounds());
		assertEquals(node5, edge5.getStart());
		assertEquals(node8, edge5.getEnd());
		
		ClassRelationshipEdge edge6 = (ClassRelationshipEdge) eIterator.next();
		assertEquals(new Rectangle2D.Double(380, 381, 80, 24), edge6.getBounds());
		assertEquals(node7, edge6.getEnd());
		assertEquals("", edge6.getEndLabel());
		assertEquals("e1", edge6.getMiddleLabel());
		assertEquals(node1, edge6.getStart());
		assertEquals("", edge6.getStartLabel());
		
		ClassRelationshipEdge edge1 = (ClassRelationshipEdge) eIterator.next();
		assertEquals(new Rectangle2D.Double(505, 310, 22, 60), edge1.getBounds());
		assertEquals(node2, edge1.getEnd());
		assertEquals("", edge1.getEndLabel());
		assertEquals("e2", edge1.getMiddleLabel());
		assertEquals(node1, edge1.getStart());
		assertEquals("", edge1.getStartLabel());
		
		ClassRelationshipEdge edge2 = (ClassRelationshipEdge) eIterator.next();
		assertEquals(new Rectangle2D.Double(505, 430, 22, 90), edge2.getBounds());
		assertEquals(node1, edge2.getEnd());
		assertEquals("", edge2.getEndLabel());
		assertEquals("e3", edge2.getMiddleLabel());
		assertEquals(node3, edge2.getStart());
		assertEquals("", edge2.getStartLabel());
		
		ClassRelationshipEdge edge3 = (ClassRelationshipEdge) eIterator.next();
		assertEquals(new Rectangle2D.Double(560, 375, 70, 25), edge3.getBounds());
		assertEquals(node4, edge3.getEnd());
		assertEquals("*", edge3.getEndLabel());
		assertEquals("e4", edge3.getMiddleLabel());
		assertEquals(node1, edge3.getStart());
		assertEquals("1", edge3.getStartLabel());
		
		ClassRelationshipEdge edge4 = (ClassRelationshipEdge) eIterator.next();
		assertEquals(new Rectangle2D.Double(560, 400, 70, 150), edge4.getBounds());
		assertEquals(node3, edge4.getEnd());
		assertEquals("", edge4.getEndLabel());
		assertEquals("e5", edge4.getMiddleLabel());
		assertEquals(node4, edge4.getStart());
		assertEquals("", edge4.getStartLabel());
	}
	
	private void verifySequenceDiagram(Graph pGraph)
	{
		Collection<Node> nodes = pGraph.getRootNodes();
		assertEquals(5, nodes.size());
		Iterator<Node> nIterator = nodes.iterator();
		
		ImplicitParameterNode object1 = (ImplicitParameterNode) nIterator.next();
		ImplicitParameterNode object2 = (ImplicitParameterNode) nIterator.next();
		ImplicitParameterNode object3 = (ImplicitParameterNode) nIterator.next();
		NoteNode note = (NoteNode) nIterator.next();
		PointNode point = (PointNode) nIterator.next();
		
		assertEquals(new Rectangle2D.Double(160,0,100,219), object1.getBounds());
		List<ChildNode> o1children = object1.getChildren();
		assertEquals(2, o1children.size());
		assertEquals("object1:Type1", object1.getName().toString());
		CallNode init = (CallNode) o1children.get(0);
		CallNode selfCall = (CallNode) o1children.get(1);
		
		assertEquals(new Rectangle2D.Double(370,0,80,219), object2.getBounds());
		List<ChildNode> o2children = object2.getChildren();
		assertEquals(1, o2children.size());
		assertEquals(":Type2", object2.getName().toString());
		CallNode o2Call = (CallNode) o2children.get(0);
		
		assertEquals(new Rectangle2D.Double(590,0,80,219), object3.getBounds());
		List<ChildNode> o3children = object3.getChildren();
		assertEquals(1, o3children.size());
		assertEquals("object3:", object3.getName().toString());
		CallNode o3Call = (CallNode) o3children.get(0);
		
		assertEquals(new Rectangle2D.Double(202,77,16,88), init.getBounds());
		assertEquals(object1, init.getParent());
		assertFalse(init.isOpenBottom());
		
		assertEquals(new Rectangle2D.Double(210,106,16,39), selfCall.getBounds());
		assertEquals(object1, selfCall.getParent());
		assertFalse(selfCall.isOpenBottom());
		
		assertEquals(new Rectangle2D.Double(402,125,16,74), o2Call.getBounds());
		assertEquals(object2, o2Call.getParent());
		assertFalse(o2Call.isOpenBottom());
		
		assertEquals(new Rectangle2D.Double(622,149,16,30), o3Call.getBounds());
		assertEquals(object3, o3Call.getParent());
		assertFalse(o3Call.isOpenBottom());
		
		assertEquals(new Rectangle2D.Double(440,200,60,40), note.getBounds());
		assertEquals("A note", note.getText().toString());
		
		assertEquals(new Rectangle2D.Double(409,189,0.01,0.01), point.getBounds());
	
		Collection<Edge> edges = pGraph.getEdges();
		assertEquals(6, edges.size());
		Iterator<Edge> eIterator = edges.iterator();
		
		CallEdge self = (CallEdge) eIterator.next(); 
		CallEdge signal = (CallEdge) eIterator.next(); 
		CallEdge call1 = (CallEdge) eIterator.next(); 
		ReturnEdge ret1 = (ReturnEdge) eIterator.next(); 
		ReturnEdge retC = (ReturnEdge) eIterator.next(); 
		NoteEdge nedge = (NoteEdge) eIterator.next(); 
		
		assertEquals(new Rectangle2D.Double(218, 82, 77, 29), self.getBounds());
		assertEquals(selfCall, self.getEnd());
		assertEquals("selfCall()", self.getMiddleLabel());
		assertEquals(init, self.getStart());
		assertFalse(self.isSignal());
		
		assertEquals(new Rectangle2D.Double(226, 106, 176, 19), signal.getBounds());
		assertEquals(o2Call, signal.getEnd());
		assertEquals("signal", signal.getMiddleLabel());
		assertEquals(selfCall, signal.getStart());
		assertTrue(signal.isSignal());
		
		assertEquals(new Rectangle2D.Double(418, 130, 204, 24), call1.getBounds());
		assertEquals(o3Call, call1.getEnd());
		assertEquals("call1()", call1.getMiddleLabel());
		assertEquals(o2Call, call1.getStart());
		assertFalse(call1.isSignal());
		
		assertEquals(new Rectangle2D.Double(418, 160, 204, 24), ret1.getBounds());
		assertEquals(o2Call, ret1.getEnd());
		assertEquals("r1", ret1.getMiddleLabel());
		assertEquals(o3Call, ret1.getStart());
		
		assertEquals(new Rectangle2D.Double(226, 194, 176, 10), retC.getBounds());
		assertEquals(selfCall, retC.getEnd());
		assertEquals("", retC.getMiddleLabel());
		assertEquals(o2Call, retC.getStart());
		
		assertEquals(new Rectangle2D.Double(409, 189, 31, 16), nedge.getBounds());
		assertEquals(point, nedge.getEnd());
		assertEquals(note, nedge.getStart());
	}
	
	private void verifyStateDiagram(Graph pGraph)
	{
		Collection<Node> nodes = pGraph.getRootNodes();
		assertEquals(7, nodes.size());
		
		Iterator<Node> nIterator = nodes.iterator();
		StateNode s1 = (StateNode) nIterator.next(); 
		StateNode s2 = (StateNode) nIterator.next(); 
		StateNode s3 = (StateNode) nIterator.next(); 
		CircularStateNode start = (CircularStateNode) nIterator.next(); 
		CircularStateNode end = (CircularStateNode) nIterator.next(); 
		NoteNode note = (NoteNode) nIterator.next();
		PointNode point = (PointNode) nIterator.next();
		
		assertEquals(new Rectangle2D.Double(250, 100, 80, 60), s1.getBounds());
		assertEquals("S1", s1.getName().toString());
		
		assertEquals(new Rectangle2D.Double(510, 100, 80, 60), s2.getBounds());
		assertEquals("S2", s2.getName().toString());
		
		assertEquals(new Rectangle2D.Double(520, 310, 80, 60), s3.getBounds());
		assertEquals("S3", s3.getName().toString());
		
		assertEquals(new Rectangle2D.Double(150, 70, 14, 14), start.getBounds());
		assertFalse(start.isFinal());
		
		assertEquals(new Rectangle2D.Double(640, 230, 20, 20), end.getBounds());
		assertTrue(end.isFinal());
		
		assertEquals("A note\non two lines", note.getText().getText());
		assertEquals(new Rectangle2D.Double(690, 320, 80, 40), note.getBounds());
		
		assertEquals(new Rectangle2D.Double(576, 339, 0.01, 0.01), point.getBounds());
		
		Collection<Edge> edges = pGraph.getEdges();
		assertEquals(7, edges.size());
		Iterator<Edge> eIterator = edges.iterator();
		
		NoteEdge ne = (NoteEdge) eIterator.next();
		StateTransitionEdge fromStart = (StateTransitionEdge) eIterator.next(); 
		StateTransitionEdge e1 = (StateTransitionEdge) eIterator.next(); 
		StateTransitionEdge e2 = (StateTransitionEdge) eIterator.next(); 
		StateTransitionEdge self = (StateTransitionEdge) eIterator.next(); 
		StateTransitionEdge toEnd = (StateTransitionEdge) eIterator.next(); 
		StateTransitionEdge toS3 = (StateTransitionEdge) eIterator.next(); 
		
		assertEquals(new Rectangle2D.Double(576, 339, 114, 1), ne.getBounds());
		assertEquals(note, ne.getStart());
		assertEquals(point, ne.getEnd());
		
		assertEquals(new Rectangle2D.Double(163, 71, 87, 39), fromStart.getBounds());
		assertEquals(start, fromStart.getStart());
		assertEquals(s1, fromStart.getEnd());
		assertEquals("start", fromStart.getLabel().toString());
		
		assertEquals(new Rectangle2D.Double(330, 99, 180, 28), e1.getBounds());
		assertEquals(s1, e1.getStart());
		assertEquals(s2, e1.getEnd());
		assertEquals("e1", e1.getLabel().toString());
		
		assertEquals(new Rectangle2D.Double(330, 133, 180, 28), e2.getBounds());
		assertEquals(s2, e2.getStart());
		assertEquals(s1, e2.getEnd());
		assertEquals("e2", e2.getLabel().toString());
		
		assertEquals(new Rectangle2D.Double(575, 70, 30, 45), self.getBounds());
		assertEquals(s2, self.getStart());
		assertEquals(s2, self.getEnd());
		assertEquals("self", self.getLabel().toString());
		
		assertEquals(new Rectangle2D.Double(582, 246, 61, 64), toEnd.getBounds());
		assertEquals(s3, toEnd.getStart());
		assertEquals(end, toEnd.getEnd());
		assertEquals("", toEnd.getLabel().toString());
		
		assertEquals(new Rectangle2D.Double(554, 160, 17, 150), toS3.getBounds());
		assertEquals(s2, toS3.getStart());
		assertEquals(s3, toS3.getEnd());
		assertEquals("", toS3.getLabel().toString());
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
		
		assertEquals(new Rectangle2D.Double(240, 130, 120, 100), type1.getBounds());
		List<ChildNode> children = type1.getChildren();
		assertEquals(1, children.size());
		assertEquals(":Type1", type1.getName().toString());
		
		FieldNode name = (FieldNode) children.get(0);
		assertEquals(new Rectangle2D.Double(252.5, 209, 87, 16), name.getBounds());
		assertEquals(0,name.getAxisX(),0.000001);
		assertEquals("name", name.getName().toString());
		assertEquals(type1, name.getParent());
		assertEquals("", name.getValue().toString());
		assertFalse(name.isBoxedValue());

		assertEquals(new Rectangle2D.Double(440, 290, 120, 140), blank.getBounds());
		children = blank.getChildren();
		assertEquals(3, children.size());
		assertEquals("", blank.getName().toString());
		FieldNode name2 = (FieldNode) children.get(0);
		FieldNode name3 = (FieldNode) children.get(1);
		FieldNode name4 = (FieldNode) children.get(2);
		
		assertEquals(new Rectangle2D.Double(445.5, 367, 99, 16), name2.getBounds());
		assertEquals(0,name2.getAxisX(),0.000001);
		assertEquals("name2", name2.getName().toString());
		assertEquals(blank, name2.getParent());
		assertEquals("value", name2.getValue().toString());
		assertFalse(name2.isBoxedValue());
		
		assertEquals(new Rectangle2D.Double(445.5, 388, 99, 16), name3.getBounds());
		assertEquals(0,name3.getAxisX(),0.000001);
		assertEquals("name3", name3.getName().toString());
		assertEquals(blank, name3.getParent());
		assertEquals("value", name3.getValue().toString());
		assertTrue(name3.isBoxedValue());
		
		assertEquals(new Rectangle2D.Double(445.5, 409, 99, 16), name4.getBounds());
		assertEquals(0,name4.getAxisX(),0.000001);
		assertEquals("name4", name4.getName().toString());
		assertEquals(blank, name4.getParent());
		assertEquals("", name4.getValue().toString());
		assertFalse(name4.isBoxedValue());

		assertEquals(new Rectangle2D.Double(540, 150, 80, 60), object2.getBounds());
		children = object2.getChildren();
		assertEquals(0, children.size());
		assertEquals("object2:", object2.getName().toString());
		
		assertEquals(new Rectangle2D.Double(610, 300, 80, 60), type3.getBounds());
		children = type3.getChildren();
		assertEquals(0, children.size());
		assertEquals(":Type3", type3.getName().toString());

		assertEquals("A note", note.getText().getText());
		assertEquals(new Rectangle2D.Double(280, 330, 60, 40), note.getBounds());
		
		assertEquals(new Rectangle2D.Double(281, 216, 0.01, 0.01), p1.getBounds());
		
		assertEquals(new Rectangle2D.Double(474, 339, 0.01, 0.01), p2.getBounds());
		
		Collection<Edge> edges = pGraph.getEdges();
		assertEquals(6, edges.size());
		Iterator<Edge> eIt = edges.iterator();
		
		ObjectReferenceEdge o1 = (ObjectReferenceEdge) eIt.next();
		ObjectReferenceEdge o2 = (ObjectReferenceEdge) eIt.next();
		ObjectReferenceEdge o3 = (ObjectReferenceEdge) eIt.next();
		NoteEdge ne1 = (NoteEdge) eIt.next();
		NoteEdge ne2 = (NoteEdge) eIt.next();
		ObjectCollaborationEdge cr1 = (ObjectCollaborationEdge) eIt.next();
		
		assertEquals(new Rectangle2D.Double(296, 180, 84, 37), o1.getBounds());
		assertEquals(name, o1.getStart());
		assertEquals(type1, o1.getEnd());
		
		assertEquals(new Rectangle2D.Double(296, 217, 144, 143), o2.getBounds());
		assertEquals(name, o2.getStart());
		assertEquals(blank, o2.getEnd());
		
		assertEquals(new Rectangle2D.Double(500, 210, 80, 80), cr1.getBounds());
		assertEquals(object2, cr1.getEnd());
		assertEquals("", cr1.getEndLabel());
		assertEquals("e1", cr1.getMiddleLabel().toString());
		assertEquals(blank, cr1.getStart());
		assertEquals("", cr1.getStartLabel().toString());
		
		assertEquals(new Rectangle2D.Double(495, 330, 115, 87), o3.getBounds());
		assertEquals(name4, o3.getStart());
		assertEquals(type3, o3.getEnd());
		
		assertEquals(new Rectangle2D.Double(281, 216, 25, 114), ne1.getBounds());
		assertEquals(note, ne1.getStart());
		assertEquals(p1, ne1.getEnd());
		
		assertEquals(new Rectangle2D.Double(340, 339, 134, 9), ne2.getBounds());
		assertEquals(note, ne2.getStart());
		assertEquals(p2, ne2.getEnd());
	}
}
