/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2019 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.views.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.SequenceDiagram;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.nodes.NodeViewerRegistry;

public class TestCallNodeView
{
	private ImplicitParameterNode aImplicitParameterNode1;
	private ImplicitParameterNode aImplicitParameterNode2;
	private SequenceDiagram aDiagram;
	private CallNode aDefaultCallNode1;
	private CallNode aDefaultCallNode2;
	private CallNode aCallNode1;
	private CallEdge aCallEdge1;
	private CallEdge aCallEdge2;
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setup()
	{
		aDiagram = new SequenceDiagram();
		aImplicitParameterNode1 = new ImplicitParameterNode();
		aImplicitParameterNode2 = new ImplicitParameterNode();
		aDefaultCallNode1 = new CallNode();
		aDefaultCallNode2 = new CallNode();
		aCallNode1 = new CallNode();
		aCallEdge1 = new CallEdge();
		aCallEdge2 = new CallEdge();
	}
	
	@Test
	public void testGetBoundsSecondCalleeOfCaller()
	{
		aImplicitParameterNode1.addChild(aDefaultCallNode1);
		aDefaultCallNode1.attach(aDiagram);
		aImplicitParameterNode2.addChild(aDefaultCallNode2);
		aDefaultCallNode2.attach(aDiagram);
		aImplicitParameterNode2.translate(200, 0);
		aDiagram.addRootNode(aImplicitParameterNode1);
		aDiagram.addRootNode(aImplicitParameterNode2);
		aCallEdge1.connect(aDefaultCallNode1, aDefaultCallNode2, aDiagram);
		aDiagram.addEdge(aCallEdge1);
		
		aImplicitParameterNode2.addChild(aCallNode1);
		aCallNode1.attach(aDiagram);
		aCallEdge2.connect(aDefaultCallNode1, aCallNode1, aDiagram);
		aDiagram.addEdge(aCallEdge2);
		
		assertEquals(new Rectangle(32, 80, 16, 120), NodeViewerRegistry.getBounds(aDefaultCallNode1));
		assertEquals(new Rectangle(232, 100, 16, 30), NodeViewerRegistry.getBounds(aDefaultCallNode2));
		assertEquals(new Rectangle(232, 150, 16, 30), NodeViewerRegistry.getBounds(aCallNode1));
	}	
}
