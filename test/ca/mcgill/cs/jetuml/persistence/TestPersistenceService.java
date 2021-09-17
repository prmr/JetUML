/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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
import static ca.mcgill.cs.jetuml.views.FontMetrics.DEFAULT_FONT_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.application.UserPreferences;
import ca.mcgill.cs.jetuml.application.UserPreferences.IntegerPreference;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.PropertyName;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ReturnEdge;
import ca.mcgill.cs.jetuml.diagram.edges.StateTransitionEdge;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseAssociationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseDependencyEdge;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseGeneralizationEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ActorNode;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.FinalStateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.diagram.nodes.InitialStateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.InterfaceNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;
import ca.mcgill.cs.jetuml.diagram.nodes.StateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.UseCaseNode;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.nodes.NodeViewerRegistry;

public class TestPersistenceService
{
	private static final String TEST_FILE_NAME = "testdata/tmp";
	private static int userDefinedFontSize;
	
	@BeforeAll
	public static void setupClass()
	{
		userDefinedFontSize = UserPreferences.instance().getInteger(UserPreferences.IntegerPreference.fontSize);
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, DEFAULT_FONT_SIZE);
		JavaFXLoader.load();
	}
	
	@AfterAll
	public static void restorePreferences()
	{
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, userDefinedFontSize);
	}
	
	@Test
	public void testClassDiagramContainment() throws Exception
	{
		Diagram diagram = PersistenceService.read(new File("testdata/testPersistenceService2.class.jet")).diagram();
		verifyClassDiagram2(diagram);
		
		File tmp = new File(TEST_FILE_NAME);
		PersistenceService.save(diagram, tmp);
		diagram = PersistenceService.read(tmp).diagram();
		verifyClassDiagram2(diagram);
		tmp.delete();
	}
	
	@Test
	public void testSequenceDiagram() throws Exception
	{
		Diagram diagram = PersistenceService.read(new File("testdata/testPersistenceService.sequence.jet")).diagram();
		verifySequenceDiagram(diagram);
		
		File tmp = new File(TEST_FILE_NAME);
		PersistenceService.save(diagram, tmp);
		diagram = PersistenceService.read(tmp).diagram();
		verifySequenceDiagram(diagram);
		tmp.delete();
	}
	
	@Test
	public void testStateDiagram() throws Exception
	{
		Diagram diagram = PersistenceService.read(new File("testdata/testPersistenceService.state.jet")).diagram();
		verifyStateDiagram(diagram);

		File tmp = new File(TEST_FILE_NAME);
		PersistenceService.save(diagram, tmp);
		diagram = PersistenceService.read(tmp).diagram();
		verifyStateDiagram(diagram);
		tmp.delete();
	}
	
	@Test
	public void testUseCaseDiagram() throws Exception
	{
		Diagram diagram = PersistenceService.read(new File("testdata/testPersistenceService.usecase.jet")).diagram();
		verifyUseCaseDiagram(diagram);

		File tmp = new File(TEST_FILE_NAME);
		PersistenceService.save(diagram, tmp);
		diagram = PersistenceService.read(tmp).diagram();
		verifyUseCaseDiagram(diagram);
		tmp.delete();
	}
	
	private void verifyUseCaseDiagram(Diagram pDiagram)
	{
		assertEquals(9, pDiagram.rootNodes().size());
		UseCaseNode u1 = (UseCaseNode) findRootNode(pDiagram, UseCaseNode.class, build(PropertyName.NAME, "Use case 1"));
		UseCaseNode u2 = (UseCaseNode) findRootNode(pDiagram, UseCaseNode.class, build(PropertyName.NAME, "Use case 2"));
		UseCaseNode u3 = (UseCaseNode) findRootNode(pDiagram, UseCaseNode.class, build(PropertyName.NAME, "Use case 3"));
		ActorNode a1 = (ActorNode) findRootNode(pDiagram, ActorNode.class, build(PropertyName.NAME, "Actor"));
		ActorNode a2 = (ActorNode) findRootNode(pDiagram, ActorNode.class, build(PropertyName.NAME, "Actor2"));
		NoteNode n1 = (NoteNode) findRootNode(pDiagram, NoteNode.class, build());
		PointNode p1 = (PointNode) findRootNode(pDiagram, PointNode.class, build());
		UseCaseNode u4 = (UseCaseNode) findRootNode(pDiagram, UseCaseNode.class, build(PropertyName.NAME, "Use case 4"));
		ActorNode a3 = (ActorNode) findRootNode(pDiagram, ActorNode.class, build(PropertyName.NAME, "Actor3"));
		
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
		
		assertEquals(10,  pDiagram.edges().size());
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
		assertTrue( cr3.properties().get(PropertyName.USE_CASE_DEPENDENCY_TYPE).get() == UseCaseDependencyEdge.Type.Extend);
		
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
		assertTrue( cr8.properties().get(PropertyName.USE_CASE_DEPENDENCY_TYPE).get() == UseCaseDependencyEdge.Type.Include);
		
		assertEquals(new Rectangle(568,147,82,osDependent(27, 26, 26)), getBounds(cr9));
		assertTrue( cr9.getStart() == u2 );
		assertTrue( cr9.getEnd() == u4 );
		assertTrue( cr9.properties().get(PropertyName.USE_CASE_DEPENDENCY_TYPE).get() == UseCaseDependencyEdge.Type.Extend);
		
		assertEquals(new Rectangle(542, 67, 114, 94), getBounds(cr10));
		assertTrue( cr10.getStart() == u1 );
		assertTrue( cr10.getEnd() == u4 );
 	}
	
	private void verifyClassDiagram2(Diagram pDiagram)
	{
		assertEquals(4, pDiagram.rootNodes().size());
		
		PackageNode p1 = (PackageNode) findRootNode(pDiagram, PackageNode.class, build(PropertyName.NAME, "p1"));
		PackageNode p2 = (PackageNode) findRootNode(pDiagram, PackageNode.class, build(PropertyName.NAME, "p2"));
		PackageNode p3 = (PackageNode) findRootNode(pDiagram, PackageNode.class, build(PropertyName.NAME, "p3"));
		
		assertEquals(new Rectangle(310, osDependent(229, 230, 230), 120, osDependent(101, 100, 100)), NodeViewerRegistry.getBounds(p1));
		assertEquals("p1", p1.getName().toString());
		
		List<Node> children = p1.getChildren();
		assertEquals(1, children.size());
		ClassNode c1 = (ClassNode) children.get(0);
		assertEquals(new Rectangle(320, 260, 100, 60), NodeViewerRegistry.getBounds(c1));
		assertEquals(p1, c1.getParent());
		assertEquals("C1", c1.getName().toString());

		assertEquals("p2", p2.getName().toString());
		assertEquals(new Rectangle(477, 130, 100, osDependent(81, 80, 80)), NodeViewerRegistry.getBounds(p2));
		children = p2.getChildren();
		assertEquals(0, children.size());

		assertEquals("p3", p3.getName().toString());
		assertEquals(new Rectangle(620, osDependent(268, 270, 270), 310, osDependent(142, 140, 140)), NodeViewerRegistry.getBounds(p3));
		children = p3.getChildren();
		assertEquals(1,children.size());
		PackageNode p4 = (PackageNode) children.get(0);
		assertEquals("p4", p4.getName().toString());
		assertEquals(new Rectangle(630, osDependent(299, 300, 300), 290, osDependent(101, 100, 100)), NodeViewerRegistry.getBounds(p4));
		
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

		assertEquals(3, pDiagram.edges().size());
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
	
	private void verifySequenceDiagram(Diagram pDiagram)
	{
		assertEquals(5, pDiagram.rootNodes().size());
		
		ImplicitParameterNode object1 = (ImplicitParameterNode) findRootNode(pDiagram, ImplicitParameterNode.class, build(PropertyName.NAME, "object1:Type1"));
		ImplicitParameterNode object2 = (ImplicitParameterNode) findRootNode(pDiagram, ImplicitParameterNode.class, build(PropertyName.NAME, ":Type2"));
		ImplicitParameterNode object3 = (ImplicitParameterNode) findRootNode(pDiagram, ImplicitParameterNode.class, build(PropertyName.NAME, "object3:"));
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
	
		assertEquals(6, pDiagram.edges().size());
		Iterator<Edge> eIterator = pDiagram.edges().iterator();
		
		CallEdge self = (CallEdge) eIterator.next(); 
		CallEdge signal = (CallEdge) eIterator.next(); 
		CallEdge call1 = (CallEdge) eIterator.next(); 
		ReturnEdge ret1 = (ReturnEdge) eIterator.next(); 
		ReturnEdge retC = (ReturnEdge) eIterator.next(); 
		NoteEdge nedge = (NoteEdge) eIterator.next(); 
		
		assertEquals(new Rectangle(osDependent(214,212, 216), 87, 86, osDependent(27, 26, 26)), getBounds(self));
		assertEquals(selfCall, self.getEnd());
		assertEquals("selfCall()", self.getMiddleLabel());
		assertEquals(init, self.getStart());
		assertFalse(self.isSignal());
		
		assertEquals(new Rectangle(osDependent(224,222, 226), osDependent(98, 99, 99), osDependent(179, 181, 177), 
				osDependent(27, 26, 26)), getBounds(signal));
		assertEquals(o2Call, signal.getEnd());
		assertEquals("signal", signal.getMiddleLabel());
		assertEquals(selfCall, signal.getStart());
		assertTrue(signal.isSignal());
		
		assertEquals(new Rectangle(417, osDependent(118, 119, 119), 206, osDependent(27, 26, 26)), getBounds(call1));
		assertEquals(o3Call, call1.getEnd());
		assertEquals("call1()", call1.getMiddleLabel());
		assertEquals(o2Call, call1.getStart());
		assertFalse(call1.isSignal());
		
		assertEquals(new Rectangle(416, 157, 207, osDependent(27,26, 23)), getBounds(ret1));
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
		assertEquals(7, pDiagram.rootNodes().size());
		
		StateNode s1 = (StateNode) findRootNode(pDiagram, StateNode.class, build(PropertyName.NAME, "S1"));
		StateNode s2 = (StateNode) findRootNode(pDiagram, StateNode.class, build(PropertyName.NAME, "S2"));
		StateNode s3 = (StateNode) findRootNode(pDiagram, StateNode.class, build(PropertyName.NAME, "S3"));
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
		
		assertEquals(7,  pDiagram.edges().size());
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
}
