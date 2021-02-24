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
package ca.mcgill.cs.jetuml.persistence;

import static ca.mcgill.cs.jetuml.persistence.PersistenceTestUtils.build;
import static ca.mcgill.cs.jetuml.persistence.PersistenceTestUtils.findRootNode;
import static ca.mcgill.cs.jetuml.testutils.GeometryUtils.osDependent;
import static ca.mcgill.cs.jetuml.viewers.edges.EdgeViewerRegistry.getBounds;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.edges.AggregationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.edges.GeneralizationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ObjectCollaborationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ObjectReferenceEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ReturnEdge;
import ca.mcgill.cs.jetuml.diagram.edges.StateTransitionEdge;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseAssociationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseDependencyEdge;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseGeneralizationEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ActorNode;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.FieldNode;
import ca.mcgill.cs.jetuml.diagram.nodes.FinalStateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.diagram.nodes.InitialStateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.InterfaceNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ObjectNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;
import ca.mcgill.cs.jetuml.diagram.nodes.StateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.UseCaseNode;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.nodes.NodeViewerRegistry;

public class TestPersistenceService
{
	private static final String TEST_FILE_NAME = "testdata/tmp";
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	private int numberOfRootNodes(Diagram pDiagram)
	{
		int sum = 0;
		for( @SuppressWarnings("unused") Node node : pDiagram.rootNodes() )
		{
			sum++;
		}
		return sum;
	}
	
	private int numberOfEdges(Diagram pDiagram)
	{
		int sum = 0;
		for( @SuppressWarnings("unused") Edge edge : pDiagram.edges() )
		{
			sum++;
		}
		return sum;
	}
	
	@Test
	public void testClassDiagram() throws Exception
	{
		Diagram graph = PersistenceService.read(new File("testdata/testPersistenceService.class.jet")).diagram();
		verifyClassDiagram(graph);
		
		File tmp = new File(TEST_FILE_NAME);
		tmp.delete();
		PersistenceService.save(graph, tmp);
		graph = PersistenceService.read(tmp).diagram();
		verifyClassDiagram(graph);
		tmp.delete();
	}
	
	@Test
	public void testClassDiagramContainment() throws Exception
	{
		Diagram graph = PersistenceService.read(new File("testdata/testPersistenceService2.class.jet")).diagram();
		verifyClassDiagram2(graph);
		
		File tmp = new File(TEST_FILE_NAME);
		tmp.delete();
		PersistenceService.save(graph, tmp);
		graph = PersistenceService.read(tmp).diagram();
		verifyClassDiagram2(graph);
		tmp.delete();
	}
	
	@Test
	public void testSequenceDiagram() throws Exception
	{
		Diagram graph = PersistenceService.read(new File("testdata/testPersistenceService.sequence.jet")).diagram();
		verifySequenceDiagram(graph);
		
		File tmp = new File(TEST_FILE_NAME);
		tmp.delete();
		PersistenceService.save(graph, tmp);
		graph = PersistenceService.read(tmp).diagram();
		verifySequenceDiagram(graph);
		tmp.delete();
	}
	
	@Test
	public void testStateDiagram() throws Exception
	{
		Diagram graph = PersistenceService.read(new File("testdata/testPersistenceService.state.jet")).diagram();
		verifyStateDiagram(graph);

		File tmp = new File(TEST_FILE_NAME);
		tmp.delete();
		PersistenceService.save(graph, tmp);
		graph = PersistenceService.read(tmp).diagram();
		verifyStateDiagram(graph);
		tmp.delete();
	}
	
	@Test
	public void testObjectDiagram() throws Exception
	{
		Diagram graph = PersistenceService.read(new File("testdata/testPersistenceService.object.jet")).diagram();
		verifyObjectDiagram(graph);

		File tmp = new File(TEST_FILE_NAME);
		tmp.delete();
		PersistenceService.save(graph, tmp);
		graph = PersistenceService.read(tmp).diagram();
		verifyObjectDiagram(graph);
		tmp.delete();
	}
	
