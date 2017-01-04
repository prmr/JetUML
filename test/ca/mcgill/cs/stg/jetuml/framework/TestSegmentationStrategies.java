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
package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.geom.Point2D;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.graph.AggregationEdge;
import ca.mcgill.cs.stg.jetuml.graph.AssociationEdge;
import ca.mcgill.cs.stg.jetuml.graph.ClassNode;
import ca.mcgill.cs.stg.jetuml.graph.DependencyEdge;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.GeneralizationEdge;
import ca.mcgill.cs.stg.jetuml.graph.PackageNode;

public class TestSegmentationStrategies 
{
	private static final int MAX_NUDGE = 11; // Same as in SegmentationStyleFactory
	
	private PackageNode aNode1;
	private PackageNode aNode2;
	private ClassNode aNode3;
	private ClassNode aNode4;
	private PackageNode aNode5;
	private ClassDiagramGraph aGraph;
	
	@Before
	public void setup()
	{
		// Default-sized node rooted at (30,30)
		aNode1 = new PackageNode();
		aNode1.translate(30, 30);
		aNode2 = new PackageNode();
		aNode2.translate(200, 100);
		aNode3 = new ClassNode();
		aNode4 = new ClassNode();
		aNode3.translate(20, 20);
		aNode4.translate(110, 20);
		aNode5 = new PackageNode();
		aNode5.translate(200, 250);
		aGraph = new ClassDiagramGraph();
		aGraph.insertNode(aNode1);
		aGraph.insertNode(aNode2);
		aGraph.insertNode(aNode3);
		aGraph.insertNode(aNode4);
		aGraph.insertNode(aNode5);
	}
	
	@Test
	public void testSelfEdge1()
	{
		DependencyEdge edge = new DependencyEdge();
		aGraph.restoreEdge(edge, aNode1, aNode1);
		Point2D[] points = SegmentationStyleFactory.createStraightStrategy().getPath(edge, aGraph);
		assertEquals( 5, points.length );
		assertEquals( new Point2D.Double(110,50), points[0]);
		assertEquals( new Point2D.Double(110,30), points[1]);
		assertEquals( new Point2D.Double(150,30), points[2]);
		assertEquals( new Point2D.Double(150,70), points[3]);
		assertEquals( new Point2D.Double(130,70), points[4]);
	}
	
	@Test
	public void testSelfEdge2()
	{
		DependencyEdge edge = new DependencyEdge();
		aGraph.restoreEdge(edge, aNode3, aNode3);
		Point2D[] points = SegmentationStyleFactory.createStraightStrategy().getPath(edge, aGraph);
		assertEquals( 5, points.length );
		assertEquals( new Point2D.Double(100,20), points[0]);
		assertEquals( new Point2D.Double(100,0), points[1]);
		assertEquals( new Point2D.Double(140,0), points[2]);
		assertEquals( new Point2D.Double(140,40), points[3]);
		assertEquals( new Point2D.Double(120,40), points[4]);
	}
	
	@Test
	public void testStraight1a()
	{
		Edge edge1 = new DependencyEdge();
		aGraph.restoreEdge(edge1, aNode1, aNode2);
		
		Point2D[] points = SegmentationStyleFactory.createStraightStrategy().getPath(edge1, aGraph);
		assertEquals( 2, points.length );
		assertEquals( new Point2D.Double(130,70), points[0]);
		assertEquals( new Point2D.Double(200,140), points[1]);
	}
	
	@Test
	public void testStraight1b()
	{
		Edge edge2 = new DependencyEdge();
		aGraph.restoreEdge(edge2, aNode2, aNode1);
		
		Point2D[] points = SegmentationStyleFactory.createStraightStrategy().getPath(edge2, aGraph);
		assertEquals( 2, points.length );
		assertEquals( new Point2D.Double(130,70), points[1]);
		assertEquals( new Point2D.Double(200,140), points[0]);
	}
	
	@Test
	public void testStraight2()
	{
		Edge edge = new DependencyEdge();
		aGraph.restoreEdge(edge, aNode3, aNode4);
		Point2D[] points = SegmentationStyleFactory.createStraightStrategy().getPath(edge, aGraph);
		assertEquals( 2, points.length );
		assertEquals( new Point2D.Double(120,50), points[0]);
		assertEquals( new Point2D.Double(110,50), points[1]);
	}
	
	@Test
	public void testHVH1a()
	{
		Edge edge = new DependencyEdge();
		aGraph.restoreEdge(edge, aNode1, aNode2);
		Point2D[] points = SegmentationStyleFactory.createHVHStrategy().getPath(edge, aGraph);
		assertEquals( 4, points.length );
		assertEquals( new Point2D.Double(130,70), points[0]);
		assertEquals( new Point2D.Double(165,70), points[1]);
		assertEquals( new Point2D.Double(165,140), points[2]);
		assertEquals( new Point2D.Double(200,140), points[3]);
	}
	
