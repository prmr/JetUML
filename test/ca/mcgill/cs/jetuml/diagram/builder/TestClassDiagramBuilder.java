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

package ca.mcgill.cs.jetuml.diagram.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.edges.GeneralizationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.InterfaceNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Point;

public class TestClassDiagramBuilder
{
	private Diagram aDiagram;
	private ClassDiagramBuilder aBuilder;
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setUp()
	{
		aDiagram = new Diagram(DiagramType.CLASS);
		aBuilder = new ClassDiagramBuilder(aDiagram);
	}
	
	private int numberOfRootNodes()
	{
		return aDiagram.rootNodes().size();
	}
	
	private int numberOfEdges()
	{
		return aDiagram.edges().size();
	}
	
	private Node getRootNode(int pIndex)
	{
		return aDiagram.rootNodes().get(pIndex);
	}
	
	@Test
	public void testcreateAddNodeOperationSimple()
	{
		ClassNode node = new ClassNode();
		DiagramOperation operation = aBuilder.createAddNodeOperation(node, new Point(10,10));
		assertEquals(0, numberOfRootNodes());
		operation.execute();
		assertEquals(1, numberOfRootNodes());
		assertTrue(aDiagram.contains(node));
		assertEquals(new Point(10,10), node.position());
		operation.undo();
		assertEquals(0, numberOfRootNodes());
	}
	
	@Test
	public void testcreateAddEdgeOperationNoteNode() // TODO: here
	{
		NoteNode node = new NoteNode();
		DiagramOperation operation = aBuilder.createAddNodeOperation(node, new Point(10,10));
		operation.execute();
		assertSame(aDiagram, node.getDiagram().get());
		assertEquals(1, numberOfRootNodes());
		assertTrue(aDiagram.contains(node));
		assertEquals(new Point(10,10), node.position());
		NoteEdge edge = new NoteEdge();
		aBuilder.createAddEdgeOperation(edge, new Point(11,11), new Point(100,100)).execute();
		assertEquals(1, aDiagram.edges().size());
		assertSame(node, edge.getStart());
		assertSame(aDiagram, node.getDiagram().get());
		PointNode end = (PointNode) edge.getEnd();
		assertSame(aDiagram, end.getDiagram().get());
	}
	
	@Test
	public void testcreateAddNodeOperationReposition()
	{
		ClassNode node = new ClassNode();
		aBuilder.setCanvasDimension(new Dimension(500,500));
		DiagramOperation operation = aBuilder.createAddNodeOperation(node, new Point(450,450));
		assertEquals(0, numberOfRootNodes());
		operation.execute();
		assertEquals(1, numberOfRootNodes());
		assertTrue(aDiagram.contains(node));
		assertEquals(new Point(400,440), node.position());
		operation.undo();
		assertEquals(0, numberOfRootNodes());
		operation.execute();
		assertEquals(1, numberOfRootNodes());
		assertTrue(aDiagram.contains(node));
		assertEquals(new Point(400,440), node.position());
		operation.undo();
		assertEquals(0, numberOfRootNodes());
	}
	
	/*
	 * Adding a node that can't be a child to the root
	 * of the diagram, so, no over any other node.
	 */
	@Test
	public void testCreateAddNodeOperationInvalidChildNotOverNode()
	{
		NoteNode node = new NoteNode();
		DiagramOperation operation = aBuilder.createAddNodeOperation(node, new Point(50,50));
		operation.execute();
		assertEquals(1, numberOfRootNodes());
		assertTrue(aDiagram.contains(node));
		assertEquals(new Point(50,50), node.position());
	}
	
	/*
	 * Adding a node that can't be a child over another node that
	 * can be a parent.
	 */
	@Test
	public void testCreateAddNodeOperationInvalidChildOverNode()
	{
		PackageNode node = new PackageNode();
		aDiagram.addRootNode(node);
		NoteNode node2 = new NoteNode();
		aBuilder.createAddNodeOperation(node2, new Point(20,20)).execute();
		assertEquals(2, numberOfRootNodes());
		assertTrue(aDiagram.contains(node2));
		assertEquals(new Point(20,20), node2.position());
	}
	