	@Test
	public void testUseCaseDiagram() throws Exception
	{
		Diagram graph = PersistenceService.read(new File("testdata/testPersistenceService.usecase.jet")).diagram();
		verifyUseCaseDiagram(graph);

		File tmp = new File(TEST_FILE_NAME);
		tmp.delete();
		PersistenceService.save(graph, tmp);
		graph = PersistenceService.read(tmp).diagram();
		verifyUseCaseDiagram(graph);
		tmp.delete();
	}
	
	private void verifyUseCaseDiagram(Diagram pDiagram)
	{
		assertEquals(9, numberOfRootNodes(pDiagram));
		UseCaseNode u1 = (UseCaseNode) findRootNode(pDiagram, UseCaseNode.class, build("name", "Use case 1"));
		UseCaseNode u2 = (UseCaseNode) findRootNode(pDiagram, UseCaseNode.class, build("name", "Use case 2"));
		UseCaseNode u3 = (UseCaseNode) findRootNode(pDiagram, UseCaseNode.class, build("name", "Use case 3"));
		ActorNode a1 = (ActorNode) findRootNode(pDiagram, ActorNode.class, build("name", "Actor"));
		ActorNode a2 = (ActorNode) findRootNode(pDiagram, ActorNode.class, build("name", "Actor2"));
		NoteNode n1 = (NoteNode) findRootNode(pDiagram, NoteNode.class, build());
		PointNode p1 = (PointNode) findRootNode(pDiagram, PointNode.class, build());
		UseCaseNode u4 = (UseCaseNode) findRootNode(pDiagram, UseCaseNode.class, build("name", "Use case 4"));
		ActorNode a3 = (ActorNode) findRootNode(pDiagram, ActorNode.class, build("name", "Actor3"));
		
		assertEquals(new Rectangle(440, 40, 110, 40), NodeViewerRegistry.getBounds(u1));
		assertEquals("Use case 1", u1.getName().toString());
		
		assertEquals(new Rectangle(460, 130, 110, 40), NodeViewerRegistry.getBounds(u2));
		assertEquals("Use case 2", u2.getName().toString());
		
		assertEquals(new Rectangle(460, 230, 110, 40), NodeViewerRegistry.getBounds(u3));
		assertEquals("Use case 3", u3.getName().toString());
		
		assertEquals(new Rectangle(270, 50, 48, osDependent(91, 90, 87)), NodeViewerRegistry.getBounds(a1));
		assertEquals("Actor", a1.getName().toString());
		
		assertEquals(new Rectangle(280, 230, osDependent(49, 48, 49), osDependent(91, 90, 87)), NodeViewerRegistry.getBounds(a2));
		assertEquals("Actor2", a2.getName().toString());
		
		assertEquals("A note", n1.getName());
		assertEquals(new Rectangle(700, 50, 60, 40), NodeViewerRegistry.getBounds(n1));
		
		assertEquals(new Rectangle(567, 56, 0, 0), NodeViewerRegistry.getBounds(p1));
		
		assertEquals(new Rectangle(650, 150, 110, 40), NodeViewerRegistry.getBounds(u4));
		assertEquals("Use case 4", u4.getName().toString());
		
		assertEquals(new Rectangle(650, 150, 110, 40), NodeViewerRegistry.getBounds(u4));
		assertEquals("Actor3", a3.getName().toString());
		
		assertEquals(10,  numberOfEdges(pDiagram));
		Iterator<Edge> eIt = pDiagram.edges().iterator();
		
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
		
		assertEquals(new Rectangle(565,54,135,13), getBounds(cr1));
		assertTrue(cr1.getStart() == n1);
		assertTrue(cr1.getEnd() == p1);
		
		assertEquals(new Rectangle(osDependent(237, 236, 236), 120, osDependent(33, 34, 34), 38), getBounds(cr2));
		assertTrue(cr2.getStart() == a3);
		assertTrue(cr2.getEnd() == a1);
		
		assertEquals(new Rectangle(osDependent(228,228, 229), 207, osDependent(62,63, 61), 44), getBounds(cr3));
		assertTrue( cr3.getStart() == a3);
		assertTrue( cr3.getEnd() == a2);
		assertTrue( cr3.properties().get("Dependency Type").get() == UseCaseDependencyEdge.Type.Extend);
		
		assertEquals(new Rectangle(316, 61, 125, 30), getBounds(cr4));
		assertTrue( cr4.getStart() == a1 );
		assertTrue( cr4.getEnd() == u1 );
		
		assertEquals(new Rectangle(osDependent(327, 326, 326), 158, osDependent(141, 142, 142), 103), getBounds(cr5));
		assertTrue( cr5.getStart() == a2 );
		assertTrue( cr5.getEnd() == u2 );
		
		assertEquals(new Rectangle(osDependent(327, 326, 326), 250, osDependent(133, 134, 134), 22), getBounds(cr6));
		assertTrue( cr6.getStart() == a2 );
		assertTrue( cr6.getEnd() == u3 );
		
		assertEquals(new Rectangle(499,77,12,54), getBounds(cr7));
		assertTrue( cr7.getStart() == u2 );
		assertTrue( cr7.getEnd() == u1 );

		assertEquals(new Rectangle(osDependent(483,483, 484),169,osDependent(64,65, 62),62), getBounds(cr8));
		assertTrue( cr8.getStart() == u2 );
		assertTrue( cr8.getEnd() == u3 );
		assertTrue( cr8.properties().get("Dependency Type").get() == UseCaseDependencyEdge.Type.Include);
		
		assertEquals(new Rectangle(568,150,82,osDependent(27, 26, 26)), getBounds(cr9));
		assertTrue( cr9.getStart() == u2 );
		assertTrue( cr9.getEnd() == u4 );
		assertTrue( cr9.properties().get("Dependency Type").get() == UseCaseDependencyEdge.Type.Extend);
		
		assertEquals(new Rectangle(542, 67, 114, 94), getBounds(cr10));
		assertTrue( cr10.getStart() == u1 );
		assertTrue( cr10.getEnd() == u4 );
 	}
	