	@Test
	public void testHVH1b()
	{
		Edge edge = new DependencyEdge();
		aGraph.restoreEdge(edge, aNode2, aNode1);
		Point2D[] points = SegmentationStyleFactory.createHVHStrategy().getPath(edge, aGraph);
		assertEquals( 4, points.length );
		assertEquals( new Point2D.Double(130,70), points[3]);
		assertEquals( new Point2D.Double(165,70), points[2]);
		assertEquals( new Point2D.Double(165,140), points[1]);
		assertEquals( new Point2D.Double(200,140), points[0]);
	}
	
	@Test
	public void testHVH2()
	{
		Edge edge = new DependencyEdge();
		aGraph.restoreEdge(edge, aNode3, aNode4);
		Point2D[] points = SegmentationStyleFactory.createHVHStrategy().getPath(edge, aGraph);
		assertEquals( 2, points.length );
		assertEquals( new Point2D.Double(120,50), points[0]);
		assertEquals( new Point2D.Double(110,50), points[1]);
	}
	
	@Test
	public void testHVH3()
	{
		ClassNode node = aNode3.clone();
		node.translate(150, 5);
		aGraph.insertNode(node);
		Edge edge = new DependencyEdge();
		aGraph.restoreEdge(edge, aNode3, node);
		Point2D[] points = SegmentationStyleFactory.createHVHStrategy().getPath(edge, aGraph);
		assertEquals( 2, points.length );
		assertEquals( new Point2D.Double(120,55), points[0]);
		assertEquals( new Point2D.Double(170,55), points[1]);
	}
	
	@Test
	public void testVHV1a()
	{
		Edge edge = new DependencyEdge();
		aGraph.restoreEdge(edge, aNode1, aNode2);
		Point2D[] points = SegmentationStyleFactory.createVHVStrategy().getPath(edge, aGraph);
		assertEquals( 4, points.length );
		assertEquals( new Point2D.Double(130,70), points[0]);
		assertEquals( new Point2D.Double(165,70), points[1]);
		assertEquals( new Point2D.Double(165,140), points[2]);
		assertEquals( new Point2D.Double(200,140), points[3]);
	}
	
	@Test
	public void testVHV1b()
	{
		Edge edge2 = new DependencyEdge();
		aGraph.restoreEdge(edge2, aNode2, aNode1);
		Point2D[] points = SegmentationStyleFactory.createVHVStrategy().getPath(edge2, aGraph);
		assertEquals( 4, points.length );
		assertEquals( new Point2D.Double(130,70), points[3]);
		assertEquals( new Point2D.Double(165,70), points[2]);
		assertEquals( new Point2D.Double(165,140), points[1]);
		assertEquals( new Point2D.Double(200,140), points[0]);
	}
	
	@Test
	public void testVHV2()
	{
		Edge edge = new DependencyEdge();
		aGraph.restoreEdge(edge, aNode3, aNode4);
		Point2D[] points = SegmentationStyleFactory.createVHVStrategy().getPath(edge, aGraph);
		assertEquals( 2, points.length );
		assertEquals( new Point2D.Double(120,50), points[0]);
		assertEquals( new Point2D.Double(110,50), points[1]);
	}
	
	@Test
	public void testVHV3a()
	{
		Edge edge = new DependencyEdge();
		aGraph.restoreEdge(edge, aNode2, aNode5);
		Point2D[] points = SegmentationStyleFactory.createVHVStrategy().getPath(edge, aGraph);
		assertEquals( 2, points.length );
		assertEquals( new Point2D.Double(250,180), points[0]);
		assertEquals( new Point2D.Double(250,250), points[1]);
	}
	
	@Test
	public void testVHV3b()
	{
		Edge edge = new AggregationEdge();
		aNode5.translate(100,0);
		aGraph.restoreEdge(edge, aNode2, aNode5);
		Point2D[] points = SegmentationStyleFactory.createVHVStrategy().getPath(edge, aGraph);
		assertEquals( 4, points.length );
		assertEquals( new Point2D.Double(250,180), points[0]);
		assertEquals( new Point2D.Double(250,215), points[1]);
		assertEquals( new Point2D.Double(350,215), points[2]);
		assertEquals( new Point2D.Double(350,250), points[3]);
	}
	
	@Test
	public void testVHV3c()
	{
		Edge edge2 = new AggregationEdge();
		aGraph.restoreEdge(edge2, aNode5, aNode2);
		Point2D[] points = SegmentationStyleFactory.createVHVStrategy().getPath(edge2, aGraph);
		assertEquals( 2, points.length );
		assertEquals( new Point2D.Double(250,250), points[0]);
		assertEquals( new Point2D.Double(250,180), points[1]);
	}
	