	@Test
	public void testCreateAddNodeOperationValidChildAddition()
	{
		PackageNode node = new PackageNode();
		aDiagram.addRootNode(node);
		InterfaceNode node2 = new InterfaceNode();
		DiagramOperation operation = aBuilder.createAddNodeOperation(node2, new Point(10,10));
		operation.execute();
		assertEquals(1, numberOfRootNodes());
		assertTrue(aDiagram.contains(node));
		assertEquals(new Point(0,0), node.position());
		
		assertEquals(1, node.getChildren().size());
		assertSame(node2, node.getChildren().get(0));
		assertEquals(new Point(10,30), node2.position());
		
		operation.undo();
		assertEquals(0, node.getChildren().size());
	}
	
	@Test
	public void testCreateAddNodeOperationValidSubChildAddition()
	{
		PackageNode bottom = new PackageNode();
		aDiagram.addRootNode(bottom);
		
		PackageNode middle = new PackageNode();
		aBuilder.createAddNodeOperation(middle, new Point(10,10)).execute();
		
		assertEquals(1, numberOfRootNodes());
		assertSame(bottom, getRootNode(0));
		assertEquals(1, bottom.getChildren().size());
		assertSame(middle, bottom.getChildren().get(0));
		
		InterfaceNode top = new InterfaceNode();
		aBuilder.createAddNodeOperation(top, new Point(20,40)).execute();
		assertEquals(1, numberOfRootNodes());
		assertSame(bottom, getRootNode(0));
		assertEquals(1, bottom.getChildren().size());
		assertSame(middle, bottom.getChildren().get(0));
		assertEquals(1, middle.getChildren().size());
		assertSame(top, middle.getChildren().get(0));
	}
	
	@Test
	public void testCanAddNode()
	{
		ClassNode node = new ClassNode();
		assertTrue(aBuilder.canAdd(node, new Point(1000,1000)));
	}
	
	@Test
	public void testCanAddEdgeNoFirstNode()
	{
		assertFalse(aBuilder.canAdd(new DependencyEdge(), new Point(10,10), new Point(20,20)));
	}
	
	@Test
	public void testCanAddEdgeFromNoteNode()
	{
		NoteNode node = new NoteNode();
		aDiagram.addRootNode(node);
		node.translate(10, 10);
		assertFalse(aBuilder.canAdd(new DependencyEdge(), new Point(15,15), new Point(100, 100)));
		assertTrue(aBuilder.canAdd(new NoteEdge(), new Point(15,15), new Point(100, 100)));
	}
	
	@Test
	public void testCanAddEdgeNoSecondNode()
	{
		ClassNode node1 = new ClassNode();
		node1.translate(10, 10);
		aDiagram.addRootNode(node1);
		assertFalse(aBuilder.canAdd(new DependencyEdge(), new Point(15,15), new Point(150, 150)));
	}
	
	@Test
	public void testCanAddEdgeAlreadyExists()
	{
		ClassNode node1 = new ClassNode();
		node1.translate(10, 10);
		ClassNode node2 = new ClassNode();
		node2.translate(200, 200);
		aDiagram.addRootNode(node1);
		aDiagram.addRootNode(node2);
		DependencyEdge edge = new DependencyEdge();
		edge.connect(node1, node2, aDiagram);
		aDiagram.addEdge(edge);
		assertFalse(aBuilder.canAdd(new DependencyEdge(), new Point(15,15), new Point(205, 205)));
	}
	
	@Test
	public void testCanAddEdgeFromNoteNodeNotNoteEdge()
	{
		NoteNode node = new NoteNode();
		NoteNode end = new NoteNode();
		end.moveTo(new Point(100,100));
		aDiagram.addRootNode(node);
		aDiagram.addRootNode(end);
		assertFalse(aBuilder.canAdd(new DependencyEdge(), new Point(15,15), new Point(105, 105)));
	}
	
	@Test
	public void testCanAddEdgeFromNoteNodeNotNoteEdge2()
	{
		NoteNode node = new NoteNode();
		ClassNode end = new ClassNode();
		end.moveTo(new Point(100,100));
		aDiagram.addRootNode(node);
		aDiagram.addRootNode(end);
		assertFalse(aBuilder.canAdd(new DependencyEdge(), new Point(15,15), new Point(105, 105)));
	}
	
	@Test
	public void testCanAddEdgeFromNoteNodeNotNoteEdge3()
	{
		ClassNode node = new ClassNode();
		NoteNode end = new NoteNode();
		end.moveTo(new Point(100,100));
		aDiagram.addRootNode(node);
		aDiagram.addRootNode(end);
		assertFalse(aBuilder.canAdd(new DependencyEdge(), new Point(15,15), new Point(105, 105)));
	}
	
