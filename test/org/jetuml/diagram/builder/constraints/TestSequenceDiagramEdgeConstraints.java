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

package org.jetuml.diagram.builder.constraints;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.ReturnEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.geom.Point;
import org.jetuml.rendering.DiagramRenderer;
import org.junit.jupiter.api.Test;

public class TestSequenceDiagramEdgeConstraints
{
	private Diagram aDiagram = new Diagram(DiagramType.SEQUENCE);
	private DiagramRenderer aRenderer = DiagramType.newRendererInstanceFor(aDiagram);
	private ImplicitParameterNode aParameter1 = new ImplicitParameterNode();
	private ImplicitParameterNode aParameter2  = new ImplicitParameterNode();
	private CallNode aCallNode1 = new CallNode();
	private CallNode aCallNode2 = new CallNode();
	private CallNode aCallNode3 = new CallNode();
	private CallEdge aCallEdge = new CallEdge();
	private ReturnEdge aReturnEdge = new ReturnEdge();
	private Point aPoint = new Point(0,0);
	
	private void createDiagram()
	{
		aDiagram.addRootNode(aParameter1);
		aDiagram.addRootNode(aParameter2);
		aParameter1.addChild(aCallNode1);
		aParameter1.addChild(aCallNode3);
		aParameter2.addChild(aCallNode2);
	}
	
	@Test
	void testCanCreateConstructor_StartNodeIncorrect()
	{
		assertFalse(SequenceDiagramEdgeConstraints.canCreateConstructor(new NoteNode(), aCallNode1, aPoint, aRenderer));
	}
	
	@Test
	void testCanCreateConstructor_EndNodeNotAnImplicitParameterNode()
	{
		assertFalse(SequenceDiagramEdgeConstraints.canCreateConstructor(aCallNode1, aCallNode2, aPoint, aRenderer));
	}
	
	@Test
	void testCanCreateConstructor_EndNodeDoesNotContainPoint()
	{
		assertFalse(SequenceDiagramEdgeConstraints.canCreateConstructor(aParameter1, aParameter2, new Point(1000,1000), aRenderer));
	}
	
	@Test
	void testCanCreateConstructor_EndNodeContainsPointButNotChildless()
	{
		createDiagram();
		assertFalse(SequenceDiagramEdgeConstraints.canCreateConstructor(aParameter1, aParameter2, new Point(10,10), aRenderer));
	}
	
	@Test
	void testCanCreateConstructor_EndNodeContainsPointButAndChildless()
	{
		assertTrue(SequenceDiagramEdgeConstraints.canCreateConstructor(aParameter1, aParameter2, new Point(10,10), aRenderer));
	}
	
