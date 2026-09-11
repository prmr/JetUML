/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2025 by McGill University.
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
package org.jetuml.diagram;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.jetuml.diagram.builder.ClassDiagramBuilder;
import org.jetuml.diagram.builder.DiagramOperationProcessor;
import org.jetuml.diagram.edges.AggregationEdge;
import org.jetuml.diagram.edges.AssociationEdge;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.GeneralizationEdge;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.InterfaceNode;
import org.jetuml.diagram.nodes.PackageNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.junit.jupiter.api.Test;

class TestUsageScenariosClassDiagram extends AbstractTestUsageScenarios {

	private ClassNode aClassNode1 = new ClassNode();
	private ClassNode aClassNode2 = new ClassNode();
	private InterfaceNode aInterfaceNode = new InterfaceNode();
	private PackageNode aPackageNode = new PackageNode();
	private AggregationEdge aAggregationEdge = new AggregationEdge();
	private AssociationEdge aAssociationEdge = new AssociationEdge();
	private DependencyEdge aDependencyEdge = new DependencyEdge();
	private GeneralizationEdge aGeneralizationEdge = new GeneralizationEdge();

	TestUsageScenariosClassDiagram() {
		super(new Diagram(DiagramType.CLASS));
	}

	@Test
	void testPasteIntoDifferentDiagram() {
		addNode(aClassNode1, new Point(25, 25));
		addNode(aClassNode2, new Point(30, 30));
		moveNode(aClassNode2, 100, 0);
		addEdge(aDependencyEdge, new Point(31, 31), new Point(131, 31));
		assertSame(aClassNode1, aDependencyEdge.start());
		assertSame(aClassNode2, aDependencyEdge.end());

		select(aClassNode1, aClassNode2, aDependencyEdge);
		copy();

		Diagram diagram2 = new Diagram(DiagramType.CLASS);
		ClassDiagramBuilder builder2 = new ClassDiagramBuilder(diagram2);
		DiagramOperationProcessor processor2 = new DiagramOperationProcessor(() -> {});
		processor2.executeNewOperation(builder2.createAddElementsOperation(getClipboardContent()));

		Iterator<Node> nodes = diagram2.rootNodes().iterator();
		Node node1 = nodes.next();
		Node node2 = nodes.next();
		Edge edge = diagram2.edges().iterator().next();
		assertSame(node1, edge.start());
		assertSame(node2, edge.end());
	}

	@Test
	void testClassDiagramCopyClassNodesAndEdgesInsidePackageNode() {
		addNode(aPackageNode, new Point(20, 20));
		addNode(aClassNode1, new Point(30, 30));
		addNode(aClassNode2, new Point(30, 30));
		moveNode(aClassNode2, 100, 0);
		addEdge(aDependencyEdge, new Point(31, 51), new Point(135, 31));

		assertEquals(1, numberOfRootNodes());
		assertEquals(2, aPackageNode.getChildren().size());
		assertSame(aClassNode1, aDependencyEdge.start());
		assertSame(aClassNode2, aDependencyEdge.end());

		select(aClassNode1, aClassNode2, aDependencyEdge);
		copy();
		paste();

		assertEquals(3, numberOfRootNodes());
		assertEquals(2, numberOfEdges());
	}

	@Test
	void testClassDiagramCopyPackageNodesAndEdgesInsidePackageNode() {
		addNode(aPackageNode, new Point(20, 20));
		addNode(aClassNode1, new Point(30, 30));
		addNode(aClassNode2, new Point(30, 30));
		moveNode(aClassNode2, 100, 0);
		addEdge(aDependencyEdge, new Point(31, 51), new Point(135, 31));

		assertEquals(1, numberOfRootNodes());
		assertEquals(2, aPackageNode.getChildren().size());
		assertEquals(1, numberOfEdges());
		assertSame(aClassNode1, aDependencyEdge.start());
		assertSame(aClassNode2, aDependencyEdge.end());

		// Does nothing
		select(aDependencyEdge);
		copy();
		paste();
		assertEquals(1, numberOfRootNodes());
		assertEquals(1, numberOfEdges());
	}

