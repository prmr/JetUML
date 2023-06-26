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
import org.jetuml.diagram.edges.ConstructorEdge;
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
import org.jetuml.diagram.nodes.InterfaceNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.jetuml.diagram.nodes.PackageDescriptionNode;
import org.jetuml.diagram.nodes.PackageNode;
import org.jetuml.diagram.nodes.StateNode;
import org.jetuml.diagram.nodes.UseCaseNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class TestStateDiagramValidator
{
	private final StateDiagramValidator aValidator =
			new StateDiagramValidator(new Diagram(DiagramType.STATE));
	private final NoteNode aNoteNode = new NoteNode();
	private final InitialStateNode aStart = new InitialStateNode();
	private final FinalStateNode aEnd = new FinalStateNode();
	private final StateNode aState1 = new StateNode();
	private final StateNode aState2 = new StateNode();
	private final StateTransitionEdge aEdge1 = new StateTransitionEdge();
	
	private Diagram diagram()
	{
		return aValidator.diagram();
	}

	private static List<Node> provideInvalidNodes()
	{
		return List.of(new ActorNode(), new CallNode(), new ClassNode(), new FieldNode(), 
				new ImplicitParameterNode(), new InterfaceNode(), new ObjectNode(),
				new PackageDescriptionNode(), new PackageNode(), new UseCaseNode());
	}
	
	private static List<Edge> provideInvalidEdges()
	{
		return List.of(new AggregationEdge(), new AssociationEdge(), new CallEdge(), new ConstructorEdge(), 
				new DependencyEdge(), new GeneralizationEdge(), new ObjectCollaborationEdge(), 
				new ObjectReferenceEdge(), new ReturnEdge(), new UseCaseAssociationEdge(), 
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
	void testEdgeToNote()
	{
		diagram().addRootNode(aState1);
		diagram().addRootNode(aNoteNode);
		aEdge1.connect(aState1, aNoteNode);
		diagram().addEdge(aEdge1);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testEdgeFromNote()
	{
		diagram().addRootNode(aState1);
		diagram().addRootNode(aNoteNode);
		aEdge1.connect(aNoteNode, aState1);
		diagram().addEdge(aEdge1);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testEdgeFromFinalNode()
	{
		diagram().addRootNode(aState1);
		diagram().addRootNode(aEnd);
		aEdge1.connect(aEnd, aState1);
		diagram().addEdge(aEdge1);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testEdgeToStartNode()
	{
		diagram().addRootNode(aState1);
		diagram().addRootNode(aStart);
		aEdge1.connect(aState1, aStart);
		diagram().addEdge(aEdge1);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testEdgeBetweenStateNodes()
	{
		diagram().addRootNode(aState1);
		diagram().addRootNode(aState2);
		aEdge1.connect(aState1, aState2);
		diagram().addEdge(aEdge1);
		assertTrue(aValidator.isValid());
	}
	
	@Test
	void testSelfEdge_Valid()
	{
		diagram().addRootNode(aState1);
		aEdge1.connect(aState1, aState1);
		diagram().addEdge(aEdge1);
		assertTrue(aValidator.isValid());
	}
}