	@Test
	void testNoEdgeFromParameterTopNotParameterNode()
	{
		createDiagram();
		assertTrue(SequenceDiagramEdgeConstraints.noEdgesFromParameterTop().satisfied(aCallEdge, aCallNode1, aCallNode1, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testNoEdgeFromParameterTopParameterFalse()
	{
		createDiagram();
		assertFalse(SequenceDiagramEdgeConstraints.noEdgesFromParameterTop().satisfied(aCallEdge, aParameter1, aParameter1,new Point(5,5),aPoint, aRenderer));
	}
	
	@Test
	void testNoEdgeFromParameterTopParameterTrue()
	{
		createDiagram();
		assertTrue(SequenceDiagramEdgeConstraints.noEdgesFromParameterTop().satisfied(aCallEdge, aParameter1, aParameter1, new Point(40,65), aPoint, aRenderer));
	}
	
	@Test
	void testreturnEdgeNotReturnEdge()
	{
		createDiagram();
		assertTrue(SequenceDiagramEdgeConstraints.returnEdge().satisfied(aCallEdge, aCallNode1, aCallNode2, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testreturnEdgeIncompatibleStart()
	{
		createDiagram();
		assertFalse(SequenceDiagramEdgeConstraints.returnEdge().satisfied(aReturnEdge, aParameter1, aCallNode2, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testreturnEdgeIncompatibleEnd()
	{
		createDiagram();
		assertFalse(SequenceDiagramEdgeConstraints.returnEdge().satisfied(aReturnEdge, aCallNode1, aParameter2, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testreturnEdgeEndNoCaller()
	{
		createDiagram();
		assertFalse(SequenceDiagramEdgeConstraints.returnEdge().satisfied(aReturnEdge, aCallNode1, aCallNode2, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testreturnEdgeEndNotCaller()
	{
		createDiagram();
		aCallEdge.connect(aCallNode1, aCallNode2, aDiagram);
		aDiagram.addEdge(aCallEdge);
		assertFalse(SequenceDiagramEdgeConstraints.returnEdge().satisfied(aReturnEdge, aCallNode2, aCallNode3, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testreturnEdgeSelfCaller()
	{
		createDiagram();
		aCallEdge.connect(aCallNode1, aCallNode3, aDiagram);
		aDiagram.addEdge(aCallEdge);
		assertFalse(SequenceDiagramEdgeConstraints.returnEdge().satisfied(aReturnEdge, aCallNode3, aCallNode1, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testreturnEdgeValid()
	{
		createDiagram();
		aCallEdge.connect(aCallNode1, aCallNode2, aDiagram);
		aDiagram.addEdge(aCallEdge);
		assertTrue(SequenceDiagramEdgeConstraints.returnEdge().satisfied(aReturnEdge, aCallNode2, aCallNode1, aPoint, aPoint, aRenderer));
	}	
	
	@Test
	void testCallEdgeEndNotCallEdge()
	{
		createDiagram();
		assertTrue(SequenceDiagramEdgeConstraints.callEdgeEnd().satisfied(aReturnEdge, aCallNode2, aCallNode1, aPoint, new Point(10,10), aRenderer));
	}	
	
	@Test
	void testCallEdgeEndEndNotParameter()
	{
		createDiagram();
		assertTrue(SequenceDiagramEdgeConstraints.callEdgeEnd().satisfied(aCallEdge, aCallNode2, aCallNode1, aPoint, new Point(10,10), aRenderer));
	}	
	
	@Test
	void testCallEdgeEndEndOnLifeLine()
	{
		createDiagram();
		assertTrue(SequenceDiagramEdgeConstraints.callEdgeEnd().satisfied(aCallEdge, aParameter2, aCallNode1, aPoint, new Point(0,85), aRenderer));
	}	
	
	@Test
	void testCallEdgeEndEndOnTopRectangle()
	{
		createDiagram();
		assertTrue(SequenceDiagramEdgeConstraints.callEdgeEnd().satisfied(aCallEdge, aParameter2, aCallNode1, aPoint, new Point(0,5), aRenderer));
	}	
	
	@Test
	void testSingleEntryPointNotACallEdge()
	{
		createDiagram();
		assertTrue(SequenceDiagramEdgeConstraints.singleEntryPoint().satisfied(aReturnEdge, aParameter1, aParameter1, aPoint, aPoint, aRenderer));
	}	
	
	@Test
	void testSingleEntryPointNotStartingOnAParameterNode()
	{
		createDiagram();
		assertTrue(SequenceDiagramEdgeConstraints.singleEntryPoint().satisfied(aCallEdge, aCallNode1, aCallNode1, aPoint, aPoint, aRenderer));
	}	
	
	@Test
	void testSingleEntryPointStartingOnParameterNodeNotSatisfied()
	{
		createDiagram();
		assertFalse(SequenceDiagramEdgeConstraints.singleEntryPoint().satisfied(aCallEdge, aParameter1, aParameter1, aPoint, aPoint, aRenderer));
	}	
	
	@Test
	void testSingleEntryPointStartingOnParameterNodeSatisfied()
	{
		aDiagram.addRootNode(aParameter1);
		assertTrue(SequenceDiagramEdgeConstraints.singleEntryPoint().satisfied(aCallEdge, aParameter1, aParameter1, aPoint, aPoint, aRenderer));
	}	
}