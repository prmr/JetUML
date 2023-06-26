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
package org.jetuml.diagram.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.AggregationEdge;
import org.jetuml.diagram.edges.AssociationEdge;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.GeneralizationEdge;
import org.jetuml.diagram.edges.ObjectCollaborationEdge;
import org.jetuml.diagram.edges.ObjectReferenceEdge;
import org.jetuml.diagram.edges.ReturnEdge;
import org.jetuml.diagram.edges.StateTransitionEdge;
import org.jetuml.diagram.edges.UseCaseAssociationEdge;
import org.jetuml.diagram.edges.UseCaseDependencyEdge;
import org.jetuml.diagram.edges.UseCaseGeneralizationEdge;
import org.jetuml.diagram.nodes.ActorNode;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.FieldNode;
import org.jetuml.diagram.nodes.FinalStateNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.diagram.nodes.InitialStateNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.jetuml.diagram.nodes.StateNode;
import org.jetuml.diagram.nodes.UseCaseNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class TestClassDiagramValidator
{
	private final ClassDiagramValidator aValidator =
			new ClassDiagramValidator(new Diagram(DiagramType.CLASS));
	private final NoteNode aNoteNode = new NoteNode();
	private final ClassNode aClassNode1 = new ClassNode();
	private final ClassNode aClassNode2 = new ClassNode();
	private final ClassNode aClassNode3 = new ClassNode();
	private final DependencyEdge aDependencyEdge1 = new DependencyEdge();
	private final DependencyEdge aDependencyEdge2 = new DependencyEdge();
	
	private Diagram diagram()
	{
		return aValidator.diagram();
	}

	private static List<Node> provideInvalidNodes()
	{
		return List.of(new ActorNode(), new CallNode(), new FieldNode(), new FinalStateNode(), 
				new ImplicitParameterNode(), new InitialStateNode(), new ObjectNode(), new StateNode(), 
				new UseCaseNode());
	}
	
	private static List<Edge> provideInvalidEdges()
	{
		return List.of(new CallEdge(), new ObjectCollaborationEdge(), new ObjectReferenceEdge(), 
				new ReturnEdge(), new StateTransitionEdge(), new UseCaseAssociationEdge(),
				new UseCaseDependencyEdge(), new UseCaseGeneralizationEdge());
	}
	
	@ParameterizedTest
	@MethodSource("provideInvalidNodes")
	void testInvalidElement_Node(Node pNode)
	{
		diagram().addRootNode(pNode);
		assertFalse(aValidator.isValid());
	}
	
	@ParameterizedTest
	@MethodSource("provideInvalidEdges")
	void testInvalidElement_Edge(Edge pEdge)
	{
		pEdge.connect(aNoteNode, aNoteNode);
		diagram().addEdge(pEdge);
		diagram().addRootNode(aNoteNode);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testTwoEdgesBetweenNodesSameDirection()
	{
		diagram().addRootNode(aClassNode1);
		diagram().addRootNode(aClassNode2);
		aDependencyEdge1.connect(aClassNode1, aClassNode2);
		aDependencyEdge2.connect(aClassNode1, aClassNode2);
		diagram().addEdge(aDependencyEdge1);
		diagram().addEdge(aDependencyEdge2);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testTwoEdgesBetweenNodesOppositeDirection()
	{
		diagram().addRootNode(aClassNode1);
		diagram().addRootNode(aClassNode2);
		aDependencyEdge1.connect(aClassNode1, aClassNode2);
		aDependencyEdge2.connect(aClassNode2, aClassNode1);
		diagram().addEdge(aDependencyEdge1);
		diagram().addEdge(aDependencyEdge2);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testSameStartNodeDifferentEndNodes()
	{
		diagram().addRootNode(aClassNode1);
		diagram().addRootNode(aClassNode2);
		diagram().addRootNode(aClassNode3);
		aDependencyEdge1.connect(aClassNode1, aClassNode2);
		aDependencyEdge2.connect(aClassNode1, aClassNode3);
		diagram().addEdge(aDependencyEdge1);
		diagram().addEdge(aDependencyEdge2);
		assertTrue(aValidator.isValid());
	}
	
	@Test
	void testSelfGeneralization()
	{
		GeneralizationEdge edge = new GeneralizationEdge();
		diagram().addRootNode(aClassNode1);
		edge.connect(aClassNode1, aClassNode1);
		diagram().addEdge(edge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testNoSelfDependency()
	{
		DependencyEdge edge = new DependencyEdge();
		diagram().addRootNode(aClassNode1);
		edge.connect(aClassNode1, aClassNode1);
		diagram().addEdge(edge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testDirectCycleGeneralizationEdge()
	{
		diagram().addRootNode(aClassNode1);
		diagram().addRootNode(aClassNode2);
		Edge edge1 = new GeneralizationEdge();
		edge1.connect(aClassNode1, aClassNode2);
		diagram().addEdge(edge1);
		Edge edge2 = new GeneralizationEdge();
		edge2.connect(aClassNode2, aClassNode1);
		diagram().addEdge(edge2);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testDirectCycleDependencyEdge()
	{
		diagram().addRootNode(aClassNode1);
		diagram().addRootNode(aClassNode2);
		Edge edge1 = new DependencyEdge();
		edge1.connect(aClassNode1, aClassNode2);
		diagram().addEdge(edge1);
		Edge edge2 = new DependencyEdge();
		edge2.connect(aClassNode2, aClassNode1);
		diagram().addEdge(edge2);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testDirectCycleAggregationEdge()
	{
		diagram().addRootNode(aClassNode1);
		diagram().addRootNode(aClassNode2);
		Edge edge1 = new AggregationEdge();
		edge1.connect(aClassNode1, aClassNode2);
		diagram().addEdge(edge1);
		Edge edge2 = new AggregationEdge();
		edge2.connect(aClassNode2, aClassNode1);
		diagram().addEdge(edge2);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testDirectCycleAssociationEdge()
	{
		diagram().addRootNode(aClassNode1);
		diagram().addRootNode(aClassNode2);
		Edge edge1 = new AssociationEdge();
		edge1.connect(aClassNode1, aClassNode2);
		diagram().addEdge(edge1);
		Edge edge2 = new AssociationEdge();
		edge2.connect(aClassNode2, aClassNode1);
		diagram().addEdge(edge2);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testNoDirectCycle()
	{
		diagram().addRootNode(aClassNode1);
		diagram().addRootNode(aClassNode2);
		Edge edge1 = new DependencyEdge();
		Edge edge2 = new GeneralizationEdge();
		edge1.connect(aClassNode1, aClassNode2);
		edge2.connect(aClassNode2, aClassNode1);
		diagram().addEdge(edge1);
		diagram().addEdge(edge2);
		assertTrue(aValidator.isValid());
	}
	
	@Test
	void testCombinedAssociationAggregationSameDirection()
	{
		diagram().addRootNode(aClassNode1);
		diagram().addRootNode(aClassNode2);
		Edge edge1 = new AggregationEdge();
		Edge edge2 = new AssociationEdge();
		edge1.connect(aClassNode1, aClassNode2);
		edge2.connect(aClassNode1, aClassNode2);
		diagram().addEdge(edge1);
		diagram().addEdge(edge2);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testCombinedAssociationAggregationDifferentDirection()
	{
		diagram().addRootNode(aClassNode1);
		diagram().addRootNode(aClassNode2);
		Edge edge1 = new AggregationEdge();
		Edge edge2 = new AssociationEdge();
		edge1.connect(aClassNode1, aClassNode2);
		edge2.connect(aClassNode2, aClassNode1);
		diagram().addEdge(edge1);
		diagram().addEdge(edge2);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testNoCombinedAssociationAggregationDirection()
	{
		diagram().addRootNode(aClassNode1);
		diagram().addRootNode(aClassNode2);
		Edge edge1 = new GeneralizationEdge();
		Edge edge2 = new AssociationEdge();
		edge1.connect(aClassNode1, aClassNode2);
		edge2.connect(aClassNode1, aClassNode2);
		diagram().addEdge(edge1);
		diagram().addEdge(edge2);
		assertTrue(aValidator.isValid());
	}
}
