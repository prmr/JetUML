/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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
package org.jetuml.persistence;

import static org.jetuml.persistence.PersistenceTestUtils.assertHasKeys;
import static org.jetuml.persistence.PersistenceTestUtils.build;
import static org.jetuml.persistence.PersistenceTestUtils.find;
import static org.jetuml.persistence.PersistenceTestUtils.findEdge;
import static org.jetuml.persistence.PersistenceTestUtils.findRootNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.jetuml.JavaFXLoader;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.PropertyName;
import org.jetuml.diagram.edges.UseCaseAssociationEdge;
import org.jetuml.diagram.edges.UseCaseDependencyEdge;
import org.jetuml.diagram.nodes.ActorNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.UseCaseNode;
import org.jetuml.persistence.json.JsonArray;
import org.jetuml.persistence.json.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestJsonEncodingUseCaseDiagram
{
	private Diagram aDiagram;
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setup()
	{
		aDiagram = new Diagram(DiagramType.USECASE);
	}
	
	/*
	 * Initializes a simple graph with an actor and a use case node.
	 */
	private void initDiagram1()
	{
		ActorNode actor = new ActorNode();
		actor.setName("Mr. Bob");
		UseCaseNode useCase = new UseCaseNode();
		useCase.setName("Do it");
		
		aDiagram.addRootNode(actor);
		aDiagram.addRootNode(useCase);
		
		UseCaseAssociationEdge edge = new UseCaseAssociationEdge();
		edge.connect(actor, useCase);
		aDiagram.addEdge(edge);
	}
	
	/*
	 * Initializes a graph with two use cases
	 * with a extension dependency between them.
	 */
	private void initDiagram()
	{
		UseCaseNode node1 = new UseCaseNode();
		node1.setName("Node1");
		UseCaseNode node2 = new UseCaseNode();
		node2.setName("Node2");
		
		aDiagram.addRootNode(node1);
		aDiagram.addRootNode(node2);
		
		UseCaseDependencyEdge edge = new UseCaseDependencyEdge(UseCaseDependencyEdge.Type.Extend);
		edge.connect(node1, node2);
		aDiagram.addEdge(edge);
	}
	
	@Test
	public void testEmpty()
	{
		JsonObject object = JsonEncoder.encode(aDiagram);
		assertHasKeys(object, "diagram", "nodes", "edges", "version");
		assertEquals("UseCaseDiagram", object.getString("diagram"));
		assertEquals(0, object.getJsonArray("nodes").size());	
		assertEquals(0, object.getJsonArray("edges").size());				
	}
	
	@Test
	public void testSingleNode()
	{
		aDiagram.addRootNode(new NoteNode());
		
		JsonObject object = JsonEncoder.encode(aDiagram);
		assertHasKeys(object, "diagram", "nodes", "edges", "version");
		assertEquals("UseCaseDiagram", object.getString("diagram"));
		assertEquals(1, object.getJsonArray("nodes").size());	
		assertEquals(0, object.getJsonArray("edges").size());	
		JsonObject node = object.getJsonArray("nodes").getJsonObject(0);
		assertHasKeys(node, "type", "id", "x", "y", "name");
		assertEquals(0, node.getInt("x"));
		assertEquals(0, node.getInt("y"));
		assertEquals("", node.getString("name"));
		assertEquals("NoteNode", node.getString("type"));
		assertEquals(0, node.getInt("id"));
	}
	
	@Test
	public void testEncode()
	{
		initDiagram1();

		JsonObject object = JsonEncoder.encode(aDiagram);
		
		assertHasKeys(object, "diagram", "nodes", "edges", "version");
		assertEquals("UseCaseDiagram", object.getString("diagram"));
		assertEquals(2, object.getJsonArray("nodes").size());	
		assertEquals(1, object.getJsonArray("edges").size());	
		
		JsonArray nodes = object.getJsonArray("nodes");
		JsonObject actor = find(nodes, "ActorNode", build(PropertyName.NAME, "Mr. Bob"));
		JsonObject useCase = find(nodes, "UseCaseNode", build(PropertyName.NAME, "Do it"));
				
		JsonArray edges = object.getJsonArray("edges");
		JsonObject edge1 = find(edges, "UseCaseAssociationEdge", build());

		assertEquals(edge1.getInt("start"), actor.getInt("id"));
		assertEquals(edge1.getInt("end"), useCase.getInt("id"));
	}
	
	@Test
	public void testEncodeDecode1()
	{
		initDiagram1();
		Diagram graph = new JsonDecoder(JsonEncoder.encode(aDiagram)).decode();
		
		ActorNode actor = (ActorNode) findRootNode(graph, ActorNode.class, build(PropertyName.NAME, "Mr. Bob"));
		UseCaseNode useCase = (UseCaseNode) findRootNode(graph, UseCaseNode.class, build(PropertyName.NAME, "Do it"));
		UseCaseAssociationEdge edge = (UseCaseAssociationEdge) findEdge(graph, UseCaseAssociationEdge.class, build());
		
		assertSame(edge.start(), actor);
		assertSame(edge.end(), useCase);
	}
	
	@Test
	public void testEncodeDecode2()
	{
		initDiagram();
		Diagram graph = new JsonDecoder(JsonEncoder.encode(aDiagram)).decode();
		
		UseCaseNode node1 = (UseCaseNode) findRootNode(graph, UseCaseNode.class, build(PropertyName.NAME, "Node1"));
		UseCaseNode node2 = (UseCaseNode) findRootNode(graph, UseCaseNode.class, build(PropertyName.NAME, "Node2"));

		UseCaseDependencyEdge edge = (UseCaseDependencyEdge) findEdge(graph, UseCaseDependencyEdge.class, build(PropertyName.USE_CASE_DEPENDENCY_TYPE, UseCaseDependencyEdge.Type.Extend));
		
		assertSame(edge.start(), node1);
		assertSame(edge.end(), node2);
	}
	

}
