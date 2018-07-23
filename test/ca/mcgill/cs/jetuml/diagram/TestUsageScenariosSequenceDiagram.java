/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.diagram;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.jetuml.diagram.builder.SequenceDiagramBuilder;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ReturnEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.geom.Point;

public class TestUsageScenariosSequenceDiagram extends AbstractTestUsageScenarios
{
	private ImplicitParameterNode aParameterNode1;
	private ImplicitParameterNode aParameterNode2;
	private CallNode aCallNode1;
	private CallNode aCallNode2;
	private CallEdge aCallEdge1;
	private CallEdge aCallEdge2;
	private ReturnEdge aReturnEdge;
	
	@Before
	public void setup()
	{
		super.setup();
		aDiagram = new SequenceDiagram();
		aBuilder = new SequenceDiagramBuilder(aDiagram);
		aParameterNode1 = new ImplicitParameterNode();
		aParameterNode2 = new ImplicitParameterNode();
		aCallNode1 = new CallNode();
		aCallNode2 = new CallNode();
		aCallEdge1 = new CallEdge();
		aCallEdge2 = new CallEdge();
		aReturnEdge = new ReturnEdge();
	}
	
	@Test
	public void testCreateAndLinkParameterNode()
	{
		setProperty(aParameterNode1.properties().get("name"), "client");
		setProperty(aParameterNode2.properties().get("name"), "platform");

		addNode(aParameterNode1, new Point(5, 0));
		addNode(aParameterNode2, new Point(25, 0));
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals("client", aParameterNode1.getName());
		assertEquals("platform", aParameterNode2.getName());
		
		assertFalse(aBuilder.canAdd(aCallEdge1, new Point(7, 0), new Point(26, 0))); 
		assertFalse(aBuilder.canAdd(aReturnEdge, new Point(7, 0), new Point(26, 0)));
		assertFalse(aBuilder.canAdd(aNoteEdge, new Point(7, 0), new Point(26, 0)));
	}
	
	@Test
	public void testCreateCallNodeAndLinkParameterNode()
	{
		addNode(aParameterNode1, new Point(5, 0));
		addNode(aParameterNode2, new Point(125, 0));
		addNode(aCallNode1, new Point(7, 75));
		
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, aParameterNode1.getChildren().size());

		assertFalse(aBuilder.canAdd(aReturnEdge, new Point(7, 75), new Point(26, 0)));
		assertFalse(aBuilder.canAdd(aNoteEdge, new Point(7, 75), new Point(26, 0)));
		
