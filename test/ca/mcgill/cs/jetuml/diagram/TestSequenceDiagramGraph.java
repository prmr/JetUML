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

package ca.mcgill.cs.jetuml.diagram;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.SequenceDiagram;
import ca.mcgill.cs.jetuml.diagram.builder.SequenceDiagramBuilder;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;

public class TestSequenceDiagramGraph
{
	 private SequenceDiagram aDiagram;
	 private SequenceDiagramBuilder aBuilder;
	 
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
	 public void setup()
	 {
		 aDiagram = new SequenceDiagram();
		 aBuilder = new SequenceDiagramBuilder(aDiagram);
	 }
	 
	 @Test
	 public void testDeepFindNodeNoChild()
	 {
		 ImplicitParameterNode param = new ImplicitParameterNode();
		 aBuilder.createAddNodeOperation(param, new Point(20,0)).execute();
		 CallNode node = new CallNode();
		 aBuilder.createAddNodeOperation(node, new Point(40, 90)).execute();
		 aDiagram.layout();
		 
		 // Point outside the bounds
		 assertNull(aDiagram.deepFindNode(node, new Point(50, 0)));
		 assertNull(aDiagram.deepFindNode(node, new Point(60, 0)));
		 
		 // Point inside the bounds
		 assertTrue(aDiagram.deepFindNode(node, new Point(60, 100)) == node);
	 }
	 
	 @Test
	 public void testDeepFindNodeOneChild()
	 {
		 ImplicitParameterNode param = new ImplicitParameterNode();
		 aBuilder.createAddNodeOperation(param, new Point(20,0)).execute();
		 CallNode node = new CallNode();
		 aBuilder.createAddNodeOperation(node, new Point(40, 90)).execute();
		 aDiagram.layout();
		 CallNode callee = new CallNode();
		 param.addChild(callee,new Point(60, 100));
		 Edge callEdge = new CallEdge();
		 aDiagram.restoreEdge(callEdge, node, callee);
		 aDiagram.layout();
	 
		 // Point outside the bounds
		 assertNull(aDiagram.deepFindNode(node, new Point(50, 0)));
		 assertNull(aDiagram.deepFindNode(node, new Point(60, 0)));
		 
		// Point inside both the caller and the callee
		assertTrue(aDiagram.deepFindNode(node, new Point(64, 110)) == callee);
	 }

	@Test
	public void testDeepFindNodeTwoChildren()
	{
		/*
		 * Here we have two implicit parameters (param and param2).
		 * The first implicit parameter has a call edge (callEdge) from 
		 * its own call node (node) to call node on the second implicit 
		 * parameter (node2), and a call edge (callEdge2) from its own call
		 * node (node) to a new call node also on itself, that is, a self-call
		 * (node3).
		 * 
		 * This exact setup is illustrated at https://github.com/prmr/JetUML/pull/184
		 */
	
		ImplicitParameterNode param = new ImplicitParameterNode();
		aBuilder.createAddNodeOperation(param, new Point(118, 0)).execute();

		CallNode node = new CallNode();
		aBuilder.createAddNodeOperation(node, new Point(152, 70)).execute();
		
		// node is the first call node of param
		assertEquals(1, param.getChildren().size());

		ImplicitParameterNode param2 = new ImplicitParameterNode();
		aBuilder.createAddNodeOperation(param2, new Point(347, 0)).execute();

		CallNode node2 = new CallNode();
		aBuilder.createAddNodeOperation(node2, new Point(382, 80)).execute();
		
		// node2 is the first call node of param2
		assertEquals(1, param2.getChildren().size());

		Edge callEdge = new CallEdge();
		aDiagram.restoreEdge(callEdge, node, node2);

		CallNode node3 = new CallNode();
		aBuilder.createAddNodeOperation(node3, new Point(160, 125)).execute();
		
		// node 3 is nested in node
		assertEquals(2, param.getChildren().size());
		assertSame(node3, param.getChildren().get(1));

		Edge callEdge2 = new CallEdge();
		aDiagram.restoreEdge(callEdge2, node, node3);

		// Point outside the bounds
		assertNull(aDiagram.deepFindNode(node, new Point(171, 71)));
		
		// Point inside the bounds of the caller but not the self-call callee
		assertTrue(aDiagram.deepFindNode(node, new Point(157, 96)) == node);
		
		// Point inside the bounds of the call node on the second implicit parameter
		assertTrue(aDiagram.deepFindNode(node, new Point(386, 96)) == node2);

		// Point inside both caller and self-call callee
		assertEquals( new Rectangle(150,80, 16, 50),node.view().getBounds());
		assertEquals( new Rectangle(158,90, 16, 30),node3.view().getBounds());
		Point point = new Point(160, 95);
		assertSame(node3, aDiagram.deepFindNode(node, new Point(160, 95)));
	}
}
