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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
		aDiagram.layout();

		CallNode node = new CallNode();
		aBuilder.createAddNodeOperation(node, new Point(152, 70)).execute();
		aDiagram.layout();		

		ImplicitParameterNode param2 = new ImplicitParameterNode();
		aBuilder.createAddNodeOperation(param2, new Point(347, 0)).execute();
		aDiagram.layout();

		CallNode node2 = new CallNode();
		aBuilder.createAddNodeOperation(node2, new Point(382, 80)).execute();
		aDiagram.layout();

		Edge callEdge = new CallEdge();
		aDiagram.restoreEdge(callEdge, node, node2);
		aDiagram.layout();

		CallNode node3 = new CallNode();
		aBuilder.createAddNodeOperation(node3, new Point(160, 125)).execute();
		aDiagram.layout();		

		Edge callEdge2 = new CallEdge();
		aDiagram.restoreEdge(callEdge2, node, node3);
		aDiagram.layout();

		// Point outside the bounds
		assertNull(aDiagram.deepFindNode(node, new Point(171, 71)));
		
		// Point inside the bounds of the caller but not the self-call callee
		assertTrue(aDiagram.deepFindNode(node, new Point(157, 96)) == node);
		
		// Point inside the bounds of the call node on the second implicit parameter
		assertTrue(aDiagram.deepFindNode(node, new Point(386, 96)) == node2);

		// Point inside both caller and self-call callee
		assertTrue(aDiagram.deepFindNode(node, new Point(161, 139)) == node3);
	}
	
	@Test
	public void testDeepFindNodeCreateNode()
	{
		ImplicitParameterNode param = new ImplicitParameterNode();
		aBuilder.createAddNodeOperation(param, new Point(118, 0)).execute();
		aDiagram.layout();
	
		CallNode node = new CallNode();
		aBuilder.createAddNodeOperation(node, new Point(152, 70)).execute();
		aDiagram.layout();		

		ImplicitParameterNode param2 = new ImplicitParameterNode();
		aBuilder.createAddNodeOperation(param2, new Point(347, 0)).execute();
		aDiagram.layout();
		
		Edge callEdge = new CallEdge();
		aDiagram.restoreEdge(callEdge, node, param2);
		aDiagram.layout();
		
		CallNode node2 = new CallNode();
		aBuilder.createAddNodeOperation(node2, new Point(160, 90)).execute();
		aDiagram.layout();		

		Edge callEdge2 = new CallEdge();
		aDiagram.restoreEdge(callEdge2, node, node2);
		aDiagram.layout();
		
		// Point outside the bounds
		assertNull(aDiagram.deepFindNode(node, new Point(50, 50)));
		
		// Point inside the bounds of the caller but not the self-call callee
		assertTrue(aDiagram.deepFindNode(node, new Point(155, 80)) == node);
		
		// Point inside both caller and self-call callee
		assertTrue(aDiagram.deepFindNode(node, new Point(165, 140)) == node2);
		
		// Point inside the bounds of the first param
		assertTrue(aDiagram.deepFindNode(param, new Point(125, 10)) == param);
		
		// Point inside the bounds of the second param
		// Note that this assert passes because containment for ImplicitParameterNode
		// only check whether the point is in the column induced by the right and left sides 
		// of the rectangular node.
		assertTrue(aDiagram.deepFindNode(param2, new Point(355, 10)) == param2);
	}
}