	/*
	 * Two horizontal dependency edges between two nodes, one from A to B and another
	 * one in the other direction.
	 */
	@Test
	public void testStraightMultipleEdgesHorizontal()
	{
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		node2.translate(200, 0);
		aGraph.insertNode(node1);
		aGraph.insertNode(node2);
		DependencyEdge edge1 = new DependencyEdge();
		DependencyEdge edge2 = new DependencyEdge();
		aGraph.restoreEdge(edge1, node1, node2);
		aGraph.restoreEdge(edge2, node2, node1);
		
		Point2D[] points = SegmentationStyleFactory.createStraightStrategy().getPath(edge1, aGraph);
		assertEquals( 2, points.length );
		assertEquals( new Point2D.Double(100,30-(MAX_NUDGE/2.0)), points[0]);
		assertEquals( new Point2D.Double(200,30-(MAX_NUDGE/2.0)), points[1]);
		
		points = SegmentationStyleFactory.createStraightStrategy().getPath(edge2, aGraph);
		assertEquals( 2, points.length );
		assertEquals( new Point2D.Double(200,30+(MAX_NUDGE/2.0)), points[0]);
		assertEquals( new Point2D.Double(100,30+(MAX_NUDGE/2.0)), points[1]);
	}
	
	/*
	 * Four interleaved vertical edges between two nodes, two in each direction, one
	 * dependency and one association
	 */
	@Test
	public void testStraightMultipleEdgesVertical()
	{
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		node2.translate(0, 200);
		aGraph.insertNode(node1);
		aGraph.insertNode(node2);
		DependencyEdge edge1 = new DependencyEdge();
		DependencyEdge edge2 = new DependencyEdge();
		AssociationEdge edge3 = new AssociationEdge();
		AssociationEdge edge4 = new AssociationEdge();
		aGraph.restoreEdge(edge1, node1, node2);
		aGraph.restoreEdge(edge2, node2, node1);
		aGraph.restoreEdge(edge3, node1, node2);
		aGraph.restoreEdge(edge4, node2, node1);
		
		Point2D[] points = SegmentationStyleFactory.createStraightStrategy().getPath(edge1, aGraph);
		assertEquals( 2, points.length );
		assertEquals( new Point2D.Double(50+(MAX_NUDGE/2.0),60), points[0]);
		assertEquals( new Point2D.Double(50+(MAX_NUDGE/2.0),200), points[1]);
		
		points = SegmentationStyleFactory.createStraightStrategy().getPath(edge2, aGraph);
		assertEquals( 2, points.length );
		assertEquals( new Point2D.Double(50+(MAX_NUDGE*3/2.0),200), points[0]);
		assertEquals( new Point2D.Double(50+(MAX_NUDGE*3/2.0),60), points[1]);
		
		points = SegmentationStyleFactory.createStraightStrategy().getPath(edge3, aGraph);
		assertEquals( 2, points.length );
		assertEquals( new Point2D.Double(50-(MAX_NUDGE*3/2.0),60), points[0]);
		assertEquals( new Point2D.Double(50-(MAX_NUDGE*3/2.0),200), points[1]);
		
		points = SegmentationStyleFactory.createStraightStrategy().getPath(edge4, aGraph);
		assertEquals( 2, points.length );
		assertEquals( new Point2D.Double(50-(MAX_NUDGE/2.0),200), points[0]);
		assertEquals( new Point2D.Double(50-(MAX_NUDGE/2.0),60), points[1]);
	}
	
	/*
	 * Two straight dependencies and one self-edge all originating 
	 * horizontally from the same node.
	 */
	@Test
	public void testStraightHorizontalWithSelfEdge()
	{
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		ClassNode node3 = new ClassNode();
		node2.translate(200, 0);
		node3.translate(200, 200);
		aGraph.insertNode(node1);
		aGraph.insertNode(node2);
		aGraph.insertNode(node3);
		DependencyEdge edge1 = new DependencyEdge();
		DependencyEdge edge2 = new DependencyEdge();
		DependencyEdge edge3 = new DependencyEdge();
		aGraph.restoreEdge(edge1, node1, node1);
		aGraph.restoreEdge(edge2, node1, node2);
		aGraph.restoreEdge(edge3, node1, node3);
		
		Point2D[] points = SegmentationStyleFactory.createStraightStrategy().getPath(edge1, aGraph);
		assertEquals( 5, points.length );
		assertEquals( new Point2D.Double(80,0), points[0]);
		assertEquals( new Point2D.Double(80,-20), points[1]);
		assertEquals( new Point2D.Double(120,-20), points[2]);
		assertEquals( new Point2D.Double(120,20), points[3]);
		assertEquals( new Point2D.Double(100,20), points[4]);
		
		points = SegmentationStyleFactory.createStraightStrategy().getPath(edge2, aGraph);
		assertEquals( 2, points.length );
		assertEquals( 100, points[0].getX(), 0.01);
		assertEquals( 33.33, points[0].getY(), 0.01);
		assertEquals( 200, points[1].getX(), 0.01);
		assertEquals( 30, points[1].getY(), 0.01);
		
		points = SegmentationStyleFactory.createStraightStrategy().getPath(edge3, aGraph);
		assertEquals( 2, points.length );
		assertEquals( 100, points[0].getX(), 0.01);
		assertEquals( 46.67, points[0].getY(), 0.01);
		assertEquals( 200, points[1].getX(), 0.01);
		assertEquals( 230, points[1].getY(), 0.01);
	}
	