	@Test
	void testBasicNode() {
		addNode(aClassNode1, new Point(5, 5));
		addNode(aInterfaceNode, new Point(44, 44));
		addNode(aPackageNode, new Point(87, 87));
		addNode(aNoteNode, new Point(134, 132));

		assertEquals(4, numberOfRootNodes());

		setProperty(aClassNode1.properties().get(PropertyName.NAME), "truck");
		setProperty(aClassNode1.properties().get(PropertyName.METHODS), "setDriver()");
		setProperty(aInterfaceNode.properties().get(PropertyName.NAME), "vehicle");
		setProperty(aInterfaceNode.properties().get(PropertyName.METHODS), "getPrice()");
		setProperty(aPackageNode.properties().get(PropertyName.NAME), "object");
		setProperty(aNoteNode.properties().get(PropertyName.NAME), "some text...");

		assertEquals(aClassNode1.getName(), "truck");
		assertEquals(aClassNode1.getMethods(), "setDriver()");
		assertEquals(aInterfaceNode.getMethods(), "getPrice()");
		assertEquals(aPackageNode.getName(), "object");
		assertEquals(aNoteNode.getName(), "some text...");
	}

	@Test
	void testEdgeCreation() {
		addNode(aClassNode1, new Point(5, 5));
		addNode(aInterfaceNode, new Point(44, 44));
		addNode(aPackageNode, new Point(87, 87));
		addNode(aNoteNode, new Point(134, 132));

		addEdge(aAggregationEdge, new Point(10, 10), new Point(45, 48));
		addEdge(aDependencyEdge, new Point(90, 93), new Point(44, 49));
		// added 2 valid edges
		assertEquals(2, numberOfEdges());
		// the diagram is valid so far
		assertTrue(validator().isValid());

		// both start and end points are invalid
		addEdge(aAggregationEdge, new Point(70, 70), new Point(170, 170));
		// removal of the invalid edge is not automatically done in here
		// thus we do not check the number of edges after adding this invalid edge
		assertFalse(validator().isValid());

		// one point is invalid
		addEdge(aAggregationEdge, new Point(6, 7), new Point(170, 170));
		assertFalse(validator().isValid());

		// not every edge is a valid self-edge
		addEdge(aGeneralizationEdge, new Point(47, 49), new Point(50, 49));
		assertFalse(validator().isValid());

		// despite 3 out of 5 edges are invalid edges, they still exist in diagram
		assertEquals(5, numberOfEdges());
		assertFalse(validator().isValid());
	}

	@Test
	void testSelfEdgeCreation() {
		addNode(aInterfaceNode, new Point(44, 44));
		addEdge(aAssociationEdge, new Point(47, 49), new Point(50, 49));
		assertEquals(1, numberOfEdges());
		assertSame(aInterfaceNode, aAssociationEdge.start());
		assertSame(aInterfaceNode, aAssociationEdge.end());

		assertTrue(validator().isValid());
		addEdge(aGeneralizationEdge, new Point(47, 49), new Point(50, 49));

		// not every edge is a valid self-edge
		assertFalse(validator().isValid());
	}

	@Test
	void testNoteEdgeCreation() {
		addNode(aClassNode1, new Point(5, 5));
		addNode(aInterfaceNode, new Point(44, 44));
		addNode(aPackageNode, new Point(87, 87));
		addNode(aNoteNode, new Point(134, 132));

		NoteEdge noteEdge1 = new NoteEdge();
		NoteEdge noteEdge2 = new NoteEdge();

		// if begin with a non-NoteNode type, both point needs to be valid
		assertTrue(validator().isValid());
		addEdge(noteEdge1, new Point(9, 9), new Point(209, 162));
		assertFalse(validator().isValid());
		addEdge(noteEdge1, new Point(10, 10), new Point(139, 142));
		assertEquals(2, numberOfEdges());
		assertEquals(noteEdge1.start(), aClassNode1);
		assertEquals(noteEdge1.end(), aNoteNode);

		// if begin with a NoteNode, the end point can be anywhere
		addEdge(noteEdge2, new Point(138, 140), new Point(9, 9));
		assertEquals(noteEdge2.start(), aNoteNode);
		assertEquals(noteEdge2.end().getClass(), PointNode.class);
		assertEquals(3, numberOfEdges());
	}

