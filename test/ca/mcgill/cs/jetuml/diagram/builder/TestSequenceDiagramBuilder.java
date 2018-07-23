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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.SequenceDiagram;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;

public class TestSequenceDiagramBuilder
{
	private SequenceDiagram aDiagram;
	private SequenceDiagramBuilder aBuilder;
	private ImplicitParameterNode aImplicitParameterNode1;
	private ImplicitParameterNode aImplicitParameterNode2;
	private CallNode aDefaultCallNode1;
	private CallNode aDefaultCallNode2;
	private CallNode aCallNode1;
	private CallEdge aCallEdge1;
	private CallEdge aCallEdge2;
	
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
		aDiagram = new SequenceDiagram();
		aBuilder = new SequenceDiagramBuilder(aDiagram);
		aImplicitParameterNode1 = new ImplicitParameterNode();
		aImplicitParameterNode2 = new ImplicitParameterNode();
		aDefaultCallNode1 = new CallNode();
		aDefaultCallNode2 = new CallNode();
		aCallNode1 = new CallNode();
		aCallEdge1 = new CallEdge();
		aCallEdge2 = new CallEdge();
	}
	
	/*
	 * Add without the default call node. 
	 */
	@Test
	public void testcreateAddNodeOperationOneImplicitParameterNode()
	{
		DiagramOperation operation = aBuilder.createAddNodeOperation(aImplicitParameterNode1, new Point(10,10));
		operation.execute();
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(new Point(10,10), aImplicitParameterNode1.position());
		
		operation.undo();
		assertEquals(0, aDiagram.getRootNodes().size());
	}
	
	@Test
	public void testcreateAddNodeOperationImplicitParameterNodeWithDefaultCallNode()
	{
		aImplicitParameterNode1.addChild(aDefaultCallNode1);
		DiagramOperation operation = aBuilder.createAddNodeOperation(aImplicitParameterNode1, new Point(10,10));
		operation.execute();
		assertEquals(1, aDiagram.getRootNodes().size());
		assertEquals(new Point(10,10), aImplicitParameterNode1.position());
		assertEquals(1, aImplicitParameterNode1.getChildren().size());
		
		operation.undo();
		assertEquals(0, aDiagram.getRootNodes().size());
	}
	
	@Test
	public void testcreateAddEdgeOperationCallNodeCallNode1()
	{
		aImplicitParameterNode1.addChild(aDefaultCallNode1);
		aImplicitParameterNode2.addChild(aDefaultCallNode2);
		aImplicitParameterNode2.translate(200, 0);
		aDiagram.addRootNode(aImplicitParameterNode1);
		aDiagram.addRootNode(aImplicitParameterNode2);
		
		DiagramOperation operation = aBuilder.createAddEdgeOperation(aCallEdge1, new Point(35, 85), new Point(235, 85));
		operation.execute();
		assertSame(aDefaultCallNode1, aCallEdge1.getStart());
		assertSame(aDefaultCallNode2, aCallEdge1.getEnd());
		assertEquals(1, aDiagram.getEdges().size());
		
		operation.undo();
		assertEquals(0, aDiagram.getEdges().size());
	}
	
	@Test
	public void testcreateAddNodeOperationSecondCallNode()
	{
		aImplicitParameterNode1.addChild(aDefaultCallNode1);
		aImplicitParameterNode2.addChild(aDefaultCallNode2);
		aImplicitParameterNode2.translate(200, 0);
		aDiagram.addRootNode(aImplicitParameterNode1);
		aDiagram.addRootNode(aImplicitParameterNode2);
		aCallEdge1.connect(aDefaultCallNode1, aDefaultCallNode2, aDiagram);
		aDiagram.addEdge(aCallEdge1);
		
		DiagramOperation operation = aBuilder.createAddNodeOperation(aCallNode1, new Point(30, 135));
		operation.execute();
		assertEquals(2, aDiagram.getRootNodes().size());
		assertEquals(2, aImplicitParameterNode1.getChildren().size());
		assertSame(aDefaultCallNode1, aImplicitParameterNode1.getChildren().get(0));
		assertSame(aCallNode1, aImplicitParameterNode1.getChildren().get(1));
	}
	
	@Test
	public void testcreateAddEdgeOperationFromImplicitParameterThatAlreadyHasACallee()
	{
		aImplicitParameterNode1.addChild(aDefaultCallNode1);
		aImplicitParameterNode2.addChild(aDefaultCallNode2);
		aImplicitParameterNode2.translate(200, 0);
		aDiagram.addRootNode(aImplicitParameterNode1);
		aDiagram.addRootNode(aImplicitParameterNode2);
		aCallEdge1.connect(aDefaultCallNode1, aDefaultCallNode2, aDiagram);
		aDiagram.addEdge(aCallEdge1);
		
		aImplicitParameterNode2.addChild(aCallNode1);
		assertEquals(new Rectangle(232,140,16,30), aCallNode1.view().getBounds());
		
		DiagramOperation operation = aBuilder.createAddEdgeOperation(aCallEdge2, new Point(35,85), new Point(235, 145));
		operation.execute();
		assertEquals(2, aDiagram.getEdges().size());
		assertSame(aDefaultCallNode1, aCallEdge2.getStart());
		assertSame(aCallNode1, aCallEdge2.getEnd());
	}
}