	@Test
	public void testCanAddEdgeToNoteNodeNotNodeEdge()
	{
		NoteNode node = new NoteNode();
		aDiagram.addRootNode(node);
		assertFalse(aBuilder.canAdd(new DependencyEdge(), new Point(205, 205), new Point(15,15)));
	}
	
	@Test
	public void testCanAddEdgeSelfGeneralization()
	{
		ClassNode node = new ClassNode();
		aDiagram.addRootNode(node);
		assertFalse(aBuilder.canAdd(new GeneralizationEdge(), new Point(15, 15), new Point(15,15)));
	}
	
	@Test
	public void testCanAddEdgeNonSelfDependency()
	{
		ClassNode node1 = new ClassNode();
		aDiagram.addRootNode(node1);
		ClassNode node2 = new ClassNode();
		node2.translate(200, 200);
		aDiagram.addRootNode(node2);
		assertTrue(aBuilder.canAdd(new DependencyEdge(), new Point(15,15), new Point(205,205)));
	}
	
	@Test
	public void testCanAddNoteEdgeNoNoteNode()
	{
		ClassNode start = new ClassNode();
		ClassNode end = new ClassNode();
		end.moveTo(new Point(150, 150));
		aDiagram.addRootNode(start);
		aDiagram.addRootNode(end);
		assertFalse(aBuilder.canAdd(new NoteEdge(), new Point(5,5), new Point(155,155)));
	}
	
	@Test
	public void testCreateAddElementsOperationNothing()
	{
		DiagramOperation operation = aBuilder.createAddElementsOperation(new ArrayList<>());
		operation.execute();
		assertTrue(numberOfRootNodes() == 0);
		assertTrue(numberOfEdges() == 0);
		operation.undo();
		assertTrue(numberOfRootNodes() == 0);
		assertTrue(numberOfEdges() == 0);
	}
	
	@Test
	public void testCreateAddElementsOperationNodesAndEdges()
	{
		ArrayList<DiagramElement> elements = new ArrayList<>();
		ClassNode node1 = new ClassNode();
		node1.moveTo(new Point(10,10));
		ClassNode node2 = new ClassNode();
		node2.moveTo(new Point(100,100));
		DependencyEdge edge = new DependencyEdge();
		edge.connect(node1, node2, aDiagram);
		elements.addAll(Arrays.asList(new DiagramElement[]{edge, node1, node2}));
		
		DiagramOperation operation = aBuilder.createAddElementsOperation(elements);
		operation.execute();
		assertEquals(2, numberOfRootNodes());
		assertEquals(1, numberOfEdges());
		assertSame(node1, getRootNode(0));
		assertSame(node2, getRootNode(1));
		
		operation.undo();
		assertEquals(0, numberOfRootNodes());
		assertEquals(0, numberOfEdges());
	}
	
	@Test
	public void testCreateRemoveElementsOperationEmpty()
	{
		DiagramOperation operation = aBuilder.createRemoveElementsOperation(new ArrayList<>());
		operation.execute();
		assertEquals(0, numberOfRootNodes());
		operation.undo();
		assertEquals(0, numberOfRootNodes());
	}
	
	@Test
	public void testCreateRemoveElementsOperationSingleNode()
	{
		ClassNode node1 = new ClassNode();
		ClassNode node2 = new ClassNode();
		node2.moveTo(new Point(100,100));
		aDiagram.addRootNode(node1);
		aDiagram.addRootNode(node2);
		ArrayList<DiagramElement> selection = new ArrayList<>();
		selection.add(node1);
		DiagramOperation operation = aBuilder.createRemoveElementsOperation(selection);
		operation.execute();
		assertEquals(1, numberOfRootNodes());
		assertSame(node2, getRootNode(0));
		operation.undo();
		assertEquals(2, numberOfRootNodes());
	}
	
	@Test
	public void testCanAttachToPackageMultipleNodes()
	{
		ClassNode child1 = new ClassNode();
		InterfaceNode child2 = new InterfaceNode();
		PackageNode parent = new PackageNode();
		aDiagram.addRootNode(child1);
		aDiagram.addRootNode(child2);
		aDiagram.addRootNode(parent);
		assertTrue(aBuilder.canLinkToPackage(Arrays.asList(child1, child2)));
	}
	
	@Test
	public void testCanAttachToPackage_EmptyList()
	{
		assertFalse(aBuilder.canLinkToPackage(Arrays.asList()));
	}
	
	@Test
	public void testCanDetachFromPackage_EmptyList()
	{
		assertFalse(aBuilder.canUnlinkFromPackage(Arrays.asList()));
	}
	
