/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2019 by the contributors of the JetUML project.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.SequenceDiagram;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.geom.Point;

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
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
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
	}
	
	private int numberOfRootNodes()
	{
		int sum = 0;
		for( @SuppressWarnings("unused") Node node : aDiagram.rootNodes() )
		{
			sum++;
		}
		return sum;
	}
	
	/*
	 * Add without the default call node. 
	 */
	@Test
	public void testcreateAddNodeOperationOneImplicitParameterNode()
	{
		DiagramOperation operation = aBuilder.createAddNodeOperation(aImplicitParameterNode1, new Point(10,10));
		operation.execute();
		assertEquals(1, numberOfRootNodes());
		assertEquals(new Point(10,10), aImplicitParameterNode1.position());
		
		operation.undo();
		assertEquals(0, numberOfRootNodes());
	}
	
	@Test
	public void testcreateAddNodeOperationImplicitParameterNodeWithDefaultCallNode()
	{
		aImplicitParameterNode1.addChild(aDefaultCallNode1);
		DiagramOperation operation = aBuilder.createAddNodeOperation(aImplicitParameterNode1, new Point(10,10));
		operation.execute();
		assertEquals(1, numberOfRootNodes());
		assertEquals(new Point(10,10), aImplicitParameterNode1.position());
		assertEquals(1, aImplicitParameterNode1.getChildren().size());
		
		operation.undo();
		assertEquals(0, numberOfRootNodes());
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
		assertEquals(2, numberOfRootNodes());
		assertEquals(2, aImplicitParameterNode1.getChildren().size());
		assertSame(aDefaultCallNode1, aImplicitParameterNode1.getChildren().get(0));
		assertSame(aCallNode1, aImplicitParameterNode1.getChildren().get(1));
	}
}