		addEdge(aCallEdge1, new Point(43, 85), new Point(130, 85));
		assertEquals(1, aDiagram.numberOfEdges());
	}
	
	@Test
	public void testLinkCallNodeToLifeLine()
	{
		addNode(aParameterNode1, new Point(5, 0));
		addNode(aParameterNode2, new Point(125, 0));
		addNode(aCallNode1, new Point(11, 75));
		
		assertSame(aCallNode1, aParameterNode1.getChildren().get(0));
		
		addEdge(aCallEdge1, new Point(43, 85), new Point(132,75));
		
		assertEquals(1, aDiagram.numberOfEdges());
		assertEquals(1, aParameterNode2.getChildren().size());
	}
	
	
	/**
	 * Testing adding more edges to the diagram.
	 */
	@Test
	public void testAddMoreEdges()
	{
		ImplicitParameterNode newParaNode = new ImplicitParameterNode();
		addNode(aParameterNode1, new Point(10, 0));
		addNode(aParameterNode2, new Point(110, 0));
		addNode(newParaNode, new Point(210, 0));
		addNode(aCallNode1, new Point(15, 85));
		assertEquals(1, aParameterNode1.getChildren().size());
		addEdge(aCallEdge1, new Point(45, 85), new Point(115,75));
		assertEquals(1, aDiagram.numberOfEdges());
		
		ReturnEdge returnEdge1 = new ReturnEdge();
		addEdge(returnEdge1, new Point(145,90), new Point(45, 90));
		assertEquals(2, aDiagram.numberOfEdges());
		
		// call edge from first CallNode to third ParameterNode life line
		addEdge(new CallEdge(), new Point(45, 85), new Point(210,75));
		assertEquals(3, aDiagram.numberOfEdges());
	}
	
	@Test
	public void testNoteNode()
	{
		addNode(aParameterNode1, new Point(10, 0));
		addNode(aParameterNode2, new Point(110, 0));
		addNode(aCallNode1, new Point(15, 75)); 
		addNode(aCallNode2, new Point(115, 75)); 
		addEdge(aCallEdge1, new Point(45, 85), new Point(145,85));
		
		NoteNode noteNode = new NoteNode();
		NoteEdge noteEdge1 = new NoteEdge();
		NoteEdge noteEdge2 = new NoteEdge();
		NoteEdge noteEdge3 = new NoteEdge();
		NoteEdge noteEdge4 = new NoteEdge();
		NoteEdge noteEdge5 = new NoteEdge();
		addNode(noteNode, new Point(55, 55));
		addEdge(noteEdge1, new Point(60, 60), new Point(87,65));
		addEdge(noteEdge2, new Point(62, 68), new Point(47,75));
		addEdge(noteEdge3, new Point(63, 69), new Point(47,35));
		addEdge(noteEdge4, new Point(64, 70), new Point(17,5));
		addEdge(noteEdge5, new Point(65, 60), new Point(67,265));
		
		assertEquals(6, aDiagram.numberOfEdges());
		assertEquals(8, aDiagram.getRootNodes().size());
		
		// from ParameterNode to NoteNode
		assertFalse(aBuilder.canAdd(new NoteEdge(), new Point(10, 10), new Point(62, 68)));
		// from CallNode to NoteNode 
		assertFalse(aBuilder.canAdd(new NoteEdge(), new Point(10, 10), new Point(62, 68)));
		assertEquals(8, aDiagram.getRootNodes().size());
	}
	
	@Test
	public void testIndividualNodeMovement()
	{
		addNode(aParameterNode1, new Point(10, 0));
		addNode(aParameterNode2, new Point(110, 0));
		addNode(aCallNode1, new Point(15, 75));
		addNode(aCallNode2, new Point(115, 75));
		addEdge(aCallEdge1, new Point(42, 85), new Point(142,85));

		aParameterNode1.translate(5, 15);
		assertEquals(new Point(15, 15), aParameterNode1.position());
		aParameterNode1.translate(25, 0);
		assertEquals(new Point(40, 15), aParameterNode1.position());

		aParameterNode2.translate(105, 25);
		assertEquals(new Point(215, 25), aParameterNode2.position());
		aParameterNode2.translate(0, 15);
		assertEquals(new Point(215, 40), aParameterNode2.position());
	}
	
	@Test
	public void testDeleteSingleParameterNode()
	{
		addNode(aParameterNode1, new Point(10, 0));
		addNode(aCallNode1, new Point(15, 65));
		select(aParameterNode1);
		deleteSelected();
		assertEquals(0, aDiagram.getRootNodes().size());
		undo();
		assertEquals(1, aDiagram.getRootNodes().size());
	}
	
	@Test
	public void testDeleteSingleCallNode()
	{
		addNode(aParameterNode1, new Point(10, 0));
		addNode(aCallNode1, new Point(15, 75));

		select(aCallNode1);
		deleteSelected();
		
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(0, aParameterNode1.getChildren().size());
		
		undo();
		assertEquals(1, aParameterNode1.getChildren().size());
	}
	
	@Test
	public void testDeleteParameterNodeInCallSequence()
	{
		ImplicitParameterNode newParameterNode = new ImplicitParameterNode();
		addNode(aParameterNode1, new Point(10, 0));
		addNode(aParameterNode2, new Point(110, 0));
		addNode(newParameterNode, new Point(210, 0));
		addNode(aCallNode1, new Point(15, 75));
		addEdge(aCallEdge1, new Point(45, 85), new Point(115,85));
		ReturnEdge returnEdge1 = new ReturnEdge();
		addEdge(returnEdge1, new Point(145,90), new Point(45, 90));		
		CallEdge callEdge2 = new CallEdge();
		addEdge(callEdge2, new Point(45, 85), new Point(210,75));
		
		select(aParameterNode1);
		deleteSelected();
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, newParameterNode.getChildren().size());
		assertEquals(1, aParameterNode2.getChildren().size()); 
		assertEquals(0, aDiagram.numberOfEdges());
		
		undo();
		assertEquals(3, aDiagram.getRootNodes().size());
		assertEquals(1, newParameterNode.getChildren().size());
		assertEquals(1, aParameterNode2.getChildren().size()); 
		assertEquals(3, aDiagram.numberOfEdges());
	}
	
	@Test
	public void testDeleteUndoParameterWithTwoCallNodes()
	{
		ImplicitParameterNode newParameterNode1 = new ImplicitParameterNode();
		ImplicitParameterNode newParameterNode2 = new ImplicitParameterNode();
		newParameterNode2.translate(100,0);
		aDiagram.addRootNode(newParameterNode1);
		aDiagram.addRootNode(newParameterNode2);
		
		CallNode caller = new CallNode();
		newParameterNode1.addChild(caller);
		
		CallNode callee1 = new CallNode();
		newParameterNode2.addChild(callee1);
				
		CallNode callee2 = new CallNode();
		newParameterNode2.addChild(callee2);
		
		CallEdge callEdge1 = new CallEdge();
		callEdge1.connect(caller, callee1, aDiagram);
		aDiagram.addEdge(callEdge1);
		
		CallEdge callEdge2 = new CallEdge();
		callEdge2.connect(caller, callee2, aDiagram);
		aDiagram.addEdge(callEdge2);
				
		Edge[] edges = aDiagram.getEdges(caller).toArray(new Edge[aDiagram.getEdges(caller).size()]);
		assertEquals(2, edges.length);
		
		select(caller);
		deleteSelected();
		assertEquals(0, newParameterNode1.getChildren().size());
		
		undo();
		assertEquals(1, newParameterNode1.getChildren().size());
		edges = aDiagram.getEdges(caller).toArray(new Edge[aDiagram.getEdges(caller).size()]);
		assertEquals(2, edges.length);
	}
	
	@Test
	public void testDeleteMiddleCallNode()
	{
		ImplicitParameterNode newParameterNode = new ImplicitParameterNode();
		CallNode middleCallNode = new CallNode();
		aParameterNode1.translate(10, 0);
		aDiagram.addRootNode(aParameterNode1);
		aParameterNode2.translate(110, 0);
		aDiagram.addRootNode(aParameterNode2);
		newParameterNode.translate(210, 0);
		aDiagram.addRootNode(newParameterNode);
		aParameterNode1.addChild(aCallNode1);
		aParameterNode2.addChild(middleCallNode);
		CallNode end = new CallNode();
		newParameterNode.addChild(end);
		
		aCallEdge1.connect(aCallNode1, middleCallNode, aDiagram);
		aDiagram.addEdge(aCallEdge1);
		
		CallEdge edge = new CallEdge();
		edge.connect(middleCallNode, end, aDiagram);
		aDiagram.addEdge(edge);
		
		ReturnEdge redge = new ReturnEdge();
		redge.connect(middleCallNode, aCallNode1, aDiagram);
		aDiagram.addEdge(redge);
		
		select(middleCallNode);
		deleteSelected();
		
		assertEquals(1, aParameterNode1.getChildren().size()); 
		assertEquals(0, aParameterNode2.getChildren().size()); 
		assertEquals(1, newParameterNode.getChildren().size()); 
		assertEquals(0, aDiagram.numberOfEdges());
		
		undo();
		assertEquals(1, aParameterNode1.getChildren().size()); 
		assertEquals(1, aParameterNode2.getChildren().size()); 
		assertEquals(1, newParameterNode.getChildren().size()); 
		assertEquals(3, aDiagram.numberOfEdges());
	}
	
	@Test
	public void testDeleteReturnEdge()
	{
		CallNode middleCallNode = new CallNode();
		ReturnEdge returnEdge = new ReturnEdge();
		aParameterNode1.translate(10, 0);
		aDiagram.addRootNode(aParameterNode1);
		aParameterNode2.translate(110, 0);
		aDiagram.addRootNode(aParameterNode2);
		aParameterNode1.addChild(aCallNode1);
		aParameterNode2.addChild(middleCallNode);
		CallEdge callEdge = new CallEdge();
		callEdge.connect(aCallNode1, middleCallNode, aDiagram);
		aDiagram.addEdge(callEdge);
		returnEdge.connect(middleCallNode, aCallNode1, aDiagram);
		aDiagram.addEdge(returnEdge);
		
		select(returnEdge);
		deleteSelected();

		assertEquals(1, aParameterNode1.getChildren().size()); 
		assertEquals(1, aParameterNode2.getChildren().size()); 
		assertEquals(1, aDiagram.numberOfEdges());
		
		undo();
		assertEquals(2, aDiagram.numberOfEdges());
	}
	
	@Test
	public void testCopyPasteParameterNode()
	{
		aDiagram.addRootNode(aParameterNode1);
		
		select(aParameterNode1);
		copy();
		paste();

		assertEquals(2, aDiagram.getRootNodes().size());
	}
	
	@Test
	public void testCutPasteParameterNode()
	{
		aDiagram.addRootNode(aParameterNode1);
		
		select(aParameterNode1);
		
		cut();
		assertEquals(0, aDiagram.getRootNodes().size());

		paste();
		
		assertEquals(1, aDiagram.getRootNodes().size());
	}
	
	@Test
	public void testCopyPasteParameterNodeWithCallNode()
	{
		aDiagram.addRootNode(aParameterNode1);
		aParameterNode1.addChild(aCallNode1);
		
		select(aParameterNode1);
		
		copy();
		paste();
		
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(1, (((ImplicitParameterNode)(aDiagram.getRootNodes().toArray()[1])).getChildren().size()));
	}
	
	@Test
	public void testCopyPasteSequenceDiagram()
	{
		aDiagram.addRootNode(aParameterNode1);
		aParameterNode2.translate(110, 0);
		aDiagram.addRootNode(aParameterNode2);
		
		ImplicitParameterNode newParameterNode = new ImplicitParameterNode();
		newParameterNode.translate(200, 0);
		aDiagram.addRootNode(newParameterNode);
		
		aParameterNode1.addChild(aCallNode1);
		CallNode middleCallNode = new CallNode();
		aParameterNode2.addChild(middleCallNode);
		newParameterNode.addChild(aCallNode2);
		
		aCallEdge1.connect(aCallNode1, middleCallNode, aDiagram);
		aDiagram.addEdge(aCallEdge1);
		
		aCallEdge2.connect(middleCallNode, aCallNode2, aDiagram);
		aDiagram.addEdge(aCallEdge2);
		
		aReturnEdge.connect(middleCallNode, aCallNode1, aDiagram);
		aDiagram.addEdge(aReturnEdge);
		
		selectAll();
		copy();
		paste();
	
		assertEquals(6, aDiagram.getRootNodes().size());
		assertEquals(6, aDiagram.numberOfEdges());
	}
}