	@Test
	public void testCanAttachToPackageNoNullParent()
	{
		ClassNode child = new ClassNode();
		PackageNode parent = new PackageNode();
		parent.addChild(child);
		aDiagram.addRootNode(parent);
		assertFalse(aBuilder.canLinkToPackage(Arrays.asList(child)));
	}
	
	@Test
	public void testCanAttachToPackageNoPackageToAttach()
	{
		ClassNode node1 = new ClassNode();
		PackageNode node2 = new PackageNode();
		node2.translate(5, 5);
		aDiagram.addRootNode(node1);
		aDiagram.addRootNode(node2);
		assertFalse(aBuilder.canLinkToPackage(Arrays.asList(node1)));
	}
	
	@Test
	public void testCanDetachFromPackageSimple()
	{
		ClassNode child = new ClassNode();
		PackageNode parent = new PackageNode();
		parent.addChild(child);
		aDiagram.addRootNode(parent);
		assertTrue(aBuilder.canUnlinkFromPackage(Arrays.asList(child)));
	}
	
	@Test
	public void testCanDetachFromPackageNullParent()
	{
		ClassNode node1 = new ClassNode();
		PackageNode node2 = new PackageNode();
		aDiagram.addRootNode(node1);
		aDiagram.addRootNode(node2);
		assertFalse(aBuilder.canUnlinkFromPackage(Arrays.asList(node1)));
	}
	
	@Test
	public void testCanDetachFromPackageNoSharedParent()
	{
		ClassNode child1 = new ClassNode();
		InterfaceNode child2 = new InterfaceNode();
		PackageNode parent1 = new PackageNode();
		PackageNode parent2 = new PackageNode();
		parent1.addChild(child1);
		parent2.addChild(child2);
		aDiagram.addRootNode(parent1);
		aDiagram.addRootNode(parent2);
		assertFalse(aBuilder.canUnlinkFromPackage(Arrays.asList(child1, child2)));
	}
	
	@Test
	public void testCreateAttachToPackageOperation()
	{
		ClassNode child = new ClassNode();
		PackageNode parent = new PackageNode();
		aDiagram.addRootNode(child);
		aDiagram.addRootNode(parent);
		List<Node> selection = Arrays.asList(child);
		assertTrue(aBuilder.canLinkToPackage(selection));
		
		DiagramOperation operation = aBuilder.createLinkToPackageOperation(selection);
		operation.execute();
		assertFalse(aDiagram.rootNodes().contains(child));
		assertTrue(parent.getChildren().contains(child));
		assertSame(parent, child.getParent());
		operation.undo();
		assertTrue(aDiagram.rootNodes().contains(child));
		assertFalse(parent.getChildren().contains(child));
		assertFalse(child.hasParent());
	}
	
	@Test
	public void testCreateDetachFromPackageOperationSimple()
	{
		ClassNode child = new ClassNode();
		PackageNode parent = new PackageNode();
		parent.addChild(child);
		aDiagram.addRootNode(parent);
		List<Node> selection = Arrays.asList(child);
		assertTrue(aBuilder.canUnlinkFromPackage(selection));
		
		DiagramOperation operation = aBuilder.createUnlinkFromPackageOperation(selection);
		operation.execute();
		assertTrue(aDiagram.rootNodes().contains(child));
		assertFalse(parent.getChildren().contains(child));
		assertFalse(child.hasParent());
		operation.undo();
		assertFalse(aDiagram.rootNodes().contains(child));
		assertTrue(parent.getChildren().contains(child));
		assertSame(parent, child.getParent());
	}
	
	@Test
	public void testCreateDetachFromPackageOperationWithOuterParent()
	{
		ClassNode child = new ClassNode();
		PackageNode innerParent = new PackageNode();
		PackageNode outerParent = new PackageNode();
		innerParent.addChild(child);
		outerParent.addChild(innerParent);
		aDiagram.addRootNode(outerParent);
		List<Node> selection = Arrays.asList(child);
		assertTrue(aBuilder.canUnlinkFromPackage(selection));
		
		DiagramOperation operation = aBuilder.createUnlinkFromPackageOperation(selection);
		operation.execute();
		assertTrue(outerParent.getChildren().contains(child));
		assertFalse(innerParent.getChildren().contains(child));
		assertSame(outerParent, child.getParent());
		operation.undo();
		assertFalse(outerParent.getChildren().contains(child));
		assertTrue(innerParent.getChildren().contains(child));
		assertSame(innerParent, child.getParent());
	}
}
