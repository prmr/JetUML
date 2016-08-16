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
		 callEdge.connect(node,  callee);
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
		 * This exact setup is illustrated at http://cs.mcgill.ca/~gcormi5/test1.jpg
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
		callEdge.connect(node, node2);
		aGraph.layout(aGraphics, aGrid);

		CallNode node3 = new CallNode();
		aGraph.addNode(node3, new Point2D.Double(160, 125));
		aGraph.layout(aGraphics, aGrid);		

		Edge callEdge2 = new CallEdge();
		aGraph.insertEdge(callEdge2);
		callEdge2.connect(node, node3);
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
}