	private void verifyClassDiagram2(Diagram pDiagram)
	{
		assertEquals(4, numberOfRootNodes(pDiagram));
		
		PackageNode p1 = (PackageNode) findRootNode(pDiagram, PackageNode.class, build("name", "p1"));
		PackageNode p2 = (PackageNode) findRootNode(pDiagram, PackageNode.class, build("name", "p2"));
		PackageNode p3 = (PackageNode) findRootNode(pDiagram, PackageNode.class, build("name", "p3"));
		
		assertEquals(new Rectangle(310, 230, 120, 100), NodeViewerRegistry.getBounds(p1));
		assertEquals("p1", p1.getName().toString());
		
		List<Node> children = p1.getChildren();
		assertEquals(1, children.size());
		ClassNode c1 = (ClassNode) children.get(0);
		assertEquals(new Rectangle(320, 260, 100, 60), NodeViewerRegistry.getBounds(c1));
		assertEquals(p1, c1.getParent());
		assertEquals("C1", c1.getName().toString());

		assertEquals("p2", p2.getName().toString());
		assertEquals(new Rectangle(477, 130, 100, 80), NodeViewerRegistry.getBounds(p2));
		children = p2.getChildren();
		assertEquals(0, children.size());

		assertEquals("p3", p3.getName().toString());
		assertEquals(new Rectangle(620, 270, 310, 140), NodeViewerRegistry.getBounds(p3));
		children = p3.getChildren();
		assertEquals(1,children.size());
		PackageNode p4 = (PackageNode) children.get(0);
		assertEquals("p4", p4.getName().toString());
		assertEquals(new Rectangle(630, 300, 290, 100), NodeViewerRegistry.getBounds(p4));
		
		children = p4.getChildren();
		assertEquals(2,children.size());
		InterfaceNode i1 = (InterfaceNode) children.get(0);
		assertEquals(new Rectangle(640, 330, 100, 60), NodeViewerRegistry.getBounds(i1));
		ClassNode c2 = (ClassNode) children.get(1);
		assertEquals(new Rectangle(810, 330, 100, 60), NodeViewerRegistry.getBounds(c2));
		assertEquals("C2", c2.getName().toString());
		
		NoteNode n1 = (NoteNode) findRootNode(pDiagram, NoteNode.class, build());
		assertEquals(new Rectangle(490, 160, 60, 40), NodeViewerRegistry.getBounds(n1));
		assertEquals("n1", n1.getName().toString());

		assertEquals(3, numberOfEdges(pDiagram));
		Iterator<Edge> eIterator = pDiagram.edges().iterator();
		
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
	
	private void verifyClassDiagram(Diagram pDiagram)
	{
		assertEquals(7, numberOfRootNodes(pDiagram));
		
		ClassNode node1 = (ClassNode) findRootNode(pDiagram, ClassNode.class, build("name", "Class1"));
		InterfaceNode node2 = (InterfaceNode) findRootNode(pDiagram, InterfaceNode.class, build("name", ""));
		ClassNode node3 = (ClassNode) findRootNode(pDiagram, ClassNode.class, build("name", "Class2"));
		ClassNode node4 = (ClassNode) findRootNode(pDiagram, ClassNode.class, build("name", "Class3"));
		PackageNode node6 = (PackageNode) findRootNode(pDiagram, PackageNode.class, build("name", "Package"));
		NoteNode node5 = (NoteNode) findRootNode(pDiagram, NoteNode.class, build());
		PointNode node8 = (PointNode) findRootNode(pDiagram, PointNode.class, build());
		
		assertEquals("", node1.getAttributes());
		assertEquals("", node1.getMethods());
		assertEquals("Class1", node1.getName());
		assertFalse(node1.hasParent());
		assertEquals(new Rectangle(460, 370, 100, 60), NodeViewerRegistry.getBounds(node1));
		
		assertEquals("", node2.getMethods());
		assertEquals("", node2.getName());
		assertFalse(node2.hasParent());
		assertEquals(new Rectangle(460, 250, 100, 60), NodeViewerRegistry.getBounds(node2));
		
		assertEquals("foo", node3.getAttributes());
		assertEquals("bar", node3.getMethods());
		assertEquals("Class2", node3.getName());
		assertFalse(node3.hasParent());
		assertEquals(new Rectangle(460, 520, 100, osDependent(81, 78, 78)), NodeViewerRegistry.getBounds(node3));
		
		assertEquals("", node4.getAttributes());
		assertEquals("", node4.getMethods());
		assertEquals("Class3", node4.getName());
		assertFalse(node4.hasParent());
		assertEquals(new Rectangle(630, 370, 100, 60), NodeViewerRegistry.getBounds(node4));
		
		assertEquals("A note", node5.getName());
		assertEquals(new Rectangle(700, 530, 60, 40), NodeViewerRegistry.getBounds(node5));
		
		List<Node> children = node6.getChildren();
		assertEquals(1, children.size());
		ClassNode node7 = (ClassNode) children.get(0);
		assertEquals("Package", node6.getName());
		assertFalse(node6.hasParent());
		assertEquals(new Rectangle(270, 340, 120, 100), NodeViewerRegistry.getBounds(node6));

		assertEquals("", node7.getAttributes());
		assertEquals("", node7.getMethods());
		assertEquals("Class", node7.getName());
		assertEquals(node6,node7.getParent());
		assertEquals(new Rectangle(280, 370, 100, 60), NodeViewerRegistry.getBounds(node7));
		
		assertEquals(new Rectangle(694, 409, 0, 0), NodeViewerRegistry.getBounds(node8));
		
		Iterator<Edge> eIterator = pDiagram.edges().iterator();
		
		NoteEdge edge5 = (NoteEdge) eIterator.next();
		assertEquals(new Rectangle(692, 407, 33, 123), getBounds(edge5));
		assertEquals(node5, edge5.getStart());
		assertEquals(node8, edge5.getEnd());
		
		DependencyEdge edge6 = (DependencyEdge) eIterator.next();
		assertEquals(new Rectangle(378, osDependent(390,390, 384), 83, osDependent(27,26, 21)), getBounds(edge6));
		assertEquals(node7, edge6.getEnd());
		assertEquals("e1", edge6.getMiddleLabel());
		assertEquals(node1, edge6.getStart());
		
		GeneralizationEdge edge1 = (GeneralizationEdge) eIterator.next();
		assertEquals(new Rectangle(503, 308, osDependent(12, 12, 25), 62), getBounds(edge1));
		assertEquals(node2, edge1.getEnd());
		assertEquals(node1, edge1.getStart());
		
		GeneralizationEdge edge2 = (GeneralizationEdge) eIterator.next();
		assertEquals(new Rectangle(503, 428, osDependent(12,12, 25), 92), getBounds(edge2));
		assertEquals(node1, edge2.getEnd());
		assertEquals(node3, edge2.getStart());
		
		AggregationEdge edge3 = (AggregationEdge) eIterator.next();
		assertEquals(new Rectangle(558, osDependent(379,380, 379), 72, osDependent(21,20, 21)), getBounds(edge3));
		assertEquals(node4, edge3.getEnd());
		assertEquals("*", edge3.getEndLabel());
		assertEquals("e4", edge3.getMiddleLabel());
		assertEquals(node1, edge3.getStart());
		assertEquals("1", edge3.getStartLabel());
		
		AggregationEdge edge4 = (AggregationEdge) eIterator.next();
		assertEquals(new Rectangle(559, 399, 72, osDependent(161, 160, 160)), getBounds(edge4));
		assertEquals(node3, edge4.getEnd());
		assertEquals("", edge4.getEndLabel());
		assertEquals("e5", edge4.getMiddleLabel());
		assertEquals(node4, edge4.getStart());
		assertEquals("", edge4.getStartLabel());
	}
	
	private void verifySequenceDiagram(Diagram pDiagram)
	{
		assertEquals(5, numberOfRootNodes(pDiagram));
		
		ImplicitParameterNode object1 = (ImplicitParameterNode) findRootNode(pDiagram, ImplicitParameterNode.class, build("name", "object1:Type1"));
		ImplicitParameterNode object2 = (ImplicitParameterNode) findRootNode(pDiagram, ImplicitParameterNode.class, build("name", ":Type2"));
		ImplicitParameterNode object3 = (ImplicitParameterNode) findRootNode(pDiagram, ImplicitParameterNode.class, build("name", "object3:"));
		NoteNode note = (NoteNode) findRootNode(pDiagram, NoteNode.class, build());
		PointNode point = (PointNode) findRootNode(pDiagram, PointNode.class, build());
		
		assertEquals(new Rectangle(160,0,osDependent(98,94, 102),250), NodeViewerRegistry.getBounds(object1));
		List<Node> o1children = object1.getChildren();
		assertEquals(2, o1children.size());
		assertEquals("object1:Type1", object1.getName().toString());
		CallNode init = (CallNode) o1children.get(0);
		CallNode selfCall = (CallNode) o1children.get(1);
		
		assertEquals(new Rectangle(370,0,80,210), NodeViewerRegistry.getBounds(object2));
		List<Node> o2children = object2.getChildren();
		assertEquals(1, o2children.size());
		assertEquals(":Type2", object2.getName().toString());
		CallNode o2Call = (CallNode) o2children.get(0);
		
		assertEquals(new Rectangle(590,0,80,190), NodeViewerRegistry.getBounds(object3));
		List<Node> o3children = object3.getChildren();
		assertEquals(1, o3children.size());
		assertEquals("object3:", object3.getName().toString());
		CallNode o3Call = (CallNode) o3children.get(0);
		
		assertEquals(new Rectangle(osDependent(201,199, 203),80,16,150), NodeViewerRegistry.getBounds(init));
		assertEquals(object1, init.getParent());
		assertFalse(init.isOpenBottom());
		
		assertEquals(new Rectangle(osDependent(209,207, 211),100,16,110), NodeViewerRegistry.getBounds(selfCall));
		assertEquals(object1, selfCall.getParent());
		assertFalse(selfCall.isOpenBottom());
		
		assertEquals(new Rectangle(402,120,16,70), NodeViewerRegistry.getBounds(o2Call));
		assertEquals(object2, o2Call.getParent());
		assertFalse(o2Call.isOpenBottom());
		
		assertEquals(new Rectangle(622,140,16,30), NodeViewerRegistry.getBounds(o3Call));
		assertEquals(object3, o3Call.getParent());
		assertFalse(o3Call.isOpenBottom());
		
		assertEquals(new Rectangle(440,200,60,40), NodeViewerRegistry.getBounds(note));
		assertEquals("A note", note.getName().toString());
		
		assertEquals(new Rectangle(409,189,0,0), NodeViewerRegistry.getBounds(point));
	
		assertEquals(6, numberOfEdges(pDiagram));
		Iterator<Edge> eIterator = pDiagram.edges().iterator();
		
		CallEdge self = (CallEdge) eIterator.next(); 
		CallEdge signal = (CallEdge) eIterator.next(); 
		CallEdge call1 = (CallEdge) eIterator.next(); 
		ReturnEdge ret1 = (ReturnEdge) eIterator.next(); 
		ReturnEdge retC = (ReturnEdge) eIterator.next(); 
		NoteEdge nedge = (NoteEdge) eIterator.next(); 
		
		assertEquals(new Rectangle(osDependent(214,212, 216), 85, 86, osDependent(27, 26, 26)), getBounds(self));
		assertEquals(selfCall, self.getEnd());
		assertEquals("selfCall()", self.getMiddleLabel());
		assertEquals(init, self.getStart());
		assertFalse(self.isSignal());
		
		assertEquals(new Rectangle(osDependent(224,222, 226), 100, osDependent(179, 181, 177), 
				osDependent(27, 26, 26)), getBounds(signal));
		assertEquals(o2Call, signal.getEnd());
		assertEquals("signal", signal.getMiddleLabel());
		assertEquals(selfCall, signal.getStart());
		assertTrue(signal.isSignal());
		
		assertEquals(new Rectangle(417, 120, 206, osDependent(27, 26, 26)), getBounds(call1));
		assertEquals(o3Call, call1.getEnd());
		assertEquals("call1()", call1.getMiddleLabel());
		assertEquals(o2Call, call1.getStart());
		assertFalse(call1.isSignal());
		
		assertEquals(new Rectangle(416, 160, 207, osDependent(27,26, 23)), getBounds(ret1));
		assertEquals(o2Call, ret1.getEnd());
		assertEquals("r1", ret1.getMiddleLabel());
		assertEquals(o3Call, ret1.getStart());
		
		assertEquals(new Rectangle(osDependent(223,221, 225), 183, osDependent(180,182, 178), 12), getBounds(retC));
		assertEquals(selfCall, retC.getEnd());
		assertEquals("", retC.getMiddleLabel());
		assertEquals(o2Call, retC.getStart());
		
		assertEquals(new Rectangle(407, 187, 33, 18), getBounds(nedge));
		assertEquals(point, nedge.getEnd());
		assertEquals(note, nedge.getStart());
	}
	
	private void verifyStateDiagram(Diagram pDiagram)
	{
		assertEquals(7, numberOfRootNodes(pDiagram));
		
		StateNode s1 = (StateNode) findRootNode(pDiagram, StateNode.class, build("name", "S1"));
		StateNode s2 = (StateNode) findRootNode(pDiagram, StateNode.class, build("name", "S2"));
		StateNode s3 = (StateNode) findRootNode(pDiagram, StateNode.class, build("name", "S3"));
		InitialStateNode start = (InitialStateNode) findRootNode(pDiagram, InitialStateNode.class, build());
		FinalStateNode end = (FinalStateNode) findRootNode(pDiagram, FinalStateNode.class, build());
		NoteNode note = (NoteNode) findRootNode(pDiagram, NoteNode.class, build());
		PointNode point = (PointNode) findRootNode(pDiagram, PointNode.class, build());
		
		assertEquals(new Rectangle(250, 100, 80, 60), NodeViewerRegistry.getBounds(s1));
		assertEquals("S1", s1.getName().toString());
		
		assertEquals(new Rectangle(510, 100, 80, 60), NodeViewerRegistry.getBounds(s2));
		assertEquals("S2", s2.getName().toString());
		
		assertEquals(new Rectangle(520, 310, 80, 60), NodeViewerRegistry.getBounds(s3));
		assertEquals("S3", s3.getName().toString());
		
		assertEquals(new Rectangle(150, 70, 20, 20), NodeViewerRegistry.getBounds(start));
		
		assertEquals(new Rectangle(640, 230, 20, 20), NodeViewerRegistry.getBounds(end));
		
		assertEquals("A note\non two lines", note.getName());
		assertEquals(new Rectangle(690, 320, osDependent(86,81, 86), osDependent(43, 40, 40)), NodeViewerRegistry.getBounds(note));
		
		assertEquals(new Rectangle(576, 339, 0, 0), NodeViewerRegistry.getBounds(point));
		
		assertEquals(7,  numberOfEdges(pDiagram));
		Iterator<Edge> eIterator = pDiagram.edges().iterator();
		
		NoteEdge ne = (NoteEdge) eIterator.next();
		StateTransitionEdge fromStart = (StateTransitionEdge) eIterator.next(); 
		StateTransitionEdge e1 = (StateTransitionEdge) eIterator.next(); 
		StateTransitionEdge e2 = (StateTransitionEdge) eIterator.next(); 
		StateTransitionEdge self = (StateTransitionEdge) eIterator.next(); 
		StateTransitionEdge toEnd = (StateTransitionEdge) eIterator.next(); 
		StateTransitionEdge toS3 = (StateTransitionEdge) eIterator.next(); 
		
		assertEquals(new Rectangle(574, 337, 116, 3), getBounds(ne));
		assertEquals(note, ne.getStart());
		assertEquals(point, ne.getEnd());
		
		assertEquals(new Rectangle(168, osDependent(75,76, 75), 82, osDependent(35,34, 35)), getBounds(fromStart));
		assertEquals(start, fromStart.getStart());
		assertEquals(s1, fromStart.getEnd());
		assertEquals("start", fromStart.getMiddleLabel().toString());
		
		assertEquals(new Rectangle(328, osDependent(102,103, 101), 182, osDependent(26,25, 26)), getBounds(e1));
		assertEquals(s1, e1.getStart());
		assertEquals(s2, e1.getEnd());
		assertEquals("e1", e1.getMiddleLabel().toString());
		
		assertEquals(new Rectangle(328, 131, 182, osDependent(25,24, 25)), getBounds(e2));
		assertEquals(s2, e2.getStart());
		assertEquals(s1, e2.getEnd());
		assertEquals("e2", e2.getMiddleLabel().toString());
		
		assertEquals(new Rectangle(545, 55, 60, 60), getBounds(self));
		assertEquals(s2, self.getStart());
		assertEquals(s2, self.getEnd());
		assertEquals("self", self.getMiddleLabel().toString());
		
		assertEquals(new Rectangle(581, 245, 62, 65), getBounds(toEnd));
		assertEquals(s3, toEnd.getStart());
		assertEquals(end, toEnd.getEnd());
		assertEquals("", toEnd.getMiddleLabel().toString());
		assertEquals(new Rectangle(552, 158, 14, 152), getBounds(toS3));
		assertEquals(s2, toS3.getStart());
		assertEquals(s3, toS3.getEnd());
		assertEquals("", toS3.getMiddleLabel().toString());
	}
	
	private void verifyObjectDiagram(Diagram pDiagram)
	{
		assertEquals(7, numberOfRootNodes(pDiagram));
		
		ObjectNode type1 = (ObjectNode) findRootNode(pDiagram, ObjectNode.class, build("name", ":Type1"));
		ObjectNode blank = (ObjectNode) findRootNode(pDiagram, ObjectNode.class, build("name", ""));
		ObjectNode object2 = (ObjectNode) findRootNode(pDiagram, ObjectNode.class, build("name", "object2:"));
		ObjectNode type3 = (ObjectNode) findRootNode(pDiagram, ObjectNode.class, build("name", ":Type3"));

		NoteNode note = (NoteNode) findRootNode(pDiagram, NoteNode.class, build());
		PointNode p1 = (PointNode) findRootNode(pDiagram, PointNode.class, build("x", 281));
		PointNode p2 = (PointNode) findRootNode(pDiagram, PointNode.class, build("x", 474));
		
		assertEquals(new Rectangle(240, 130, osDependent(120, 110, 100), osDependent(100, 100, 100)), NodeViewerRegistry.getBounds(type1));
		List<Node> children = type1.getChildren();
		assertEquals(1, children.size());
		assertEquals(":Type1", type1.getName().toString());
		
		FieldNode name = (FieldNode) children.get(0);
		assertEquals(new Rectangle(245, 200, osDependent(110, 100, 90), osDependent(27, 26, 21)), NodeViewerRegistry.getBounds(name));
		assertEquals("name", name.getName().toString());
		assertEquals(type1, name.getParent());
		assertEquals("", name.getValue().toString());

		assertEquals(new Rectangle(440, 290, osDependent(130, 130, 110), osDependent(170, 160, 160)), NodeViewerRegistry.getBounds(blank));
		children = blank.getChildren();
		assertEquals(3, children.size());
		assertEquals("", blank.getName().toString());
		FieldNode name2 = (FieldNode) children.get(0);
		FieldNode name3 = (FieldNode) children.get(1);
		FieldNode name4 = (FieldNode) children.get(2);
		
		assertEquals(new Rectangle(445, 360, osDependent(120, 120, 100), osDependent(27, 26, 26)), NodeViewerRegistry.getBounds(name2));
		assertEquals("name2", name2.getName().toString());
		assertEquals(blank, name2.getParent());
		assertEquals("value", name2.getValue().toString());
		
		assertEquals(new Rectangle(445, osDependent(392, 391, 391), osDependent(120, 120, 100), osDependent(27, 26, 26)), NodeViewerRegistry.getBounds(name3));
		assertEquals("name3", name3.getName().toString());
		assertEquals(blank, name3.getParent());
		assertEquals("value", name3.getValue().toString());
		
		assertEquals(new Rectangle(445, osDependent(424, 422, 422), osDependent(120, 120, 100), osDependent(27,26,26)), NodeViewerRegistry.getBounds(name4));
		assertEquals("name4", name4.getName().toString());
		assertEquals(blank, name4.getParent());
		assertEquals("", name4.getValue().toString());

		assertEquals(new Rectangle(540, 150, 80, 60), NodeViewerRegistry.getBounds(object2));
		children = object2.getChildren();
		assertEquals(0, children.size());
		assertEquals("object2:", object2.getName().toString());
		
		assertEquals(new Rectangle(610, 300, 80, 60), NodeViewerRegistry.getBounds(type3));
		children = type3.getChildren();
		assertEquals(0, children.size());
		assertEquals(":Type3", type3.getName().toString());

		assertEquals("A note", note.getName());
		assertEquals(new Rectangle(280, 330, 60, 40), NodeViewerRegistry.getBounds(note));
		
		assertEquals(new Rectangle(281, 216, 0, 0), NodeViewerRegistry.getBounds(p1));
		
		assertEquals(new Rectangle(474, 339, 0, 0), NodeViewerRegistry.getBounds(p2));
		
		Iterator<Edge> eIt = pDiagram.edges().iterator();
		
		ObjectReferenceEdge o1 = (ObjectReferenceEdge) eIt.next();
		ObjectReferenceEdge o2 = (ObjectReferenceEdge) eIt.next();
		ObjectReferenceEdge o3 = (ObjectReferenceEdge) eIt.next();
		NoteEdge ne1 = (NoteEdge) eIt.next();
		NoteEdge ne2 = (NoteEdge) eIt.next();
		ObjectCollaborationEdge cr1 = (ObjectCollaborationEdge) eIt.next();
		
		assertEquals(new Rectangle(osDependent(349, 339, 329), osDependent(179, 179, 179), 32, osDependent(35, 35, 32)), getBounds(o1));
		assertEquals(name, o1.getStart());
		assertEquals(type1, o1.getEnd());
		
		assertEquals(new Rectangle(osDependent(349, 339, 329), 212, osDependent(92, 102, 112), osDependent(164, 159, 159)), getBounds(o2));
		assertEquals(name, o2.getStart());
		assertEquals(blank, o2.getEnd());
		
		assertEquals(new Rectangle(osDependent(536, 535, 527), 208, osDependent(32, 33, 39), 82), getBounds(cr1));
		assertEquals(object2, cr1.getEnd());
		assertEquals("e1", cr1.getMiddleLabel().toString());
		assertEquals(blank, cr1.getStart());
		
		assertEquals(new Rectangle(osDependent(559, 559, 539), 329, osDependent(52, 52, 72), osDependent(109, 107, 107)), getBounds(o3));
		assertEquals(name4, o3.getStart());
		assertEquals(type3, o3.getEnd());
		
		assertEquals(new Rectangle(279, 214, 27, 116), getBounds(ne1));
		assertEquals(note, ne1.getStart());
		assertEquals(p1, ne1.getEnd());
		
		assertEquals(new Rectangle(338, 337, 136, 11), getBounds(ne2));
		assertEquals(note, ne2.getStart());
		assertEquals(p2, ne2.getEnd());
	}
}
