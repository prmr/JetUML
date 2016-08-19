/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016 by the contributors of the JetUML project.
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

package ca.mcgill.cs.stg.jetuml.diagrams;

import static org.junit.Assert.*;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.graph.CallEdge;
import ca.mcgill.cs.stg.jetuml.graph.CallNode;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.ImplicitParameterNode;

/**
 * @author Martin P. Robillard
 * @author Gabriel Cormier-Affleck (testDeepFindNodeTwoChildren)
 */
public class TestSequenceDiagramGraph
{
	 private SequenceDiagramGraph aGraph;
	 private Graphics2D aGraphics;
	 private Grid aGrid;
	 
	 @Before
	 public void setup()
	 {
		 aGraph = new SequenceDiagramGraph();
		 aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
		 aGrid = new Grid();
	 }
	 
	 @Test
	 public void testDeepFindNodeNoChild()
	 {
		 ImplicitParameterNode param = new ImplicitParameterNode();
		 aGraph.addNode(param, new Point2D.Double(20,0));
		 CallNode node = new CallNode();
		 aGraph.addNode(node, new Point2D.Double(40, 90));
		 aGraph.layout(aGraphics, aGrid);
		 
		 // Point outside the bounds
		 assertNull(aGraph.deepFindNode(node, new Point2D.Double(50, 0)));
		 assertNull(aGraph.deepFindNode(node, new Point2D.Double(60, 0)));
		 
		 // Point inside the bounds
		 assertTrue(aGraph.deepFindNode(node, new Point2D.Double(60, 100)) == node);
	 }
	 
	 @Test
	 public void testDeepFindNodeOneChild()
	 {
		 ImplicitParameterNode param = new ImplicitParameterNode();
		 aGraph.addNode(param, new Point2D.Double(20,0));
		 CallNode node = new CallNode();
		 aGraph.addNode(node, new Point2D.Double(40, 90));
		 aGraph.layout(aGraphics, aGrid);
		 CallNode callee = new CallNode();
		 param.addChild(callee,new Point2D.Double(60, 100));
		 Edge callEdge = new CallEdge();
		 aGraph.insertEdge(callEdge);
		 callEdge.connect(node,  callee, aGraph);
		 aGraph.layout(aGraphics, aGrid);
	 
		 // Point outside the bounds
		 assertNull(aGraph.deepFindNode(node, new Point2D.Double(50, 0)));
		 assertNull(aGraph.deepFindNode(node, new Point2D.Double(60, 0)));
		 
		 // Point inside the bounds of the caller but not the callee
		 assertTrue(aGraph.deepFindNode(node, new Point2D.Double(60, 100)) == node);
		 
		// Point inside both the caller and the callee
		assertTrue(aGraph.deepFindNode(node, new Point2D.Double(64, 110)) == callee);
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
		aGraph.addNode(param, new Point2D.Double(118, 0));
		aGraph.layout(aGraphics, aGrid);

		CallNode node = new CallNode();
		aGraph.addNode(node, new Point2D.Double(152, 70));
		aGraph.layout(aGraphics, aGrid);		

		ImplicitParameterNode param2 = new ImplicitParameterNode();
		aGraph.addNode(param2, new Point2D.Double(347, 0));
		aGraph.layout(aGraphics, aGrid);

		CallNode node2 = new CallNode();
		aGraph.addNode(node2, new Point2D.Double(382, 80));
		aGraph.layout(aGraphics, aGrid);

		Edge callEdge = new CallEdge();
		aGraph.insertEdge(callEdge);
		callEdge.connect(node, node2, aGraph);
		aGraph.layout(aGraphics, aGrid);

		CallNode node3 = new CallNode();
		aGraph.addNode(node3, new Point2D.Double(160, 125));
		aGraph.layout(aGraphics, aGrid);		

		Edge callEdge2 = new CallEdge();
		aGraph.insertEdge(callEdge2);
		callEdge2.connect(node, node3, aGraph);
		aGraph.layout(aGraphics, aGrid);

		// Point outside the bounds
		assertNull(aGraph.deepFindNode(node, new Point2D.Double(171, 71)));
		
		// Point inside the bounds of the caller but not the self-call callee
		assertTrue(aGraph.deepFindNode(node, new Point2D.Double(157, 96)) == node);
		
		// Point inside the bounds of the call node on the second implicit parameter
		assertTrue(aGraph.deepFindNode(node, new Point2D.Double(386, 96)) == node2);

		// Point inside both caller and self-call callee
		assertTrue(aGraph.deepFindNode(node, new Point2D.Double(161, 139)) == node3);
	}
	
	@Test
	public void testDeepFindNodeCreateNode()
	{
		ImplicitParameterNode param = new ImplicitParameterNode();
		aGraph.addNode(param, new Point2D.Double(118, 0));
		aGraph.layout(aGraphics, aGrid);
	
		CallNode node = new CallNode();
		aGraph.addNode(node, new Point2D.Double(152, 70));
		aGraph.layout(aGraphics, aGrid);		

		ImplicitParameterNode param2 = new ImplicitParameterNode();
		aGraph.addNode(param2, new Point2D.Double(347, 0));
		aGraph.layout(aGraphics, aGrid);
		
		Edge callEdge = new CallEdge();
		aGraph.insertEdge(callEdge);
		callEdge.connect(node, param2);
		aGraph.layout(aGraphics, aGrid);
		
		CallNode node2 = new CallNode();
		aGraph.addNode(node2, new Point2D.Double(160, 90));
		aGraph.layout(aGraphics, aGrid);		

		Edge callEdge2 = new CallEdge();
		aGraph.insertEdge(callEdge2);
		callEdge2.connect(node, node2);
		aGraph.layout(aGraphics, aGrid);
		
		// Point outside the bounds
		assertNull(aGraph.deepFindNode(node, new Point2D.Double(50, 50)));
		
		// Point inside the bounds of the caller but not the self-call callee
		assertTrue(aGraph.deepFindNode(node, new Point2D.Double(155, 80)) == node);
		
		// Point inside both caller and self-call callee
		assertTrue(aGraph.deepFindNode(node, new Point2D.Double(165, 140)) == node2);
		
		// Point inside the bounds of the first param
		assertTrue(aGraph.deepFindNode(param, new Point2D.Double(125, 10)) == param);
		
		// Point inside the bounds of the second param
		// Note that this assert passes because containment for ImplicitParameterNode
		// only check whether the point is in the column induced by the right and left sides 
		// of the rectangular node.
		assertTrue(aGraph.deepFindNode(param2, new Point2D.Double(355, 10)) == param2);
	}
}