	@Test
	void testSingleNodeMovement() {

		addNode(aClassNode1, new Point(5, 5));
		addNode(aInterfaceNode, new Point(44, 44));
		addNode(aPackageNode, new Point(87, 87));
		addNode(aNoteNode, new Point(134, 132));

		moveNode(aClassNode1, 5, 5);
		moveNode(aInterfaceNode, 11, 19);
		moveNode(aPackageNode, 32, -42);
		moveNode(aNoteNode, -5, 19);

		assertEquals(new Point(10, 10), aClassNode1.position());
		assertEquals(new Point(55, 63), aInterfaceNode.position());
		assertEquals(new Point(119, 45), aPackageNode.position());
		assertEquals(new Point(129, 151), aNoteNode.position());
	}

	@Test
	void testSelectionNodeAndEdges() {
		addNode(aClassNode1, new Point(5, 5));
		addNode(aInterfaceNode, new Point(44, 44));
		addNode(aPackageNode, new Point(87, 87));
		addNode(aNoteNode, new Point(134, 132));

		addEdge(aAggregationEdge, new Point(10, 10), new Point(45, 48));
		addEdge(aDependencyEdge, new Point(90, 93), new Point(44, 49));

		select(aClassNode1, aAggregationEdge, aInterfaceNode);
		moveSelection(10, 10);
		assertEquals(new Point(15, 15), aClassNode1.position());
		assertEquals(new Point(54, 54), aInterfaceNode.position());
		assertSame(aClassNode1, aAggregationEdge.start());
		assertSame(aInterfaceNode, aAggregationEdge.end());
	}

	@Test
	void testMoveNodeWithSelfEdge() {
		addNode(aClassNode1, new Point(5, 7));
		addEdge(aAggregationEdge, new Point(10, 10), new Point(12, 10));
		moveNode(aClassNode1, 10, 10);

		assertEquals(new Point(15, 17), aClassNode1.position());
		assertSame(aClassNode1, aAggregationEdge.start());
		assertSame(aClassNode1, aAggregationEdge.end());
	}

	@Test
	void testMoveNodeConnectWithAnotherNode() {

		addNode(aClassNode1, new Point(5, 5));
		addNode(aInterfaceNode, new Point(44, 44));
		addNode(aPackageNode, new Point(87, 87));
		addEdge(aAggregationEdge, new Point(10, 10), new Point(45, 48));
		addEdge(aDependencyEdge, new Point(90, 93), new Point(44, 49));

		moveNode(aClassNode1, 20, 20);
		assertSame(aClassNode1, aAggregationEdge.start());
		assertSame(aInterfaceNode, aAggregationEdge.end());

		moveNode(aInterfaceNode, -19, 45);
		assertSame(aPackageNode, aDependencyEdge.start());
		assertSame(aInterfaceNode, aDependencyEdge.end());
	}

	@Test
	void testDeleteSingleNode() {
		addNode(aClassNode1, new Point(5, 5));
		addNode(aInterfaceNode, new Point(44, 44));

		select(aClassNode1);
		deleteSelected();
		assertEquals(1, numberOfRootNodes());

		select(aInterfaceNode);
		deleteSelected();
		assertEquals(0, numberOfRootNodes());

		undo();
		assertEquals(1, numberOfRootNodes());
		undo();
		assertEquals(2, numberOfRootNodes());
	}

	@Test
	void testDeleteSingleEdge() {
		addNode(aClassNode1, new Point(5, 5));
		addNode(aInterfaceNode, new Point(44, 44));
		addEdge(aAggregationEdge, new Point(10, 10), new Point(45, 48));

		select(aAggregationEdge);
		deleteSelected();
		assertEquals(0, numberOfEdges());

		undo();
		assertEquals(1, numberOfEdges());
	}

	@Test
	void testMultipleEdgesSameTypeCanNotExist() {
		addNode(aClassNode1, new Point(5, 5));
		addNode(aInterfaceNode, new Point(44, 44));
		addEdge(aAggregationEdge, new Point(10, 10), new Point(45, 48));
		addEdge(aDependencyEdge, new Point(10, 10), new Point(45, 48));
		addEdge(aGeneralizationEdge, new Point(10, 10), new Point(45, 48));
		assertEquals(3, numberOfEdges());
		assertTrue(validator().isValid());

		addEdge(aAggregationEdge, new Point(9, 11), new Point(46, 49));
		addEdge(aAssociationEdge, new Point(9, 11), new Point(46, 49));
		addEdge(aDependencyEdge, new Point(9, 11), new Point(46, 49));
		addEdge(aGeneralizationEdge, new Point(9, 11), new Point(46, 49));
		assertEquals(7, numberOfEdges());
		assertFalse(validator().isValid());
	}