	/*
	 * Two straight dependencies and one self-edge all originating 
	 * vertically from the same node.
	 */
	@Test
	public void testStraightVerticalWithSelfEdge()
	{
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		ClassNode node3 = new ClassNode();
		node1.translate(1000, 1000);
		node2.translate(900, 500);
		node3.translate(1100, 500);
		aGraph.insertNode(node1);
		aGraph.insertNode(node2);
		aGraph.insertNode(node3);
		DependencyEdge edge1 = new DependencyEdge();
		DependencyEdge edge2 = new DependencyEdge();
		DependencyEdge edge3 = new DependencyEdge();
		aGraph.restoreEdge(edge1, node1, node1);
		aGraph.restoreEdge(edge2, node1, node2);
		aGraph.restoreEdge(edge3, node1, node3);
		
		Point2D[] points = SegmentationStyleFactory.createStraightStrategy().getPath(edge1, aGraph);
		assertEquals( 5, points.length );
		assertEquals( new Point2D.Double(1080,1000), points[0]);
		assertEquals( new Point2D.Double(1080,980), points[1]);
		assertEquals( new Point2D.Double(1120,980), points[2]);
		assertEquals( new Point2D.Double(1120,1020), points[3]);
		assertEquals( new Point2D.Double(1100,1020), points[4]);
		
		points = SegmentationStyleFactory.createStraightStrategy().getPath(edge2, aGraph);
		assertEquals( 2, points.length );
		assertEquals( 1026.67, points[0].getX(), 0.01);
		assertEquals( 1000, points[0].getY(), 0.01);
		assertEquals( 950, points[1].getX(), 0.01);
		assertEquals( 560, points[1].getY(), 0.01);
		
		points = SegmentationStyleFactory.createStraightStrategy().getPath(edge3, aGraph);
		assertEquals( 2, points.length );
		assertEquals( 1053.33, points[0].getX(), 0.01);
		assertEquals( 1000, points[0].getY(), 0.01);
		assertEquals( 1150, points[1].getX(), 0.01);
		assertEquals( 560, points[1].getY(), 0.01);
	}
	
	/*
	 * Two generalization edges vertically oriented, collapsed into a single end point.
	 */
	@Test
	public void testAggregateGeneralizationEdgesOnly()
	{
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		ClassNode node3 = new ClassNode();
		node1.translate(1000, 0);
		node2.translate(900, 500);
		node3.translate(1100, 500);
		aGraph.insertNode(node1);
		aGraph.insertNode(node2);
		aGraph.insertNode(node3);
		GeneralizationEdge edge1 = new GeneralizationEdge();
		GeneralizationEdge edge2 = new GeneralizationEdge();
		aGraph.restoreEdge(edge1, node2, node1);
		aGraph.restoreEdge(edge2, node3, node1);
		
		Point2D[] points = SegmentationStyleFactory.createVHVStrategy().getPath(edge1, aGraph);
		assertEquals( 4, points.length );
		assertEquals( 950, points[0].getX(), 0.01);
		assertEquals( 500, points[0].getY(), 0.01);
		assertEquals( 950, points[1].getX(), 0.01);
		assertEquals( 280, points[1].getY(), 0.01);
		assertEquals( 1050, points[2].getX(), 0.01);
		assertEquals( 280, points[2].getY(), 0.01);
		assertEquals( 1050, points[3].getX(), 0.01);
		assertEquals( 60, points[3].getY(), 0.01);
		
		points = SegmentationStyleFactory.createVHVStrategy().getPath(edge2, aGraph);
		assertEquals( 4, points.length );
		assertEquals( 1150, points[0].getX(), 0.01);
		assertEquals( 500, points[0].getY(), 0.01);
		assertEquals( 1150, points[1].getX(), 0.01);
		assertEquals( 280, points[1].getY(), 0.01);
		assertEquals( 1050, points[2].getX(), 0.01);
		assertEquals( 280, points[2].getY(), 0.01);
		assertEquals( 1050, points[3].getX(), 0.01);
		assertEquals( 60, points[3].getY(), 0.01);
	}
}
