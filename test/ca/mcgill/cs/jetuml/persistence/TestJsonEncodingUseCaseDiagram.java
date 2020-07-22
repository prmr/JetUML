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
package ca.mcgill.cs.jetuml.persistence;

import static ca.mcgill.cs.jetuml.persistence.PersistenceTestUtils.assertHasKeys;
import static ca.mcgill.cs.jetuml.persistence.PersistenceTestUtils.build;
import static ca.mcgill.cs.jetuml.persistence.PersistenceTestUtils.find;
import static ca.mcgill.cs.jetuml.persistence.PersistenceTestUtils.findEdge;
import static ca.mcgill.cs.jetuml.persistence.PersistenceTestUtils.findRootNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseAssociationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.UseCaseDependencyEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ActorNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.UseCaseNode;

public class TestJsonEncodingUseCaseDiagram
{
	private Diagram aGraph;
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setup()
	{
		aGraph = new Diagram(DiagramType.USECASE);
	}
	
	/*
	 * Initializes a simple graph with an actor and a use case node.
	 */
	private void initiGraph1()
	{
		ActorNode actor = new ActorNode();
		actor.setName("Mr. Bob");
		UseCaseNode useCase = new UseCaseNode();
		useCase.setName("Do it");
		
		aGraph.addRootNode(actor);
		aGraph.addRootNode(useCase);
		
		UseCaseAssociationEdge edge = new UseCaseAssociationEdge();
		edge.connect(actor, useCase, aGraph);
		aGraph.addEdge(edge);
	}
	
	/*
	 * Initializes a graph with two use cases
	 * with a extension dependency between them.
	 */
	private void initiGraph()
	{
		UseCaseNode node1 = new UseCaseNode();
		node1.setName("Node1");
		UseCaseNode node2 = new UseCaseNode();
		node2.setName("Node2");
		
		aGraph.addRootNode(node1);
		aGraph.addRootNode(node2);
		
		UseCaseDependencyEdge edge = new UseCaseDependencyEdge(UseCaseDependencyEdge.Type.Extend);
		edge.connect(node1, node2, aGraph);
		aGraph.addEdge(edge);
	}
	
	@Test
	public void testEmpty()
	{
		JSONObject object = JsonEncoder.encode(aGraph);
		assertHasKeys(object, "diagram", "nodes", "edges", "version");
		assertEquals("UseCaseDiagram", object.getString("diagram"));
		assertEquals(0, object.getJSONArray("nodes").length());	
		assertEquals(0, object.getJSONArray("edges").length());				
	}
	
	@Test
	public void testSingleNode()
	{
		aGraph.addRootNode(new NoteNode());
		
		JSONObject object = JsonEncoder.encode(aGraph);
		assertHasKeys(object, "diagram", "nodes", "edges", "version");
		assertEquals("UseCaseDiagram", object.getString("diagram"));
		assertEquals(1, object.getJSONArray("nodes").length());	
		assertEquals(0, object.getJSONArray("edges").length());	
		JSONObject node = object.getJSONArray("nodes").getJSONObject(0);
		assertHasKeys(node, "type", "id", "x", "y", "name");
		assertEquals(0, node.getInt("x"));
		assertEquals(0, node.getInt("y"));
		assertEquals("", node.getString("name"));
		assertEquals("NoteNode", node.getString("type"));
		assertEquals(0, node.getInt("id"));
	}
	
	@Test
	public void testEncodeGraph1()
	{
		initiGraph1();

		JSONObject object = JsonEncoder.encode(aGraph);
		
		assertHasKeys(object, "diagram", "nodes", "edges", "version");
		assertEquals("UseCaseDiagram", object.getString("diagram"));
		assertEquals(2, object.getJSONArray("nodes").length());	
		assertEquals(1, object.getJSONArray("edges").length());	
		
		JSONArray nodes = object.getJSONArray("nodes");
		JSONObject actor = find(nodes, build("type", "ActorNode", "name", "Mr. Bob"));
		JSONObject useCase = find(nodes, build("type", "UseCaseNode", "name", "Do it"));
				
		JSONArray edges = object.getJSONArray("edges");
		JSONObject edge1 = find(edges, build("type", "UseCaseAssociationEdge"));

		assertEquals(edge1.getInt("start"), actor.getInt("id"));
		assertEquals(edge1.getInt("end"), useCase.getInt("id"));
	}
	
	@Test
	public void testEncodeDecodeGraph1()
	{
		initiGraph1();
		Diagram graph = JsonDecoder.decode(JsonEncoder.encode(aGraph));
		
		ActorNode actor = (ActorNode) findRootNode(graph, ActorNode.class, build("name", "Mr. Bob"));
		UseCaseNode useCase = (UseCaseNode) findRootNode(graph, UseCaseNode.class, build("name", "Do it"));
		UseCaseAssociationEdge edge = (UseCaseAssociationEdge) findEdge(graph, UseCaseAssociationEdge.class, build());
		
		assertSame(edge.getStart(), actor);
		assertSame(edge.getEnd(), useCase);
	}
	
	@Test
	public void testEncodeDecodeGraph()
	{
		initiGraph();
		Diagram graph = JsonDecoder.decode(JsonEncoder.encode(aGraph));
		
		UseCaseNode node1 = (UseCaseNode) findRootNode(graph, UseCaseNode.class, build("name", "Node1"));
		UseCaseNode node2 = (UseCaseNode) findRootNode(graph, UseCaseNode.class, build("name", "Node2"));

		UseCaseDependencyEdge edge = (UseCaseDependencyEdge) findEdge(graph, UseCaseDependencyEdge.class, build("Dependency Type", UseCaseDependencyEdge.Type.Extend));
		
		assertSame(edge.getStart(), node1);
		assertSame(edge.getEnd(), node2);
	}
	

}