	@Test
	void testNewEdgesSameTypeDoNotReplaceOriginalEdges() {
		AggregationEdge aSecondAggregationEdge = new AggregationEdge();
		AssociationEdge aSecondAssociationEdge = new AssociationEdge();
		DependencyEdge aSecondDependencyEdge = new DependencyEdge();
		GeneralizationEdge aSecondGeneralizationEdge = new GeneralizationEdge();

		addNode(aClassNode1, new Point(5, 5));
		addNode(aInterfaceNode, new Point(44, 44));
		addEdge(aAggregationEdge, new Point(10, 10), new Point(45, 48));
		addEdge(aDependencyEdge, new Point(10, 10), new Point(45, 48));
		addEdge(aGeneralizationEdge, new Point(10, 10), new Point(45, 48));
		assertEquals(3, numberOfEdges());
		assertTrue(validator().isValid());

		// new invalid edges appear in diagram
		addEdge(aSecondAggregationEdge, new Point(5, 5), new Point(46, 49));
		addEdge(aSecondAssociationEdge, new Point(5, 5), new Point(46, 49));
		addEdge(aSecondDependencyEdge, new Point(5, 5), new Point(46, 49));
		addEdge(aSecondGeneralizationEdge, new Point(5, 5), new Point(46, 49));
		assertEquals(7, numberOfEdges());

		assertTrue(diagram().contains(aSecondAggregationEdge));
		assertTrue(diagram().contains(aSecondAssociationEdge));
		assertTrue(diagram().contains(aSecondDependencyEdge));
		assertTrue(diagram().contains(aSecondGeneralizationEdge));

		assertFalse(validator().isValid());
	}

	@Test
	void testDeleteNodeEdgeCombination1() {
		addNode(aClassNode1, new Point(5, 5));
		addNode(aInterfaceNode, new Point(44, 44));
		addEdge(aAggregationEdge, new Point(10, 10), new Point(45, 48));
		Rectangle classNodeBounds = builder().renderer().getBounds(aClassNode1);

		select(aClassNode1);
		deleteSelected();

		assertEquals(1, numberOfRootNodes());
		assertEquals(0, numberOfEdges());

		undo();
		assertEquals(1, numberOfEdges());
		assertEquals(2, numberOfRootNodes());
		assertEquals(classNodeBounds, builder().renderer().getBounds(getRootNode(1)));
	}

	@Test
	void testDeleteNodeEdgeCombination2() {
		addNode(aClassNode1, new Point(5, 5));
		addNode(aInterfaceNode, new Point(44, 44));
		addEdge(aAggregationEdge, new Point(10, 10), new Point(45, 48));
		Rectangle classNodeBounds = builder().renderer().getBounds(aClassNode1);
		Rectangle interfaceNodeBounds = builder().renderer().getBounds(aInterfaceNode);

		selectAll();
		deleteSelected();
		assertEquals(0, numberOfRootNodes());
		assertEquals(0, numberOfEdges());

		undo();
		assertEquals(1, numberOfEdges());
		assertEquals(2, numberOfRootNodes());
		for (Node node : diagram().rootNodes()) {
			if (node instanceof ClassNode) {
				assertEquals(classNodeBounds, builder().renderer().getBounds(node));
			}
			else {
				assertEquals(interfaceNodeBounds, builder().renderer().getBounds(node));
			}
		}
	}

	@Test
	void testDeleteNodeInsidePackageNode() {
		addNode(aPackageNode, new Point(5, 5));
		addNode(aClassNode1, new Point(6, 8));
		addNode(aClassNode2, new Point(11, 12));

		select(aClassNode2);
		deleteSelected();

		assertEquals(1, aPackageNode.getChildren().size());
		undo();
		assertEquals(2, aPackageNode.getChildren().size());
	}

