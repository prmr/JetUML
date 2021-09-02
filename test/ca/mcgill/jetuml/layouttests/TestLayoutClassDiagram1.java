package ca.mcgill.jetuml.layouttests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.PropertyName;
import ca.mcgill.cs.jetuml.diagram.nodes.NamedNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.persistence.PersistenceService;
import ca.mcgill.cs.jetuml.persistence.PersistenceTestUtils;
import ca.mcgill.cs.jetuml.viewers.nodes.NodeViewerRegistry;

/*
 * This class tests that the layout of a manually-created diagram file corresponds to expectations.
 */
public class TestLayoutClassDiagram1
{
	private static final Path PATH = Path.of("testdata", "testPersistenceService.class.jet");

	private final Diagram aDiagram; 

	public TestLayoutClassDiagram1() throws IOException
	{
		aDiagram = PersistenceService.read(PATH.toFile()).diagram();
	}
	 
	/*
	 * Tests that nodes are in the position that corresponds to their position value 
	 * in the file. We don't test Node7 because its position is calculated from its children
	 */
	@ParameterizedTest
	@CsvSource({"Node1, 200, 10",
				"Node2, 30, 130",
				"Node3, 200, 130",
				"Node4, 370, 130",
				"Node5, 200, 280",
				"Node6, 440, 290"})
	void testNamedNodePosition(String pNodeName, int pExpectedX, int pExpectedY)
	{
		verifyPosition(nodeByName(pNodeName), pExpectedX, pExpectedY);
	}
	
	/*
	 * Tests that all class nodes that are supposed to have the default dimension
	 * actually do. 
	 */
	@ParameterizedTest
	@ValueSource(strings = {"Node1", "Node2", "Node3", "Node4"})
	void testClassNodesDefaultDimension(String pNodeName)
	{
		final int DEFAULT_WIDTH = 100;
		final int DEFAULT_HEIGHT = 60;
		Rectangle bounds = NodeViewerRegistry.getBounds(nodeByName(pNodeName));
		assertEquals(DEFAULT_WIDTH, bounds.getWidth());
		assertEquals(DEFAULT_HEIGHT, bounds.getHeight());
	}
	
	/*
	 * Tests that the note node has the default dimensions. 
	 */
	@Test
	void testNoteNodeDefaultDimension()
	{
		final int DEFAULT_WIDTH = 60;
		final int DEFAULT_HEIGHT = 40;
		Rectangle bounds = NodeViewerRegistry.getBounds(nodeByName("Node6"));
		assertEquals(DEFAULT_WIDTH, bounds.getWidth());
		assertEquals(DEFAULT_HEIGHT, bounds.getHeight());
	}
	
	/*
	 * Tests that Node5 is longer that the default height 
	 */
	@Test
	void testNode5IsExpanded()
	{
		final int DEFAULT_HEIGHT = 60;
		Rectangle bounds = NodeViewerRegistry.getBounds(nodeByName("Node5"));
		assertTrue(bounds.getHeight() > DEFAULT_HEIGHT);
	}
	
	/*
	 * Tests that the bounds of the package node are outside of the bounds
	 * of its child Node2
	 */
	@Test
	void testPackageNodeContainment()
	{
		Rectangle boundsNode2 = NodeViewerRegistry.getBounds(nodeByName("Node2"));
		Rectangle boundsNode7 = NodeViewerRegistry.getBounds(nodesByType(PackageNode.class).get(0));
		assertEquals(boundsNode2.getX()-10, boundsNode7.getX());
		assertEquals(boundsNode2.getMaxX()+10, boundsNode7.getMaxX());
		assertEquals(boundsNode2.getMaxY() + 10, boundsNode7.getMaxY());
		assertTrue(boundsNode7.getY() < boundsNode2.getY());
	}
	
	/*
	 * Returns a named node with the matching name
	 */
	private Node nodeByName(String pName)
	{
		return PersistenceTestUtils.getAllNodes(aDiagram).stream()
			.filter(node -> node instanceof NamedNode )
			.filter( node -> node.properties().get(PropertyName.NAME).get().equals(pName))
			.findFirst()
			.get();
	}
	
	/*
	 * Returns all the nodes of a certain type
	 */
	private List<Node> nodesByType(Class<?> pType)
	{
		return PersistenceTestUtils.getAllNodes(aDiagram).stream()
				.filter(node -> node.getClass() == pType)
				.collect(Collectors.toUnmodifiableList());
	}
	
	private static void verifyPosition(Node pNode, int pExpectedX, int pExpectedY)
	{
		assertEquals(pExpectedX, pNode.position().getX());
		assertEquals(pExpectedY, pNode.position().getY());
	}

}
