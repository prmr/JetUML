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

public class TestObjectDiagramValidator
{
	private final ObjectDiagramValidator aValidator =
			new ObjectDiagramValidator(new Diagram(DiagramType.OBJECT));
	private final NoteNode aNoteNode = new NoteNode();
	private final ObjectNode aObject1 = new ObjectNode();
	private final ObjectNode aObject2 = new ObjectNode();
	private final FieldNode aField1 = new FieldNode();
	private final FieldNode aField2 = new FieldNode();
	
	private Diagram diagram()
	{
		return aValidator.diagram();
	}

	private static List<Node> provideInvalidNodes()
	{
		return List.of(new ActorNode(), new CallNode(), new ClassNode(), new FinalStateNode(), 
				new ImplicitParameterNode(), new InitialStateNode(), new InterfaceNode(), 
				new PackageDescriptionNode(), new PackageNode(), new StateNode(), new UseCaseNode());
	}
	
	private static List<Edge> provideInvalidEdges()
	{
		return List.of(new AggregationEdge(), new AssociationEdge(), new CallEdge(), new ConstructorEdge(), 
				new DependencyEdge(), new GeneralizationEdge(), new ReturnEdge(), new StateTransitionEdge(), 
				new UseCaseAssociationEdge(), new UseCaseDependencyEdge(), new UseCaseGeneralizationEdge());
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
	void testFieldAsRootNodes()
	{
		diagram().addRootNode(aField1);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testReferenceFromFieldToNote()
	{
		diagram().addRootNode(aObject1);
		diagram().addRootNode(aNoteNode);
		aObject1.addChild(aField1);
		Edge edge = new ObjectReferenceEdge();
		edge.connect(aField1, aNoteNode);
		diagram().addEdge(edge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testReferenceFromFieldToField()
	{
		diagram().addRootNode(aObject1);
		diagram().addRootNode(aObject2);
		aObject1.addChild(aField1);
		aObject2.addChild(aField2);
		Edge edge = new ObjectReferenceEdge();
		edge.connect(aField1, aField2);
		diagram().addEdge(edge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testReferenceFromObjectToObject()
	{
		diagram().addRootNode(aObject1);
		diagram().addRootNode(aObject2);
		Edge edge = new ObjectReferenceEdge();
		edge.connect(aObject1, aObject2);
		diagram().addEdge(edge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testReferenceFromObjectToField()
	{
		diagram().addRootNode(aObject1);
		diagram().addRootNode(aObject2);
		aObject1.addChild(aField1);
		Edge edge = new ObjectReferenceEdge();
		edge.connect(aObject2, aObject1);
		diagram().addEdge(edge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testCollaborationFromObjectToNote()
	{
		diagram().addRootNode(aObject1);
		diagram().addRootNode(aNoteNode);
		Edge edge = new ObjectCollaborationEdge();
		edge.connect(aObject1, aNoteNode);
		diagram().addEdge(edge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testCollaborationFromNoteToObject()
	{
		diagram().addRootNode(aObject1);
		diagram().addRootNode(aNoteNode);
		Edge edge = new ObjectCollaborationEdge();
		edge.connect(aNoteNode, aObject1 );
		diagram().addEdge(edge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testCollaborationFromFieldToObject()
	{
		diagram().addRootNode(aObject1);
		aObject1.addChild(aField1);
		diagram().addRootNode(aObject2);
		Edge edge = new ObjectCollaborationEdge();
		edge.connect(aField1, aObject2 );
		diagram().addEdge(edge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testCollaborationFromFieldToField()
	{
		diagram().addRootNode(aObject1);
		aObject1.addChild(aField1);
		diagram().addRootNode(aObject2);
		aObject2.addChild(aField2);
		Edge edge = new ObjectCollaborationEdge();
		edge.connect(aField1, aField2 );
		diagram().addEdge(edge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testCollaborationNoSelfEdge()
	{
		diagram().addRootNode(aObject1);
		Edge edge = new ObjectCollaborationEdge();
		edge.connect(aObject1, aObject1 );
		diagram().addEdge(edge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testReferenceNoSelfEdge()
	{
		diagram().addRootNode(aObject1);
		aObject1.addChild(aField1);
		Edge edge = new ObjectCollaborationEdge();
		edge.connect(aField1, aField1 );
		diagram().addEdge(edge);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testReferenceNoDuplicatedEdge()
	{
		diagram().addRootNode(aObject1);
		aObject1.addChild(aField1);
		diagram().addRootNode(aObject2);
		Edge edge = new ObjectReferenceEdge();
		edge.connect(aField1, aObject2 );
		diagram().addEdge(edge);
		Edge edge2 = new ObjectReferenceEdge();
		edge2.connect(aField1, aObject2 );
		diagram().addEdge(edge2);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testCollaborationNoDuplicatedEdge()
	{
		diagram().addRootNode(aObject1);
		diagram().addRootNode(aObject2);
		Edge edge = new ObjectCollaborationEdge();
		edge.connect(aObject1, aObject2 );
		diagram().addEdge(edge);
		Edge edge2 = new ObjectCollaborationEdge();
		edge2.connect(aObject1, aObject2 );
		diagram().addEdge(edge2);
		assertFalse(aValidator.isValid());
	}
	
	@Test
	void testCollaborationDirectCycle()
	{
		diagram().addRootNode(aObject1);
		diagram().addRootNode(aObject2);
		Edge edge = new ObjectCollaborationEdge();
		edge.connect(aObject1, aObject2 );
		diagram().addEdge(edge);
		Edge edge2 = new ObjectCollaborationEdge();
		edge2.connect(aObject2, aObject1 );
		diagram().addEdge(edge2);
		assertFalse(aValidator.isValid());
	}
}