	@Test
	void testDeleteNodeInsideNestedPackageNode() {
		PackageNode innerNode = new PackageNode();
		addNode(aPackageNode, new Point(5, 5));
		addNode(innerNode, new Point(10, 10));
		addNode(aClassNode1, new Point(15, 35));
		addNode(aClassNode2, new Point(20, 40));

		select(aClassNode2);
		deleteSelected();
		assertEquals(1, aPackageNode.getChildren().size());
		assertEquals(1, innerNode.getChildren().size());
		undo();
		assertEquals(1, aPackageNode.getChildren().size());
		assertEquals(2, innerNode.getChildren().size());
	}

	@Test
	void testCopyPasteSingleNode() {
		addNode(aClassNode1, new Point(5, 5));

		select(aClassNode1);
		copy();
		paste();

		assertEquals(2, numberOfRootNodes());
		assertEquals(new Point(5, 5), getRootNode(1).position());
	}

	@Test
	void testCutPasteSingleNode() {
		addNode(aClassNode1, new Point(5, 5));
		select(aClassNode1);
		cut();
		assertEquals(0, numberOfRootNodes());
		paste();
		assertEquals(1, numberOfRootNodes());
		assertEquals(new Point(5, 5), getRootNode(0).position());
	}

	@Test
	void testCopyPasteCombinationNodeAndEdge() {
		addNode(aClassNode1, new Point(5, 5));
		addNode(aInterfaceNode, new Point(45, 45));
		addEdge(aAggregationEdge, new Point(10, 10), new Point(45, 48));

		selectAll();
		copy();
		paste();
		assertEquals(4, numberOfRootNodes());
		assertEquals(2, numberOfEdges());

		undo();
		assertEquals(2, numberOfRootNodes());
		assertEquals(1, numberOfEdges());
	}

	@Test
	void testCutPasteCombinationNodeAndEdge() {
		addNode(aClassNode1, new Point(5, 5));
		addNode(aInterfaceNode, new Point(45, 45));
		addEdge(aAggregationEdge, new Point(10, 10), new Point(45, 48));

		selectAll();
		cut();
		paste();

		assertEquals(2, numberOfRootNodes());
		assertEquals(1, numberOfEdges());

		undo();
		assertEquals(0, numberOfRootNodes());
		assertEquals(0, numberOfEdges());

		undo();
		assertEquals(2, numberOfRootNodes());
		assertEquals(1, numberOfEdges());
	}

	@Test
	void testAddNodesToPackageNode() {
		addNode(aPackageNode, new Point(20, 20));
		addNode(aClassNode1, new Point(25, 25));
		addNode(aClassNode2, new Point(30, 30));
		addNode(new PackageNode(), new Point(35, 45));

		assertEquals(1, numberOfRootNodes());
		assertEquals(3, aPackageNode.getChildren().size());
	}

	@Test
	void testAddNodeToInnerPackageNode() {
		PackageNode innerNode = new PackageNode();
		addNode(aPackageNode, new Point(20, 20));
		addNode(innerNode, new Point(25, 25));
		addNode(aClassNode1, new Point(31, 55));
		addNode(aClassNode2, new Point(35, 60));

		assertEquals(1, numberOfRootNodes());
		assertEquals(1, aPackageNode.getChildren().size());
		assertEquals(2, innerNode.getChildren().size());
	}

	@Test
	void testCutNodesAndEdgesInsidePackageNode() {
		addNode(aPackageNode, new Point(20, 20));
		addNode(aClassNode1, new Point(25, 25));
		addNode(aClassNode2, new Point(30, 30));
		moveNode(aClassNode2, 100, 0);
		addEdge(aDependencyEdge, new Point(26, 26), new Point(131, 31));

		selectAll();
		cut();
		assertEquals(0, numberOfRootNodes());
		assertEquals(0, numberOfEdges());

		paste();
		assertEquals(1, numberOfRootNodes());
		assertEquals(1, numberOfEdges());
	}

	@Test
	void testCopyNodesAndEdgesInsidePackageNode() {
		addNode(aPackageNode, new Point(20, 20));
		addNode(aClassNode1, new Point(25, 25));
		addNode(aClassNode2, new Point(130, 30));
		addEdge(aDependencyEdge, new Point(26, 26), new Point(131, 31));

		selectAll();
		copy();
		paste();
		assertEquals(2, numberOfRootNodes());
		assertEquals(2, numberOfEdges());
	}
}
