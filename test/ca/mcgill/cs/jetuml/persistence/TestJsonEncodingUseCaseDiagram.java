package ca.mcgill.cs.jetuml.persistence;

import static ca.mcgill.cs.jetuml.persistence.PersistenceTestUtils.assertHasKeys;
import static ca.mcgill.cs.jetuml.persistence.PersistenceTestUtils.build;
import static ca.mcgill.cs.jetuml.persistence.PersistenceTestUtils.find;
import static ca.mcgill.cs.jetuml.persistence.PersistenceTestUtils.findEdge;
import static ca.mcgill.cs.jetuml.persistence.PersistenceTestUtils.findRootNode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.jetuml.application.MultiLineString;
import ca.mcgill.cs.jetuml.diagrams.UseCaseDiagramGraph;
import ca.mcgill.cs.jetuml.graph.edges.UseCaseAssociationEdge;
import ca.mcgill.cs.jetuml.graph.edges.UseCaseDependencyEdge;
import ca.mcgill.cs.jetuml.graph.nodes.ActorNode;
import ca.mcgill.cs.jetuml.graph.nodes.NoteNode;
import ca.mcgill.cs.jetuml.graph.nodes.UseCaseNode;

public class TestJsonEncodingUseCaseDiagram
{
	private UseCaseDiagramGraph aGraph;
	
	@Before
	public void setup()
	{
		aGraph = new UseCaseDiagramGraph();
	}
	
	/*
	 * Initializes a simple graph with an actor and a use case node.
	 */
	private void initiGraph1()
	{
		ActorNode actor = new ActorNode();
		actor.getName().setText("Mr. Bob");
		UseCaseNode useCase = new UseCaseNode();
		useCase.getName().setText("Do it");
		
		aGraph.restoreRootNode(actor);
		aGraph.restoreRootNode(useCase);
		
		UseCaseAssociationEdge edge = new UseCaseAssociationEdge();
		aGraph.restoreEdge(edge, actor, useCase);
	}
	
	/*
	 * Initializes a graph with two use cases
	 * with a extension dependency between them.
	 */
	private void initiGraph2()
	{
		UseCaseNode node1 = new UseCaseNode();
		node1.getName().setText("Node1");
		UseCaseNode node2 = new UseCaseNode();
		node2.getName().setText("Node2");
		
		aGraph.restoreRootNode(node1);
		aGraph.restoreRootNode(node2);
		
		UseCaseDependencyEdge edge = new UseCaseDependencyEdge();
		edge.setType(UseCaseDependencyEdge.Type.Extend);
		aGraph.restoreEdge(edge, node1, node2);
	}
	
	@Test
	public void testEmpty()
	{
		JSONObject object = JsonEncoder.encode(aGraph);
		assertHasKeys(object, "diagram", "nodes", "edges", "version");
		assertEquals("UseCaseDiagramGraph", object.getString("diagram"));
		assertEquals(0, object.getJSONArray("nodes").length());	
		assertEquals(0, object.getJSONArray("edges").length());				
	}
	
	@Test
	public void testSingleNode()
	{
		aGraph.restoreRootNode(new NoteNode());
		
		JSONObject object = JsonEncoder.encode(aGraph);
		assertHasKeys(object, "diagram", "nodes", "edges", "version");
		assertEquals("UseCaseDiagramGraph", object.getString("diagram"));
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
		assertEquals("UseCaseDiagramGraph", object.getString("diagram"));
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
		UseCaseDiagramGraph graph = (UseCaseDiagramGraph) JsonDecoder.decode(JsonEncoder.encode(aGraph));
		
		ActorNode actor = (ActorNode) findRootNode(graph, ActorNode.class, build("name", new MultiLineString("Mr. Bob")));
		UseCaseNode useCase = (UseCaseNode) findRootNode(graph, UseCaseNode.class, build("name", new MultiLineString("Do it")));
		UseCaseAssociationEdge edge = (UseCaseAssociationEdge) findEdge(graph, UseCaseAssociationEdge.class, build());
		
		assertSame(edge.getStart(), actor);
		assertSame(edge.getEnd(), useCase);
	}
	
	@Test
	public void testEncodeDecodeGraph2()
	{
		initiGraph2();
		UseCaseDiagramGraph graph = (UseCaseDiagramGraph) JsonDecoder.decode(JsonEncoder.encode(aGraph));
		
		UseCaseNode node1 = (UseCaseNode) findRootNode(graph, UseCaseNode.class, build("name", new MultiLineString("Node1")));
		UseCaseNode node2 = (UseCaseNode) findRootNode(graph, UseCaseNode.class, build("name", new MultiLineString("Node2")));

		UseCaseDependencyEdge edge = (UseCaseDependencyEdge) findEdge(graph, UseCaseDependencyEdge.class, build("dependencyType", UseCaseDependencyEdge.Type.Extend));
		
		assertSame(edge.getStart(), node1);
		assertSame(edge.getEnd(), node2);
	}
	

}
