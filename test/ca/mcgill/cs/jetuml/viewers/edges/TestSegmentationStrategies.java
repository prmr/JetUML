/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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
package ca.mcgill.cs.jetuml.viewers.edges;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.edges.AggregationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.AssociationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.edges.GeneralizationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.GeneralizationEdge.Type;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import javafx.geometry.Point2D;

public class TestSegmentationStrategies 
{
	private PackageNode aNode1;
	private PackageNode aNode2;
	private ClassNode aNode3;
	private ClassNode aNode4;
	private PackageNode aNode5;
	private Diagram aGraph;
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
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
		aGraph = new Diagram(DiagramType.CLASS);
		aGraph.addRootNode(aNode1);
		aGraph.addRootNode(aNode2);
		aGraph.addRootNode(aNode3);
		aGraph.addRootNode(aNode4);
		aGraph.addRootNode(aNode5);
	}
	
	@Test
	public void testSelfEdge1()
	{
		DependencyEdge edge = new DependencyEdge();
		edge.connect(aNode1, aNode1, aGraph);
		aGraph.addEdge(edge);
		Point2D[] points = SegmentationStyleFactory.createStraightStrategy().getPath(edge);
		assertEquals( 5, points.length );
		assertEquals( new Point2D(110,50), points[0]);
		assertEquals( new Point2D(110,30), points[1]);
		assertEquals( new Point2D(150,30), points[2]);
		assertEquals( new Point2D(150,70), points[3]);
		assertEquals( new Point2D(130,70), points[4]);
	}
	
	@Test
	public void testSelfEdge2()
	{
		DependencyEdge edge = new DependencyEdge();
		edge.connect(aNode3, aNode3, aGraph);
		aGraph.addEdge(edge);
		Point2D[] points = SegmentationStyleFactory.createStraightStrategy().getPath(edge);
		assertEquals( 5, points.length );
		assertEquals( new Point2D(100,20), points[0]);
		assertEquals( new Point2D(100,0), points[1]);
		assertEquals( new Point2D(140,0), points[2]);
		assertEquals( new Point2D(140,40), points[3]);
		assertEquals( new Point2D(120,40), points[4]);
	}
	
	@Test
	public void testStraight1a()
	{
		Edge edge1 = new DependencyEdge();
		edge1.connect(aNode1, aNode2, aGraph);
		aGraph.addEdge(edge1);
		
		Point2D[] points = SegmentationStyleFactory.createStraightStrategy().getPath(edge1);
		assertEquals( 2, points.length );
		assertEquals( new Point2D(130,70), points[0]);
		assertEquals( new Point2D(200,140), points[1]);
	}
	
	@Test
	public void testStraight1b()
	{
		Edge edge2 = new DependencyEdge();
		edge2.connect(aNode2, aNode1, aGraph);
		aGraph.addEdge(edge2);
		
		Point2D[] points = SegmentationStyleFactory.createStraightStrategy().getPath(edge2);
		assertEquals( 2, points.length );
		assertEquals( new Point2D(130,70), points[1]);
		assertEquals( new Point2D(200,140), points[0]);
	}
	
	@Test
	public void testStraight2()
	{
		Edge edge = new DependencyEdge();
		edge.connect(aNode3, aNode4, aGraph);
		aGraph.addEdge(edge);
		Point2D[] points = SegmentationStyleFactory.createStraightStrategy().getPath(edge);
		assertEquals( 2, points.length );
		assertEquals( new Point2D(120,50), points[0]);
		assertEquals( new Point2D(110,50), points[1]);
	}
	
	@Test
	public void testHVH1a()
	{
		Edge edge = new DependencyEdge();
		edge.connect(aNode1, aNode2, aGraph);
		aGraph.addEdge(edge);
		Point2D[] points = SegmentationStyleFactory.createHVHStrategy().getPath(edge);
		assertEquals( 4, points.length );
		assertEquals( new Point2D(130,70), points[0]);
		assertEquals( new Point2D(165,70), points[1]);
		assertEquals( new Point2D(165,140), points[2]);
		assertEquals( new Point2D(200,140), points[3]);
	}
	
	@Test
	public void testHVH1b()
	{
		Edge edge = new DependencyEdge();
		edge.connect(aNode2, aNode1, aGraph);
		aGraph.addEdge(edge);
		Point2D[] points = SegmentationStyleFactory.createHVHStrategy().getPath(edge);
		assertEquals( 4, points.length );
		assertEquals( new Point2D(130,70), points[3]);
		assertEquals( new Point2D(165,70), points[2]);
		assertEquals( new Point2D(165,140), points[1]);
		assertEquals( new Point2D(200,140), points[0]);
	}
	
	@Test
	public void testHVH2()
	{
		Edge edge = new DependencyEdge();
		edge.connect(aNode3, aNode4, aGraph);
		aGraph.addEdge(edge);
		Point2D[] points = SegmentationStyleFactory.createHVHStrategy().getPath(edge);
		assertEquals( 2, points.length );
		assertEquals( new Point2D(120,50), points[0]);
		assertEquals( new Point2D(110,50), points[1]);
	}
	
	@Test
	public void testHVH3()
	{
		ClassNode node = (ClassNode) aNode3.clone();
		node.translate(150, 5);
		aGraph.addRootNode(node);
		Edge edge = new DependencyEdge();
		edge.connect(aNode3, node, aGraph);
		aGraph.addEdge(edge);
		Point2D[] points = SegmentationStyleFactory.createHVHStrategy().getPath(edge);
		assertEquals( 2, points.length );
		assertEquals( new Point2D(120,55), points[0]);
		assertEquals( new Point2D(170,55), points[1]);
	}
	
	@Test
	public void testVHV1a()
	{
		Edge edge = new DependencyEdge();
		edge.connect(aNode1, aNode2, aGraph);
		aGraph.addEdge(edge);
		Point2D[] points = SegmentationStyleFactory.createVHVStrategy().getPath(edge);
		assertEquals( 4, points.length );
		assertEquals( new Point2D(130,70), points[0]);
		assertEquals( new Point2D(165,70), points[1]);
		assertEquals( new Point2D(165,140), points[2]);
		assertEquals( new Point2D(200,140), points[3]);
	}
	
	@Test
	public void testVHV1b()
	{
		Edge edge2 = new DependencyEdge();
		edge2.connect(aNode2, aNode1, aGraph);
		aGraph.addEdge(edge2);
		Point2D[] points = SegmentationStyleFactory.createVHVStrategy().getPath(edge2);
		assertEquals( 4, points.length );
		assertEquals( new Point2D(130,70), points[3]);
		assertEquals( new Point2D(165,70), points[2]);
		assertEquals( new Point2D(165,140), points[1]);
		assertEquals( new Point2D(200,140), points[0]);
	}
	
	@Test
	public void testVHV2()
	{
		Edge edge = new DependencyEdge();
		edge.connect(aNode3, aNode4, aGraph);
		aGraph.addEdge(edge);
		Point2D[] points = SegmentationStyleFactory.createVHVStrategy().getPath(edge);
		assertEquals( 2, points.length );
		assertEquals( new Point2D(120,50), points[0]);
		assertEquals( new Point2D(110,50), points[1]);
	}
	
	@Test
	public void testVHV3a()
	{
		Edge edge = new DependencyEdge();
		edge.connect(aNode2, aNode5, aGraph);
		aGraph.addEdge(edge);
		Point2D[] points = SegmentationStyleFactory.createVHVStrategy().getPath(edge);
		assertEquals( 2, points.length );
		assertEquals( new Point2D(250,180), points[0]);
		assertEquals( new Point2D(250,250), points[1]);
	}
	
	@Test
	public void testVHV3b()
	{
		Edge edge = new AggregationEdge();
		aNode5.translate(100,0);
		edge.connect(aNode2, aNode5, aGraph);
		aGraph.addEdge(edge);
		Point2D[] points = SegmentationStyleFactory.createVHVStrategy().getPath(edge);
		assertEquals( 4, points.length );
		assertEquals( new Point2D(250,180), points[0]);
		assertEquals( new Point2D(250,215), points[1]);
		assertEquals( new Point2D(350,215), points[2]);
		assertEquals( new Point2D(350,250), points[3]);
	}
	
	@Test
	public void testVHV3c()
	{
		Edge edge2 = new AggregationEdge();
		edge2.connect(aNode5, aNode2, aGraph);
		aGraph.addEdge(edge2);
		Point2D[] points = SegmentationStyleFactory.createVHVStrategy().getPath(edge2);
		assertEquals( 2, points.length );
		assertEquals( new Point2D(250,250), points[0]);
		assertEquals( new Point2D(250,180), points[1]);
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
		aGraph.addRootNode(node1);
		aGraph.addRootNode(node2);
		DependencyEdge edge1 = new DependencyEdge();
		DependencyEdge edge2 = new DependencyEdge();
		edge1.connect(node1, node2, aGraph);
		aGraph.addEdge(edge1);
		edge2.connect(node2, node1, aGraph);
		aGraph.addEdge(edge2);
		
		Point2D[] points = SegmentationStyleFactory.createStraightStrategy().getPath(edge1);
		assertEquals( 2, points.length );
		assertEquals( new Point2D(100,25), points[0]);
		assertEquals( new Point2D(200,25), points[1]);
		
		points = SegmentationStyleFactory.createStraightStrategy().getPath(edge2);
		assertEquals( 2, points.length );
		assertEquals( new Point2D(200,36), points[0]);
		assertEquals( new Point2D(100,36), points[1]);
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
		aGraph.addRootNode(node1);
		aGraph.addRootNode(node2);
		DependencyEdge edge1 = new DependencyEdge();
		DependencyEdge edge2 = new DependencyEdge();
		AssociationEdge edge3 = new AssociationEdge();
		AssociationEdge edge4 = new AssociationEdge();
		edge1.connect(node1, node2, aGraph);
		aGraph.addEdge(edge1);
		edge2.connect(node2, node1, aGraph);
		aGraph.addEdge(edge2);
		edge3.connect(node1, node2, aGraph);
		aGraph.addEdge(edge3);
		edge4.connect(node2, node1, aGraph);
		aGraph.addEdge(edge4);
		
		Point2D[] points = SegmentationStyleFactory.createStraightStrategy().getPath(edge1);
		assertEquals( 2, points.length );
		assertEquals( new Point2D(56,60), points[0]);
		assertEquals( new Point2D(56,200), points[1]);
		
		points = SegmentationStyleFactory.createStraightStrategy().getPath(edge2);
		assertEquals( 2, points.length );
		assertEquals( new Point2D(67,200), points[0]);
		assertEquals( new Point2D(67,60), points[1]);
		
		points = SegmentationStyleFactory.createStraightStrategy().getPath(edge3);
		assertEquals( 2, points.length );
		assertEquals( new Point2D(34,60), points[0]);
		assertEquals( new Point2D(34,200), points[1]);
		
		points = SegmentationStyleFactory.createStraightStrategy().getPath(edge4);
		assertEquals( 2, points.length );
		assertEquals( new Point2D(45,200), points[0]);
		assertEquals( new Point2D(45,60), points[1]);
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
		aGraph.addRootNode(node1);
		aGraph.addRootNode(node2);
		aGraph.addRootNode(node3);
		DependencyEdge edge1 = new DependencyEdge();
		DependencyEdge edge2 = new DependencyEdge();
		DependencyEdge edge3 = new DependencyEdge();
		edge1.connect(node1, node1, aGraph);
		aGraph.addEdge(edge1);
		edge2.connect(node1, node2, aGraph);
		aGraph.addEdge(edge2);
		edge3.connect(node1, node3, aGraph);
		aGraph.addEdge(edge3);
		
		Point2D[] points = SegmentationStyleFactory.createStraightStrategy().getPath(edge1);
		assertEquals( 5, points.length );
		assertEquals( new Point2D(80,0), points[0]);
		assertEquals( new Point2D(80,-20), points[1]);
		assertEquals( new Point2D(120,-20), points[2]);
		assertEquals( new Point2D(120,20), points[3]);
		assertEquals( new Point2D(100,20), points[4]);
		
		points = SegmentationStyleFactory.createStraightStrategy().getPath(edge2);
		assertEquals( 2, points.length );
		assertEquals( 100, points[0].getX(), 0.01);
		assertEquals( 33, points[0].getY(), 0.01);
		assertEquals( 200, points[1].getX(), 0.01);
		assertEquals( 30, points[1].getY(), 0.01);
		
		points = SegmentationStyleFactory.createStraightStrategy().getPath(edge3);
		assertEquals( 2, points.length );
		assertEquals( 100, points[0].getX(), 0.01);
		assertEquals( 46, points[0].getY(), 0.01);
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
		aGraph.addRootNode(node1);
		aGraph.addRootNode(node2);
		aGraph.addRootNode(node3);
		DependencyEdge edge1 = new DependencyEdge();
		DependencyEdge edge2 = new DependencyEdge();
		DependencyEdge edge3 = new DependencyEdge();
		edge1.connect(node1, node1, aGraph);
		aGraph.addEdge(edge1);
		edge2.connect(node1, node2, aGraph);
		aGraph.addEdge(edge2);
		edge3.connect(node1, node3, aGraph);
		aGraph.addEdge(edge3);
		
		Point2D[] points = SegmentationStyleFactory.createStraightStrategy().getPath(edge1);
		assertEquals( 5, points.length );
		assertEquals( new Point2D(1080,1000), points[0]);
		assertEquals( new Point2D(1080,980), points[1]);
		assertEquals( new Point2D(1120,980), points[2]);
		assertEquals( new Point2D(1120,1020), points[3]);
		assertEquals( new Point2D(1100,1020), points[4]);
		
		points = SegmentationStyleFactory.createStraightStrategy().getPath(edge2);
		assertEquals( 2, points.length );
		assertEquals( 1026, points[0].getX(), 0.01);
		assertEquals( 1000, points[0].getY(), 0.01);
		assertEquals( 950, points[1].getX(), 0.01);
		assertEquals( 560, points[1].getY(), 0.01);
		
		points = SegmentationStyleFactory.createStraightStrategy().getPath(edge3);
		assertEquals( 2, points.length );
		assertEquals( 1052, points[0].getX(), 0.01);
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
		aGraph.addRootNode(node1);
		aGraph.addRootNode(node2);
		aGraph.addRootNode(node3);
		GeneralizationEdge edge1 = new GeneralizationEdge();
		GeneralizationEdge edge2 = new GeneralizationEdge();
		edge1.connect(node2, node1, aGraph);
		aGraph.addEdge(edge1);
		edge2.connect(node3, node1, aGraph);
		aGraph.addEdge(edge2);
		
		Point2D[] points = SegmentationStyleFactory.createVHVStrategy().getPath(edge1);
		assertEquals( 4, points.length );
		assertEquals( 950, points[0].getX(), 0.01);
		assertEquals( 500, points[0].getY(), 0.01);
		assertEquals( 950, points[1].getX(), 0.01);
		assertEquals( 280, points[1].getY(), 0.01);
		assertEquals( 1050, points[2].getX(), 0.01);
		assertEquals( 280, points[2].getY(), 0.01);
		assertEquals( 1050, points[3].getX(), 0.01);
		assertEquals( 60, points[3].getY(), 0.01);
		
		points = SegmentationStyleFactory.createVHVStrategy().getPath(edge2);
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
	
	/*
	 * Two generalization edges vertically oriented, separated by a dependency
	 * edge, and one more generalization edge of a different type.
	 */
	@Test
	public void testAggregateGeneralizationEdgesMixed()
	{
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		ClassNode node3 = new ClassNode();
		ClassNode node4 = new ClassNode();
		ClassNode node5 = new ClassNode();
		node1.translate(1000, 0);
		node2.translate(900, 500);
		node3.translate(1000, 1000);
		node4.translate(1100, 500);
		node5.translate(1500, 400);
		aGraph.addRootNode(node1);
		aGraph.addRootNode(node2);
		aGraph.addRootNode(node3);
		aGraph.addRootNode(node4);
		aGraph.addRootNode(node5);
		GeneralizationEdge edge1 = new GeneralizationEdge();
		DependencyEdge edge2 = new DependencyEdge();
		GeneralizationEdge edge3 = new GeneralizationEdge();
		GeneralizationEdge edge4 = new GeneralizationEdge();
		edge4.setType(Type.Implementation);
		edge1.connect(node2, node1, aGraph);
		aGraph.addEdge(edge1);
		edge2.connect(node3, node1, aGraph);
		aGraph.addEdge(edge2);
		edge3.connect(node4, node1, aGraph);
		aGraph.addEdge(edge3);
		edge4.connect(node5, node1, aGraph);
		aGraph.addEdge(edge4);
		
		Point2D[] points = SegmentationStyleFactory.createVHVStrategy().getPath(edge1);
		assertEquals( 4, points.length );
		assertEquals( 950, points[0].getX(), 0.01);
		assertEquals( 500, points[0].getY(), 0.01);
		assertEquals( 950, points[1].getX(), 0.01);
		assertEquals( 280, points[1].getY(), 0.01);
		assertEquals( 1039, points[2].getX(), 0.01);
		assertEquals( 280, points[2].getY(), 0.01);
		assertEquals( 1039, points[3].getX(), 0.01);
		assertEquals( 60, points[3].getY(), 0.01);
		
		points = SegmentationStyleFactory.createStraightStrategy().getPath(edge2);
		assertEquals( 2, points.length );
		assertEquals( 1050, points[0].getX(), 0.01);
		assertEquals( 1000, points[0].getY(), 0.01);
		assertEquals( 1050, points[1].getX(), 0.01);
		assertEquals( 60, points[1].getY(), 0.01);
		
		points = SegmentationStyleFactory.createVHVStrategy().getPath(edge3);
		assertEquals( 4, points.length );
		assertEquals( 1150, points[0].getX(), 0.01);
		assertEquals( 500, points[0].getY(), 0.01);
		assertEquals( 1150, points[1].getX(), 0.01);
		assertEquals( 280, points[1].getY(), 0.01);
		assertEquals( 1039, points[2].getX(), 0.01);
		assertEquals( 280, points[2].getY(), 0.01);
		assertEquals( 1039, points[3].getX(), 0.01);
		assertEquals( 60, points[3].getY(), 0.01);
		
		points = SegmentationStyleFactory.createVHVStrategy().getPath(edge4);
		assertEquals( 4, points.length );
		assertEquals( 1550, points[0].getX(), 0.01);
		assertEquals( 400, points[0].getY(), 0.01);
		assertEquals( 1550, points[1].getX(), 0.01);
		assertEquals( 230, points[1].getY(), 0.01);
		assertEquals( 1061, points[2].getX(), 0.01);
		assertEquals( 230, points[2].getY(), 0.01);
		assertEquals( 1061, points[3].getX(), 0.01);
		assertEquals( 60, points[3].getY(), 0.01);
	}
}
