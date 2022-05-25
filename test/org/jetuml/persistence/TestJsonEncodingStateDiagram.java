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
import org.jetuml.diagram.edges.StateTransitionEdge;
import org.jetuml.diagram.nodes.FinalStateNode;
import org.jetuml.diagram.nodes.InitialStateNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.StateNode;
import org.jetuml.geom.Point;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestJsonEncodingStateDiagram
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
		aGraph = new Diagram(DiagramType.STATE);
	}
	
	/*
	 * Initializes a simple graph with a start and end node,
	 * two state nodes, and individual transitions between them.
	 */
	private void initiGraph1()
	{
		StateNode node1 = new StateNode();
		node1.setName("Start");
		StateNode node2 = new StateNode();
		node2.setName("End");
		InitialStateNode start = new InitialStateNode();
		FinalStateNode end = new FinalStateNode();
		StateTransitionEdge edge1 = new StateTransitionEdge();
		edge1.setMiddleLabel("edge1");
		StateTransitionEdge edge2 = new StateTransitionEdge();
		edge2.setMiddleLabel("edge2");
		StateTransitionEdge edge3 = new StateTransitionEdge();
		edge3.setMiddleLabel("edge3");
		aGraph.addRootNode(node1);
		aGraph.addRootNode(node2);
		aGraph.addRootNode(start);
		aGraph.addRootNode(end);
		edge1.connect(start, node1, aGraph);
		aGraph.addEdge(edge1);
		edge2.connect(node1, node2, aGraph);
		aGraph.addEdge(edge2);
		edge3.connect(node2, end, aGraph);
		aGraph.addEdge(edge3);
	}
	
	/*
	 * Initializes a graph with a single node at position 10,20
	 */
	private void initiGraph3()
	{
		StateNode node1 = new StateNode();
		node1.setName("Node1");
		StateNode node2 = new StateNode();
		node2.setName("Node2");

		StateTransitionEdge self1 = new StateTransitionEdge();
		self1.setMiddleLabel("self1");
		StateTransitionEdge self2 = new StateTransitionEdge();
		self2.setMiddleLabel("self2");
		StateTransitionEdge edge1 = new StateTransitionEdge();
		edge1.setMiddleLabel("edge1");
		StateTransitionEdge edge2 = new StateTransitionEdge();
		edge2.setMiddleLabel("edge2");
		
		aGraph.addRootNode(node1);
		aGraph.addRootNode(node2);
		self1.connect(node1, node1, aGraph);
		aGraph.addEdge(self1);
		self2.connect(node1, node1, aGraph);
		aGraph.addEdge(self2);
		edge1.connect(node1, node2, aGraph);
		aGraph.addEdge(edge1);
		edge2.connect(node1, node2, aGraph);
		aGraph.addEdge(edge2);
	}
	
	/*
	 * Initializes a graph with a node with two self-edges,
	 * and two transitions to a second node.
	 */
	private void initiGraph()
	{
		StateNode node1 = new StateNode();
		node1.setName("The Node");
		node1.moveTo(new Point(10,20));
		aGraph.addRootNode(node1);
	}
	
	@Test
	public void testEmpty()
	{
		JSONObject object = JsonEncoder.encode(aGraph);
		assertHasKeys(object, "diagram", "nodes", "edges", "version");
		assertEquals("StateDiagram", object.getString("diagram"));
		assertEquals(0, object.getJSONArray("nodes").length());	
		assertEquals(0, object.getJSONArray("edges").length());				
	}
	
	@Test
	public void testSingleNode()
	{
		aGraph.addRootNode(new NoteNode());
		
		JSONObject object = JsonEncoder.encode(aGraph);
		assertHasKeys(object, "diagram", "nodes", "edges", "version");
		assertEquals("StateDiagram", object.getString("diagram"));
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
		assertEquals("StateDiagram", object.getString("diagram"));
		assertEquals(4, object.getJSONArray("nodes").length());	
		assertEquals(3, object.getJSONArray("edges").length());	
		
		JSONArray nodes = object.getJSONArray("nodes");
		JSONObject node1b = find(nodes, "StateNode", build(PropertyName.NAME, "Start"));
		JSONObject node2b = find(nodes, "StateNode", build(PropertyName.NAME, "End"));
		JSONObject startb = find(nodes, "InitialStateNode", build());
		JSONObject endb = find(nodes, "FinalStateNode", build());
		
		JSONArray edges = object.getJSONArray("edges");
		JSONObject edge1b = find(edges, "StateTransitionEdge", build(PropertyName.MIDDLE_LABEL, "edge1"));
		JSONObject edge2b = find(edges, "StateTransitionEdge", build(PropertyName.MIDDLE_LABEL, "edge2"));
		JSONObject edge3b = find(edges, "StateTransitionEdge", build(PropertyName.MIDDLE_LABEL, "edge3"));

		assertEquals(edge1b.getInt("start"), startb.getInt("id"));
		assertEquals(edge1b.getInt("end"), node1b.getInt("id"));
		assertEquals(edge2b.getInt("start"), node1b.getInt("id"));
		assertEquals(edge2b.getInt("end"), node2b.getInt("id"));
		assertEquals(edge3b.getInt("start"), node2b.getInt("id"));
		assertEquals(edge3b.getInt("end"), endb.getInt("id"));
	}
	
	@Test
	public void testEncodeDecodeGraph1()
	{
		initiGraph1();
		Diagram graph = JsonDecoder.decode(JsonEncoder.encode(aGraph));
		
		StateNode node1 = (StateNode) findRootNode(graph, StateNode.class, build(PropertyName.NAME, "Start"));
		StateNode node2 = (StateNode) findRootNode(graph, StateNode.class, build(PropertyName.NAME, "End"));
		InitialStateNode start = (InitialStateNode) findRootNode(graph, InitialStateNode.class, build());
		FinalStateNode end = (FinalStateNode) findRootNode(graph, FinalStateNode.class, build());
		StateTransitionEdge edge1 = (StateTransitionEdge) findEdge(graph, StateTransitionEdge.class, build(PropertyName.MIDDLE_LABEL, "edge1"));
		StateTransitionEdge edge2 = (StateTransitionEdge) findEdge(graph, StateTransitionEdge.class, build(PropertyName.MIDDLE_LABEL, "edge2"));
		StateTransitionEdge edge3 = (StateTransitionEdge) findEdge(graph, StateTransitionEdge.class, build(PropertyName.MIDDLE_LABEL, "edge3"));
		
		assertSame(edge1.getStart(), start);
		assertSame(edge1.getEnd(), node1);
		assertSame(edge2.getStart(), node1);
		assertSame(edge2.getEnd(), node2);
		assertSame(edge3.getStart(), node2);
		assertSame(edge3.getEnd(), end);
	}
	
	@Test
	public void testEncodeDecodeGraph()
	{
		initiGraph();
		Diagram graph = JsonDecoder.decode(JsonEncoder.encode(aGraph));
		
		StateNode node1 = (StateNode) findRootNode(graph, StateNode.class, build(PropertyName.NAME, "The Node"));
		assertEquals(new Point(10,20), node1.position());
		assertEquals("The Node", node1.getName());
	}
	
	@Test
	public void testEncodeDecodeGraph3()
	{
		initiGraph3();
		Diagram graph = JsonDecoder.decode(JsonEncoder.encode(aGraph));
		
		StateNode node1 = (StateNode) findRootNode(graph, StateNode.class, build(PropertyName.NAME, "Node1"));
		StateNode node2 = (StateNode) findRootNode(graph, StateNode.class, build(PropertyName.NAME, "Node2"));
		StateTransitionEdge self1 = (StateTransitionEdge) findEdge(graph, StateTransitionEdge.class, build(PropertyName.MIDDLE_LABEL, "self1"));
		StateTransitionEdge self2 = (StateTransitionEdge) findEdge(graph, StateTransitionEdge.class, build(PropertyName.MIDDLE_LABEL, "self2"));
		StateTransitionEdge edge1 = (StateTransitionEdge) findEdge(graph, StateTransitionEdge.class, build(PropertyName.MIDDLE_LABEL, "edge1"));
		StateTransitionEdge edge2 = (StateTransitionEdge) findEdge(graph, StateTransitionEdge.class, build(PropertyName.MIDDLE_LABEL, "edge2"));

		assertSame(self1.getStart(), node1);
		assertSame(self1.getEnd(), node1);
		assertSame(self2.getStart(), node1);
		assertSame(self2.getEnd(), node1);
		assertSame(edge1.getStart(), node1);
		assertSame(edge1.getEnd(), node2);
		assertSame(edge2.getStart(), node1);
		assertSame(edge2.getEnd(), node2);
	}
}
